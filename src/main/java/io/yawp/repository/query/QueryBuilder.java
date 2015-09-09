package io.yawp.repository.query;

import io.yawp.commons.utils.EntityUtils;
import io.yawp.commons.utils.ObjectModel;
import io.yawp.commons.utils.kind.KindResolver;
import io.yawp.repository.IdRef;
import io.yawp.repository.Repository;
import io.yawp.repository.query.condition.BaseCondition;
import io.yawp.repository.query.condition.Condition;
import io.yawp.repository.query.condition.FalsePredicateException;
import io.yawp.repository.query.condition.SimpleCondition;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import com.google.appengine.api.datastore.Cursor;
import com.google.appengine.api.datastore.Entity;
import com.google.appengine.api.datastore.EntityNotFoundException;
import com.google.appengine.api.datastore.FetchOptions;
import com.google.appengine.api.datastore.Key;
import com.google.appengine.api.datastore.PreparedQuery;
import com.google.appengine.api.datastore.Query;
import com.google.appengine.api.datastore.QueryResultList;

public class QueryBuilder<T> {

	private Class<T> clazz;

	private ObjectModel model;

	private Repository r;

	private Key parentKey;

	private IdRef<?> parentId;

	private BaseCondition condition;

	private List<DatastoreQueryOrder> preOrders = new ArrayList<DatastoreQueryOrder>();

	private List<DatastoreQueryOrder> postOrders = new ArrayList<DatastoreQueryOrder>();

	private Integer limit;

	private String cursor;

	public static <T> QueryBuilder<T> q(Class<T> clazz, Repository r) {
		return new QueryBuilder<T>(clazz, r);
	}

	private QueryBuilder(Class<T> clazz, Repository r) {
		this.clazz = clazz;
		this.r = r;
		this.model = new ObjectModel(clazz);
	}

	public <N> DatastoreQueryTransformer<T, N> transform(String transformName) {
		return new DatastoreQueryTransformer<T, N>(this, transformName);
	}

	@Deprecated
	public QueryBuilder<T> where(Object... values) {
		if (values.length % 3 != 0) {
			throw new RuntimeException("You must pass values 3 at a time.");
		}
		for (int i = 0; i < values.length; i += 3) {
			where(values[i].toString(), values[i + 1].toString(), values[i + 2]);
		}
		return this;
	}

	public QueryBuilder<T> and(String field, String operator, Object value) {
		return where(field, operator, value);
	}

	public QueryBuilder<T> where(String field, String operator, Object value) {
		return where(Condition.c(field, operator, value));
	}

	public QueryBuilder<T> where(BaseCondition c) {
		if (condition == null) {
			condition = c;
		} else {
			condition = Condition.and(condition, c);
		}

		condition.init(r, clazz);
		return this;
	}

	public QueryBuilder<T> and(BaseCondition c) {
		return where(c);
	}

	// public QueryBuilder<T> from(IdRef<?> parentId) {
	// if (parentId == null) {
	// this.parentId = null;
	// return this;
	// }
	//
	// this.parentId = parentId;
	// return this;
	// }

	public QueryBuilder<T> from(IdRef<?> parentId) {
		if (parentId == null) {
			parentKey = null;
			this.parentId = null;
			return this;
		}

		r.namespace().set(getClazz());
		try {
			parentKey = parentId.asKey();
			this.parentId = parentId;
			return this;
		} finally {
			r.namespace().reset();
		}
	}

	public QueryBuilder<T> order(String property) {
		order(property, null);
		return this;
	}

	public QueryBuilder<T> order(String property, String direction) {
		preOrders.add(new DatastoreQueryOrder(null, property, direction));
		return this;
	}

	public QueryBuilder<T> sort(String property) {
		sort(property, null);
		return this;
	}

	public QueryBuilder<T> sort(String property, String direction) {
		sort(null, property, direction);
		return this;
	}

	public QueryBuilder<T> sort(String entity, String property, String direction) {
		postOrders.add(new DatastoreQueryOrder(entity, property, direction));
		return this;
	}

	public QueryBuilder<T> limit(int limit) {
		this.limit = limit;
		return this;
	}

	public QueryBuilder<T> cursor(String cursor) {
		this.cursor = cursor;
		return this;
	}

	public IdRef<?> getParentId() {
		return parentId;
	}

	public String getCursor() {
		return this.cursor;
	}

	public void setCursor(String cursor) {
		this.cursor = cursor;
	}

