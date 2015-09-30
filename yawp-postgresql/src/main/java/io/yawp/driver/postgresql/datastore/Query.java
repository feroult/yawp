package io.yawp.driver.postgresql.datastore;

import io.yawp.commons.utils.DateUtils;
import io.yawp.commons.utils.ReflectionUtils;
import io.yawp.driver.postgresql.IdRefToKey;
import io.yawp.driver.postgresql.sql.SqlRunner;
import io.yawp.repository.FieldModel;
import io.yawp.repository.IdRef;
import io.yawp.repository.Repository;
import io.yawp.repository.query.QueryBuilder;
import io.yawp.repository.query.QueryOrder;
import io.yawp.repository.query.condition.BaseCondition;
import io.yawp.repository.query.condition.JoinedCondition;
import io.yawp.repository.query.condition.LogicalOperator;
import io.yawp.repository.query.condition.SimpleCondition;
import io.yawp.repository.query.condition.WhereOperator;

import java.lang.reflect.Array;
import java.lang.reflect.Field;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;

public class Query {

	private static final String SQL_PREFIX = "select key, properties from :kind where ";

	private Repository r;

	private QueryBuilder<?> builder;

	private Map<String, Object> whereBinds = new HashMap<String, Object>();

	public Query(QueryBuilder<?> builder) {
		this.builder = builder;
		this.r = builder.getRepository();
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

		String sql = SQL_PREFIX + where(builder, builder.getCondition());

		return new DatastoreSqlRunner(getKind(), sql) {
			@Override
			protected void bind() {
				for (String key : whereBinds.keySet()) {
					bind(key, whereBinds.get(key));
				}
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

	// filter

	private final String NORMALIZED_FIELD_PREFIX = "__";

	private String where(QueryBuilder<?> builder, BaseCondition condition) throws FalsePredicateException {
		if (condition instanceof SimpleCondition) {
			return simpleWhere(builder, (SimpleCondition) condition);
		}
		if (condition instanceof JoinedCondition) {
			return joinedWhere(builder, (JoinedCondition) condition);
		}
		throw new RuntimeException("Invalid condition class: " + condition.getClass());
	}

	private String simpleWhere(QueryBuilder<?> builder, SimpleCondition condition) throws FalsePredicateException {
		String field = condition.getField();
		Class<?> clazz = builder.getModel().getClazz();
		Object whereValue = condition.getWhereValue();
		WhereOperator whereOperator = condition.getWhereOperator();

		String actualFieldName = getActualFieldName(field, clazz);
		Object actualValue = getActualFieldValue(field, clazz, whereValue);

		if (whereOperator == WhereOperator.IN && listSize(whereValue) == 0) {
			throw new FalsePredicateException();
		}

		String placeHolder = "p" + (whereBinds.size() + 1);
		String where = String.format("properties->>'%s' %s :%s", actualFieldName, filterOperatorAsText(whereOperator), placeHolder);
		whereBinds.put(placeHolder, actualValue);
		return where;
	}

	private String joinedWhere(QueryBuilder<?> builder, JoinedCondition joinedCondition) throws FalsePredicateException {
		BaseCondition[] conditions = joinedCondition.getConditions();
		LogicalOperator logicalOperator = joinedCondition.getLogicalOperator();

		List<String> wheres = new ArrayList<>();
		for (int i = 0; i < conditions.length; i++) {
			try {
				BaseCondition condition = conditions[i];
				if (!condition.hasPreFilter()) {
					continue;
				}

				wheres.add(where(builder, condition));
			} catch (FalsePredicateException e) {
				if (logicalOperator == LogicalOperator.AND) {
					throw e;
				}
			}
		}

		if (wheres.isEmpty()) {
			throw new FalsePredicateException();
		}

		if (wheres.size() == 1) {
			return wheres.get(0);
		}

		return applyLogicalOperator(logicalOperator, wheres);
	}

	private String applyLogicalOperator(LogicalOperator logicalOperator, List<String> wheres) {
		boolean first = true;
		String operator = logicalOperatorAsText(logicalOperator);

		StringBuilder sb = new StringBuilder();

		sb.append("(");
		for (String where : wheres) {
			if (!first) {
				sb.append(operator);
			} else {
				first = false;
			}
			sb.append(where);
		}
		sb.append(")");

		return sb.toString();
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
		return IdRefToKey.toKey(r, id);
	}

	private String logicalOperatorAsText(LogicalOperator logicalOperator) {
		String operator = null;
		if (logicalOperator == LogicalOperator.AND) {
			operator = " and ";
		} else if (logicalOperator == LogicalOperator.OR) {
			operator = " or ";
		} else {
			throw new RuntimeException("Invalid logical operator: " + logicalOperator);
		}
		return operator;
	}

	private String filterOperatorAsText(WhereOperator whereOperator) {
		switch (whereOperator) {
		case EQUAL:
			return "=";
		case GREATER_THAN:
			return ">";
		case GREATER_THAN_OR_EQUAL:
			return ">=";
		case IN:
			return "in";
		case LESS_THAN:
			return "<";
		case LESS_THAN_OR_EQUAL:
			return "<=";
		case NOT_EQUAL:
			return "<>";
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
