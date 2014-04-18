package endpoint;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;

import com.google.appengine.api.datastore.Cursor;
import com.google.appengine.api.datastore.DatastoreService;
import com.google.appengine.api.datastore.DatastoreServiceFactory;
import com.google.appengine.api.datastore.Entity;
import com.google.appengine.api.datastore.EntityNotFoundException;
import com.google.appengine.api.datastore.FetchOptions;
import com.google.appengine.api.datastore.Key;
import com.google.appengine.api.datastore.PreparedQuery;
import com.google.appengine.api.datastore.Query;
import com.google.appengine.api.datastore.Query.CompositeFilterOperator;
import com.google.appengine.api.datastore.Query.Filter;
import com.google.appengine.api.datastore.Query.FilterOperator;
import com.google.appengine.api.datastore.Query.FilterPredicate;
import com.google.appengine.api.datastore.Query.SortDirection;
import com.google.appengine.api.datastore.QueryResultList;

import endpoint.utils.EntityUtils;

public class DatastoreQuery<T> {

	private Class<T> clazz;

	private Repository r;

	private Key parentKey;

	private Object[] where;

	private String[] order;

	private Integer limit;

	private String cursor;

	public static <T> DatastoreQuery<T> q(Class<T> clazz, Repository r) {
		return new DatastoreQuery<T>(clazz, r);
	}

	private DatastoreQuery(Class<T> clazz, Repository r) {
		this.clazz = clazz;
		this.r = r;
	}

	public DatastoreQuery<T> where(Object... values) {
		this.where = values;
		return this;
	}

	public DatastoreQuery<T> parent(Key parentKey) {
		this.parentKey = parentKey;
		return this;
	}

	public DatastoreQuery<T> order(String... values) {
		this.order = values;
		return this;
	}

	public DatastoreQuery<T> limit(int limit) {
		this.limit = limit;
		return this;
	}

	public DatastoreQuery<T> cursor(String cursor) {
		this.cursor = cursor;
		return this;
	}

	public String getCursor() {
		return this.cursor;
	}

	public DatastoreQuery<T> options(DatastoreQueryOptions options) {
		if (options.getWhere() != null) {
			where(options.getWhere());
		}

		if (options.getOrder() != null) {
			order(options.getOrder());
		}

		if (options.getLimit() != null) {
			limit(options.getLimit());
		}

		return this;
	}

	public DatastoreResultList<T> list() {
		r.namespace().set(getClazz());
		try {
			return new DatastoreResultList<T>(executeQuery());
		} finally {
			r.namespace().reset();
		}
	}

	public DatastoreResult<T> first() {
		r.namespace().set(getClazz());
		try {
			limit(1);

			List<T> list = executeQuery();
			if (list.size() == 0) {
				return new DatastoreResult<T>(r);
			}
			return new DatastoreResult<T>(r, list.get(0));
		} finally {
			r.namespace().reset();
		}
	}

	public DatastoreResult<T> id(Long id) {
		r.namespace().set(getClazz());
		try {
			DatastoreService datastoreService = DatastoreServiceFactory.getDatastoreService();

			try {
				Entity entity = datastoreService.get(EntityUtils.createKey(id, clazz));
				T object = EntityUtils.toObject(entity, clazz);
				loadLists(object);
				return new DatastoreResult<T>(r, object);
			} catch (EntityNotFoundException e) {
				return new DatastoreResult<T>(r);
			}
		} finally {
			r.namespace().reset();
		}
	}

	private void loadLists(Object object) {
		Field[] fields = EntityUtils.getFields(object.getClass());
		for (int i = 0; i < fields.length; i++) {
			Field field = fields[i];
			if (!EntityUtils.isSaveAsList(field)) {
				continue;
			}

			field.setAccessible(true);

			List<Object> list = new ArrayList<Object>();
			list.addAll(q(EntityUtils.getListClass(field), r).parent(EntityUtils.getKey(object)).list().now());

			try {
				field.set(object, list);
			} catch (Exception e) {
				throw new RuntimeException(e);
			}
		}
	}

	private List<T> executeQuery() {
		PreparedQuery pq = prepareQuery();
		FetchOptions fetchOptions = configureFetchOptions();

		QueryResultList<Entity> queryResult = pq.asQueryResultList(fetchOptions);

		List<T> objects = new ArrayList<T>();

		for (Entity entity : queryResult) {
			T object = EntityUtils.toObject(entity, clazz);
			objects.add(object);
		}

		this.cursor = queryResult.getCursor().toWebSafeString();
		return objects;
	}

	private FetchOptions configureFetchOptions() {
		FetchOptions fetchOptions = FetchOptions.Builder.withDefaults();

		if (limit != null) {
			fetchOptions.limit(limit);
		}
		if (cursor != null) {
			fetchOptions.startCursor(Cursor.fromWebSafeString(cursor));
		}
		return fetchOptions;
	}

	private PreparedQuery prepareQuery() {
		Query q = new Query(EntityUtils.getKind(clazz));

		prepareQueryAncestor(q);
		prepareQueryWhere(q);
		prepareQueryOrder(q);

		DatastoreService service = DatastoreServiceFactory.getDatastoreService();
		return service.prepare(q);
	}

	private void prepareQueryOrder(Query q) {
		if (order == null) {
			return;
		}
		String string = EntityUtils.getIndexFieldName(order[0], clazz);
		q.addSort(string, getSortDirection(order[1]));
	}

	private void prepareQueryWhere(Query q) {
		if (where == null) {
			return;
		}
		List<Filter> filters = new ArrayList<Filter>();

		int i = 0;
		while (i < where.length) {
			String fieldName = (String) where[i + 0];
			String indexFieldName = EntityUtils.getIndexFieldName(fieldName, clazz);
			Object value = EntityUtils.getIndexFieldValue(fieldName, clazz, where[i + 2]);
			filters.add(new FilterPredicate(indexFieldName, getFilterOperator(where[i + 1]), value));
			i += 3;
		}

		if (filters.size() > 1) {
			q.setFilter(CompositeFilterOperator.and(filters));
		} else {
			q.setFilter(filters.get(0));
		}
	}

	private void prepareQueryAncestor(Query q) {
		if (parentKey == null) {
			return;
		}
		q.setAncestor(parentKey);
	}

	private SortDirection getSortDirection(String order) {
		if (order.equalsIgnoreCase("desc")) {
			return SortDirection.DESCENDING;
		}
		if (order.equalsIgnoreCase("asc")) {
			return SortDirection.ASCENDING;
		}
		throw new RuntimeException("invalid sort direction");
	}

	protected Class<T> getClazz() {
		return clazz;
	}

	private FilterOperator getFilterOperator(Object o) {
		String operator = (String) o;

		if (operator.equals("=")) {
			return FilterOperator.EQUAL;
		}
		if (operator.equals(">")) {
			return FilterOperator.GREATER_THAN;
		}
		if (operator.equals(">=")) {
			return FilterOperator.GREATER_THAN_OR_EQUAL;
		}
		if (operator.equalsIgnoreCase("in")) {
			return FilterOperator.IN;
		}
		if (operator.equals("<")) {
			return FilterOperator.LESS_THAN;
		}
		if (operator.equals("<=")) {
			return FilterOperator.LESS_THAN_OR_EQUAL;
		}
		if (operator.equals("!=")) {
			return FilterOperator.NOT_EQUAL;
		}
		throw new RuntimeException("invalid filter operator");
	}
}