	public Integer getLimit() {
		return limit;
	}

	public List<DatastoreQueryOrder> getPreOrders() {
		return preOrders;
	}

	public BaseCondition getCondition() {
		return condition;
	}

	public Repository getRepository() {
		return this.r;
	}

	public ObjectModel getModel() {
		return model;
	}

	public QueryBuilder<T> options(DatastoreQueryOptions options) {
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

	public List<T> executeQueryList() {
		r.namespace().set(getClazz());
		try {
			return executeQuery();
		} finally {
			r.namespace().reset();
		}
	}

	public List<T> list() {
		List<T> list = executeQueryList();
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
				object = executeQueryOnly();
			}

			if (object == null) {
				throw new NoResultException();
			}

			return object;
		} finally {
			r.namespace().reset();
		}
	}

	private T executeQueryOnly() throws MoreThanOneResultException {
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
		try {

			List<T> objects = r.driver().query().execute(this);
			return postFilter(objects);

		} catch (FalsePredicateException ex) {
			return Collections.emptyList();
		}
	}

	private List<T> postFilter(List<T> objects) {
		if (condition == null || !condition.hasPostFilter()) {
			return objects;
		}

		return condition.applyPostFilter(objects);
	}

	private void setCursor(QueryResultList<Entity> queryResult) {
		if (queryResult.getCursor() != null) {
			this.cursor = queryResult.getCursor().toWebSafeString();
		}
	}

	private T executeQueryById() {
		try {
			SimpleCondition c = (SimpleCondition) condition;
			IdRef<?> idRef = (IdRef<?>) c.getWhereValue();
			Key key = idRef.asKey();
			Entity entity = r.datastore().get(key);
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
		return c.isIdField() && c.isEqualOperator();
	}

	public void sortList(List<?> objects) {
		if (postOrders.size() == 0) {
			return;
		}

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

	private PreparedQuery prepareQuery(boolean keysOnly) throws FalsePredicateException {
		Query q = new Query(KindResolver.getKindFromClass(clazz));

		if (keysOnly) {
			q.setKeysOnly();
		}

		prepareQueryAncestor(q);
		prepareQueryWhere(q);
		prepareQueryOrder(q);

		return r.datastore().prepare(q);
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

	private void prepareQueryWhere(Query q) throws FalsePredicateException {
		if (condition != null && condition.hasPreFilter()) {
			q.setFilter(condition.createPreFilter());
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

	public QueryBuilder<T> whereById(String operator, IdRef<?> id) {
		return from(id.getParentId()).where(model.getIdField().getName(), operator, id);
	}

	public T fetch(IdRef<?> idRef) {
		return whereById("=", idRef).only();
	}

	public T fetch(Long id) {
		IdRef<?> idRef = IdRef.create(r, clazz, id);
		return fetch(idRef);
	}

	public T fetch(String name) {
		IdRef<?> idRef = IdRef.create(r, clazz, name);
		return fetch(idRef);
	}

	public List<IdRef<T>> ids() {
		r.namespace().set(getClazz());
		try {
			QueryResultList<Entity> queryResult = generateResults(true);
			List<IdRef<T>> ids = new ArrayList<>();

			for (Entity entity : queryResult) {
				ids.add(extractIdRef(entity));
			}

			return ids;
		} catch (FalsePredicateException ex) {
			return Collections.emptyList();
		} finally {
			r.namespace().reset();
		}
	}

	private QueryResultList<Entity> generateResults(boolean keysOnly) throws FalsePredicateException {
		QueryResultList<Entity> queryResult = prepareQuery(keysOnly).asQueryResultList(configureFetchOptions());
		setCursor(queryResult);
		return queryResult;
	}

	@SuppressWarnings("unchecked")
	private IdRef<T> extractIdRef(Entity entity) {
		return (IdRef<T>) IdRef.fromKey(r, entity.getKey());
	}

	public IdRef<T> onlyId() throws NoResultException, MoreThanOneResultException {
		r.namespace().set(getClazz());
		try {
			Entity e = prepareQuery(true).asSingleEntity();
			if (e == null) {
				throw new NoResultException();
			}
			return extractIdRef(e);
		} catch (FalsePredicateException ex) {
			throw new NoResultException();
		} catch (PreparedQuery.TooManyResultsException ex) {
			throw new MoreThanOneResultException();
		} finally {
			r.namespace().reset();
		}
	}

}
