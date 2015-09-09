package io.yawp.repository.driver.appengine;

import io.yawp.commons.http.HttpVerb;
import io.yawp.commons.utils.EntityUtils;
import io.yawp.commons.utils.FieldModel;
import io.yawp.commons.utils.JsonUtils;
import io.yawp.commons.utils.ObjectHolder;
import io.yawp.commons.utils.ObjectModel;
import io.yawp.repository.IdRef;
import io.yawp.repository.Repository;
import io.yawp.repository.driver.api.QueryDriver;
import io.yawp.repository.query.DatastoreQueryOrder;
import io.yawp.repository.query.QueryBuilder;
import io.yawp.repository.query.condition.BaseCondition;
import io.yawp.repository.query.condition.FalsePredicateException;
import io.yawp.repository.query.condition.JoinedCondition;
import io.yawp.repository.query.condition.LogicalOperator;
import io.yawp.repository.query.condition.SimpleCondition;
import io.yawp.repository.query.condition.WhereOperator;

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
import com.google.appengine.api.datastore.Query.FilterPredicate;
import com.google.appengine.api.datastore.QueryResultList;
import com.google.appengine.api.datastore.Text;

public class AppengineQueryDriver implements QueryDriver {

	private Repository r;

	public AppengineQueryDriver(Repository r) {
		this.r = r;
	}

	private DatastoreService datastore() {
		return DatastoreServiceFactory.getDatastoreService();
	}

	@SuppressWarnings("unchecked")
	@Override
	public <T> List<T> objects(QueryBuilder<?> builder) throws FalsePredicateException {
		QueryResultList<Entity> queryResult = generateResults(builder, false);

		List<T> objects = new ArrayList<T>();

		for (Entity entity : queryResult) {
			objects.add((T) toObject(builder.getModel(), entity));
		}

		return objects;
	}

	@SuppressWarnings("unchecked")
	@Override
	public <T> List<IdRef<T>> ids(QueryBuilder<?> builder) throws FalsePredicateException {
		QueryResultList<Entity> queryResult = generateResults(builder, true);
		List<IdRef<T>> ids = new ArrayList<>();

		for (Entity entity : queryResult) {
			ids.add((IdRef<T>) extractIdRef(entity));
		}

		return ids;
	}

	@SuppressWarnings("unchecked")
	@Override
	public <T> T fetch(IdRef<T> id) {
		try {
			Key key = id.asKey();
			Entity entity = datastore().get(key);
			return (T) toObject(id.getModel(), entity);
		} catch (EntityNotFoundException e) {
			return null;
		}
	}

	private QueryResultList<Entity> generateResults(QueryBuilder<?> builder, boolean keysOnly) throws FalsePredicateException {
		QueryResultList<Entity> queryResult = prepareQuery(builder, keysOnly).asQueryResultList(configureFetchOptions(builder));
		setCursor(builder, queryResult);
		return queryResult;
	}

	private FetchOptions configureFetchOptions(QueryBuilder<?> builder) {
		FetchOptions fetchOptions = FetchOptions.Builder.withDefaults();

		if (builder.getLimit() != null) {
			fetchOptions.limit(builder.getLimit());
		}
		if (builder.getCursor() != null) {
			fetchOptions.startCursor(Cursor.fromWebSafeString(builder.getCursor()));
		}
		return fetchOptions;
	}

	private void setCursor(QueryBuilder<?> builder, QueryResultList<Entity> queryResult) {
		if (queryResult.getCursor() != null) {
			builder.setCursor(queryResult.getCursor().toWebSafeString());
		}
	}

	private PreparedQuery prepareQuery(QueryBuilder<?> builder, boolean keysOnly) throws FalsePredicateException {
		Query q = new Query(builder.getModel().getKind());

		if (keysOnly) {
			q.setKeysOnly();
		}

		prepareQueryAncestor(builder, q);
		prepareQueryWhere(builder, q);
		prepareQueryOrder(builder, q);

		return r.datastore().prepare(q);
	}

	private void prepareQueryOrder(QueryBuilder<?> builder, Query q) {
		if (builder.getPreOrders().isEmpty()) {
			return;
		}

		for (DatastoreQueryOrder order : builder.getPreOrders()) {
			String string = EntityUtils.getActualFieldName(order.getProperty(), builder.getModel().getClazz());
			q.addSort(string, order.getSortDirection());
		}
	}

	private void prepareQueryWhere(QueryBuilder<?> builder, Query q) throws FalsePredicateException {
		BaseCondition condition = builder.getCondition();
		if (condition != null && condition.hasPreFilter()) {
			q.setFilter(createFilter(builder, condition));
		}
	}

	private void prepareQueryAncestor(QueryBuilder<?> builder, Query q) {
		IdRef<?> parentId = builder.getParentId();
		if (parentId == null) {
			return;
		}
		q.setAncestor(getKey(builder, parentId));
	}

	private Key getKey(QueryBuilder<?> builder, IdRef<?> parentId) {
		r.namespace().set(builder.getModel().getClazz());
		try {
			return parentId.asKey();
		} finally {
			r.namespace().reset();
		}
	}

