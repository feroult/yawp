package io.yawp.driver.postgresql.datastore.query;

import io.yawp.commons.utils.DateUtils;
import io.yawp.commons.utils.ReflectionUtils;
import io.yawp.driver.postgresql.IdRefToKey;
import io.yawp.driver.postgresql.datastore.DatastoreSqlRunner;
import io.yawp.driver.postgresql.datastore.Entity;
import io.yawp.driver.postgresql.datastore.Key;
import io.yawp.driver.postgresql.sql.SqlRunner;
import io.yawp.repository.FieldModel;
import io.yawp.repository.IdRef;
import io.yawp.repository.query.QueryBuilder;
import io.yawp.repository.query.QueryOrder;
import io.yawp.repository.query.condition.BaseCondition;
import io.yawp.repository.query.condition.SimpleCondition;
import io.yawp.repository.query.condition.WhereOperator;

import java.lang.reflect.Array;
import java.lang.reflect.Field;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;

import org.apache.commons.lang3.StringUtils;

public class Query {

	private static final String SQL_PREFIX = "select key, properties from :kind where ";

	private QueryBuilder<?> builder;

	public Query(QueryBuilder<?> builder) {
		this.builder = builder;
	}

	public void setKeysOnly() {
		// TODO Auto-generated method stub

	}

	public void setAncestor(Key key) {
		// TODO Auto-generated method stub

	}

	public void addSort(String string, QueryOrder order) {
		// TODO Auto-generated method stub

	}

	public SqlRunner createRunner() throws FalsePredicateException {

		final Filter filter = createFilter(builder, builder.getCondition());

		String sql = SQL_PREFIX + filter.getWhereCaluse();

		return new DatastoreSqlRunner(getKind(), createSql()) {
			@Override
			protected void bind() {
				bind("name", "jim");
			}

			@Override
			protected Object collect(ResultSet rs) throws SQLException {
				return getEntities(rs);
			}

		};
	}

	private String getKind() {
		return builder.getModel().getKind();
	}

	private String createSql() {
		return SQL_PREFIX + where();
	}

	private String where() {
		return "properties->>'name' = :name";
	}

	// filter

	private final String NORMALIZED_FIELD_PREFIX = "__";

	private Filter createFilter(QueryBuilder<?> builder, BaseCondition condition) throws FalsePredicateException {
		if (condition instanceof SimpleCondition) {
			return createSimpleFilter(builder, (SimpleCondition) condition);
		}
		// if (condition instanceof JoinedCondition) {
		// return createJoinedFilter(builder, (JoinedCondition) condition);
		// }
		throw new RuntimeException("Invalid condition class: " + condition.getClass());
	}

	private Filter createSimpleFilter(QueryBuilder<?> builder, SimpleCondition condition) throws FalsePredicateException {
		String field = condition.getField();
		Class<?> clazz = builder.getModel().getClazz();
		Object whereValue = condition.getWhereValue();
		WhereOperator whereOperator = condition.getWhereOperator();

		String actualFieldName = getActualFieldName(field, clazz);
		Object actualValue = getActualFieldValue(field, clazz, whereValue);

		if (whereOperator == WhereOperator.IN && listSize(whereValue) == 0) {
			throw new FalsePredicateException();
		}

		return new FilterPredicate(actualFieldName, getFilterOperator(whereOperator), actualValue);
	}

	private <T> String getActualFieldName(String fieldName, Class<T> clazz) {
		Field field = ReflectionUtils.getFieldRecursively(clazz, fieldName);
		FieldModel fieldModel = new FieldModel(field);

		if (fieldModel.isId()) {
			return Entity.KEY_RESERVED_PROPERTY;
		}

		if (fieldModel.isIndexNormalizable()) {
			return NORMALIZED_FIELD_PREFIX + fieldName;
		}

		return fieldName;
	}

	public <T> Object getActualFieldValue(String fieldName, Class<T> clazz, Object value) {
		Field field = ReflectionUtils.getFieldRecursively(clazz, fieldName);
		FieldModel fieldModel = new FieldModel(field);

		if (fieldModel.isCollection(value)) {
			return getActualListFieldValue(fieldName, clazz, (Collection<?>) value);
		}

		if (fieldModel.isId()) {
			return getActualKeyFieldValue(clazz, value);
		}

		if (fieldModel.isEnum(value)) {
			return value.toString();
		}

		if (fieldModel.isIndexNormalizable()) {
			return normalizeValue(value);
		}

		if (value instanceof IdRef) {
			return ((IdRef<?>) value).getUri();
		}

		if (fieldModel.isDate() && value instanceof String) {
			return DateUtils.toTimestamp((String) value);
		}

		return value;
	}

	private <T> Object getActualListFieldValue(String fieldName, Class<T> clazz, Collection<?> value) {
		Collection<?> objects = (Collection<?>) value;
		List<Object> values = new ArrayList<>();
		for (Object obj : objects) {
			values.add(getActualFieldValue(fieldName, clazz, obj));
		}
		return values;
	}

	private <T> Key getActualKeyFieldValue(Class<T> clazz, Object value) {
		IdRef<?> id = (IdRef<?>) value;
		return IdRefToKey.toKey(null, id);
	}

	private FilterOperator getFilterOperator(WhereOperator whereOperator) {
		switch (whereOperator) {
		case EQUAL:
			return FilterOperator.EQUAL;
		case GREATER_THAN:
			return FilterOperator.GREATER_THAN;
		case GREATER_THAN_OR_EQUAL:
			return FilterOperator.GREATER_THAN_OR_EQUAL;
		case IN:
			return FilterOperator.IN;
		case LESS_THAN:
			return FilterOperator.LESS_THAN;
		case LESS_THAN_OR_EQUAL:
			return FilterOperator.LESS_THAN_OR_EQUAL;
		case NOT_EQUAL:
			return FilterOperator.NOT_EQUAL;
		default:
			throw new RuntimeException("Invalid where operator: " + whereOperator);
		}
	}

	private Object normalizeValue(Object o) {
		if (o == null) {
			return null;
		}

		if (!o.getClass().equals(String.class)) {
			return o;
		}

		return StringUtils.stripAccents((String) o).toLowerCase();
	}

	public int listSize(Object value) {
		if (value == null) {
			return 0;
		}
		if (value.getClass().isArray()) {
			return Array.getLength(value);
		}
		if (Collection.class.isAssignableFrom(value.getClass())) {
			return Collection.class.cast(value).size();
		}
		if (Iterable.class.isAssignableFrom(value.getClass())) {
			return iterableSize(value);
		}
		throw new RuntimeException("Value used with operator 'in' is not an array or list.");
	}

	private int iterableSize(Object value) {
		Iterator<?> it = Iterable.class.cast(value).iterator();
		int i = 0;
		while (it.hasNext()) {
			it.next();
			i++;
		}
		return i;
	}
}
