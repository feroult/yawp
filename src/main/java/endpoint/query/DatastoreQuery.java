package endpoint.query;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
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
import com.google.appengine.api.datastore.Query.FilterOperator;
import com.google.appengine.api.datastore.QueryResultList;

import endpoint.IdRef;
import endpoint.Repository;
import endpoint.query.Condition.SimpleCondition;
import endpoint.utils.EntityUtils;

public class DatastoreQuery<T> {

	private Class<T> clazz;

	private Repository r;

	private Key parentKey;

	private Condition condition;

	private List<DatastoreQueryOrder> preOrders = new ArrayList<DatastoreQueryOrder>();

	private List<DatastoreQueryOrder> postOrders = new ArrayList<DatastoreQueryOrder>();

	private Integer limit;

	private String cursor;

	public static <T> DatastoreQuery<T> q(Class<T> clazz, Repository r) {
		return new DatastoreQuery<T>(clazz, r);
	}

	private DatastoreQuery(Class<T> clazz, Repository r) {
		this.clazz = clazz;
		this.r = r;
	}

	public DatastoreQueryTransformer<?> transform(String transformName) {
		return new DatastoreQueryTransformer<Object>(this, Object.class, transformName);
	}

	public <TT> DatastoreQueryTransformer<TT> transform(Class<TT> transformClazz, String transformName) {
		return new DatastoreQueryTransformer<TT>(this, transformClazz, transformName);
	}

	@Deprecated
	public DatastoreQuery<T> where(Object... values) {
		if (values.length % 3 != 0) {
			throw new RuntimeException("You must pass values 3 at a time.");
		}
		for (int i = 0; i < values.length; i += 3) {
			where(values[i].toString(), values[i + 1].toString(), values[i + 2]);
		}
		return this;
	}

	public DatastoreQuery<T> and(String field, String operator, Object value) {
		return where(field, operator, value);
	}

	public DatastoreQuery<T> where(String field, String operator, Object value) {
		return where(Condition.c(field, operator, value));
	}

	public DatastoreQuery<T> where(Condition c) {
		if (condition == null) {
			condition = c;
		} else {
			condition = Condition.and(condition, c);
		}
		return this;
	}

	public DatastoreQuery<T> parent(Long parentId, Class<?> parentClazz) {
		r.namespace().set(getClazz());
		try {
			Key createKey = EntityUtils.createKey(parentId, parentClazz);
			return parent(createKey);
		} finally {
			r.namespace().reset();
		}
	}

	public DatastoreQuery<T> parent(Key parentKey) {
		this.parentKey = parentKey;
		return this;
	}

	public DatastoreQuery<T> order(String property) {
		order(property, null);
		return this;
	}

	public DatastoreQuery<T> order(String property, String direction) {
		preOrders.add(new DatastoreQueryOrder(null, property, direction));
		return this;
	}

	public DatastoreQuery<T> sort(String property) {
		sort(property, null);
		return this;
	}

	public DatastoreQuery<T> sort(String property, String direction) {
		sort(null, property, direction);
		return this;
	}

	public DatastoreQuery<T> sort(String entity, String property, String direction) {
		postOrders.add(new DatastoreQueryOrder(entity, property, direction));
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

	public Repository getRepository() {
		return this.r;
	}

	public DatastoreQuery<T> options(DatastoreQueryOptions options) {
		if (options.getWhere() != null) {
			where(options.getWhere());
		}

		if (options.getCondition() != null) {
			where(options.getCondition());
		}

		if (options.getPreOrders() != null) {
			preOrders.addAll(options.getPreOrders());
		}

		if (options.getPostOrders() != null) {
			postOrders.addAll(options.getPostOrders());
		}

		if (options.getLimit() != null) {
			limit(options.getLimit());
		}

		return this;
	}

	public List<T> unsortedList() {
		r.namespace().set(getClazz());
		try {
			return executeQuery();
		} finally {
			r.namespace().reset();
		}
	}

	public List<T> list() {
		List<T> list = unsortedList();
		sortList(list);
		return list;
	}

	public T first() {
		r.namespace().set(getClazz());
		try {
			if (isQueryById()) {
				return executeQueryById();
			}
			return executeQueryFirst();
		} finally {
			r.namespace().reset();
		}
	}

	private T executeQueryFirst() {
		limit(1);

		List<T> list = executeQuery();
		if (list.size() == 0) {
			return null;
		}
		return list.get(0);
	}

	public T only() throws NoResultException, MoreThanOneResultException {
		r.namespace().set(getClazz());
		try {
			T object = null;

			if (isQueryById()) {
				object = executeQueryById();
			} else {
				object = executeQueryOnlyFirst();
			}

			if (object == null) {
				throw new NoResultException();
			}

			return object;
		} finally {
			r.namespace().reset();
		}
	}

	private T executeQueryOnlyFirst() throws MoreThanOneResultException {
		List<T> list = executeQuery();
		if (list.size() == 0) {
			return null;
		}
		if (list.size() == 1) {
			return list.get(0);
		}
		throw new MoreThanOneResultException();
	}

	private List<T> executeQuery() {
		PreparedQuery pq = prepareQuery();
		FetchOptions fetchOptions = configureFetchOptions();

		QueryResultList<Entity> queryResult = pq.asQueryResultList(fetchOptions);

		List<T> objects = new ArrayList<T>();

		for (Entity entity : queryResult) {
			T object = EntityUtils.toObject(r, entity, clazz);
			objects.add(object);
		}

		if (queryResult.getCursor() != null) {
			this.cursor = queryResult.getCursor().toWebSafeString();
		}
		return objects;
	}

	private T executeQueryById() {
		try {
			SimpleCondition c = (SimpleCondition) condition;
			Key key = EntityUtils.createKey((Long) c.getValue(), clazz);
			Entity entity = DatastoreServiceFactory.getDatastoreService().get(key);
			return EntityUtils.toObject(r, entity, clazz);
		} catch (EntityNotFoundException e) {
			return null;
		}
	}

	private boolean isQueryById() {
		if (condition == null || !(condition instanceof SimpleCondition)) {
			return false;
		}

		SimpleCondition c = (SimpleCondition) condition;
		return c.getField().equals(EntityUtils.getIdFieldName(clazz)) && c.getOperator().equals(FilterOperator.EQUAL);
	}

	public void sortList(List<?> objects) {
		Collections.sort(objects, new Comparator<Object>() {
			@Override
			public int compare(Object o1, Object o2) {
				for (DatastoreQueryOrder order : postOrders) {
					int compare = order.compare(o1, o2);

					if (compare == 0) {
						continue;
					}

					return compare;
				}
				return 0;
			}
		});
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
		if (preOrders.isEmpty()) {
			return;
		}

		for (DatastoreQueryOrder order : preOrders) {
			String string = EntityUtils.getActualFieldName(order.getProperty(), clazz);
			q.addSort(string, order.getSortDirection());
		}
	}

	private void prepareQueryWhere(Query q) {
		if (condition != null) {
			q.setFilter(condition.getPredicate(clazz));
		}
	}

	private void prepareQueryAncestor(Query q) {
		if (parentKey == null) {
			return;
		}
		q.setAncestor(parentKey);
	}

	protected Class<T> getClazz() {
		return clazz;
	}

	public DatastoreQuery<T> whereById(String operator, Long id) {
		return where(EntityUtils.getIdFieldName(clazz), operator, id);
	}

	public T id(Long id) {
		return whereById("=", id).only();
	}

	public T id(IdRef<?> idRef) {
		return id(idRef.asLong());
	}

}