	public Object toObject(ObjectModel model, Entity entity) {
		Object object = model.createInstance();

		ObjectHolder objectH = new ObjectHolder(object);
		objectH.setId(IdRef.fromKey(r, entity.getKey()));

		List<FieldModel> fieldModels = objectH.getModel().getFieldModels();

		for (FieldModel fieldModel : fieldModels) {
			if (fieldModel.isId()) {
				continue;
			}

			safeSetObjectProperty(entity, object, fieldModel);
		}

		return object;
	}

	private IdRef<?> extractIdRef(Entity entity) {
		return IdRef.fromKey(r, entity.getKey());
	}

	private <T> void safeSetObjectProperty(Entity entity, T object, FieldModel fieldModel) {
		try {
			setObjectProperty(object, entity, fieldModel, fieldModel.getField());
		} catch (IllegalAccessException e) {
			throw new RuntimeException(e);
		}
	}

	private <T> void setObjectProperty(T object, Entity entity, FieldModel fieldModel, Field field) throws IllegalAccessException {
		Object value = entity.getProperty(field.getName());

		if (value == null) {
			field.set(object, null);
			return;
		}

		if (fieldModel.isEnum()) {
			setEnumProperty(object, field, value);
			return;
		}

		if (fieldModel.isSaveAsJson()) {
			setJsonProperty(r, object, field, value);
			return;
		}

		if (fieldModel.isInt()) {
			setIntProperty(object, field, value);
			return;
		}

		if (fieldModel.isIdRef()) {
			setIdRefProperty(r, object, field, value);
			return;
		}

		if (fieldModel.isSaveAsText()) {
			setTextProperty(object, field, value);
			return;
		}

		field.set(object, value);
	}

	private <T> void setIdRefProperty(Repository r, T object, Field field, Object value) throws IllegalAccessException {
		field.set(object, IdRef.parse(r, HttpVerb.GET, (String) value));
	}

	private <T> void setIntProperty(T object, Field field, Object value) throws IllegalAccessException {
		field.set(object, ((Long) value).intValue());
	}

	private <T> void setTextProperty(T object, Field field, Object value) throws IllegalAccessException {
		field.set(object, ((Text) value).getValue());
	}

	private <T> void setJsonProperty(Repository r, T object, Field field, Object value) throws IllegalAccessException {
		String json = ((Text) value).getValue();
		field.set(object, JsonUtils.from(r, json, field.getGenericType()));
	}

	@SuppressWarnings({ "unchecked", "rawtypes" })
	private <T> void setEnumProperty(T object, Field field, Object value) throws IllegalAccessException {
		field.set(object, Enum.valueOf((Class) field.getType(), value.toString()));
	}

	private Filter createFilter(QueryBuilder<?> builder, BaseCondition condition) throws FalsePredicateException {
		if (condition instanceof SimpleCondition) {
			return createSimpleFilter(builder, (SimpleCondition) condition);
		}
		if (condition instanceof JoinedCondition) {
			return createJoinedFilter(builder, (JoinedCondition) condition);
		}
		throw new RuntimeException("Invalid condition class: " + condition.getClass());
	}

	private Filter createSimpleFilter(QueryBuilder<?> builder, SimpleCondition condition) throws FalsePredicateException {
		String field = condition.getField();
		Class<?> clazz = builder.getModel().getClazz();
		Object whereValue = condition.getWhereValue();
		WhereOperator whereOperator = condition.getWhereOperator();

		String actualFieldName = EntityUtils.getActualFieldName(field, clazz);
		Object actualValue = EntityUtils.getActualFieldValue(field, clazz, whereValue);

		if (whereOperator == WhereOperator.IN && EntityUtils.listSize(whereValue) == 0) {
			throw new FalsePredicateException();
		}

		return new FilterPredicate(actualFieldName, whereOperator.getFilterOperator(), actualValue);
	}

	private Filter createJoinedFilter(QueryBuilder<?> builder, JoinedCondition joinedCondition) throws FalsePredicateException {
		BaseCondition[] conditions = joinedCondition.getConditions();
		LogicalOperator logicalOperator = joinedCondition.getLogicalOperator();

		List<Filter> filters = new ArrayList<>();
		for (int i = 0; i < conditions.length; i++) {
			try {
				BaseCondition condition = conditions[i];
				if (!condition.hasPreFilter()) {
					continue;
				}

				filters.add(createFilter(builder, condition));
			} catch (FalsePredicateException e) {
				if (logicalOperator == LogicalOperator.AND) {
					throw e;
				}
			}
		}

		if (filters.isEmpty()) {
			throw new FalsePredicateException();
		}

		if (filters.size() == 1) {
			return filters.get(0);
		}

		Filter[] filtersArray = filters.toArray(new Filter[filters.size()]);

		if (logicalOperator == LogicalOperator.AND) {
			return CompositeFilterOperator.and(filtersArray);
		}
		if (logicalOperator == LogicalOperator.OR) {
			return CompositeFilterOperator.or(filtersArray);
		}

		throw new RuntimeException("Invalid logical operator: " + logicalOperator);
	}

}
