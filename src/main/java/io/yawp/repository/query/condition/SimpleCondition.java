package io.yawp.repository.query.condition;

import io.yawp.commons.utils.EntityUtils;
import io.yawp.commons.utils.ReflectionUtils;
import io.yawp.repository.Repository;

import java.util.Arrays;
import java.util.List;

import com.google.appengine.api.datastore.Query.Filter;
import com.google.appengine.api.datastore.Query.FilterOperator;
import com.google.appengine.api.datastore.Query.FilterPredicate;

public class SimpleCondition extends BaseCondition {

	private String field;
	private FilterOperator operator;
	private Object value;

	public SimpleCondition(String field, FilterOperator operator, Object value) {
		this.field = field;
		this.operator = operator;
		this.value = value;

		if (operator == FilterOperator.IN) {
			assertList(value);
		}
	}

	@Override
	public Class<?> getIdTypeFor(Class<?> clazz) {
		if (isFieldIdType(clazz)) {
			boolean isList = operator == FilterOperator.IN;
			if (isValidIdClass(value, isList)) {
				return value.getClass();
			} else {
				throw new RuntimeException("If you are searching by @Id, you can only use the valid @Id types classes: "
						+ Arrays.toString(VALID_ID_CLASSES) + ". Found " + value.getClass().getSimpleName());
			}
		}
		return null;
	}

	private boolean isFieldIdType(Class<?> clazz) {
		return this.field.equals(EntityUtils.getIdFieldName(clazz));
	}

	@Override
	public Filter getPredicate(Class<?> clazz) throws FalsePredicateException {
		String actualFieldName = EntityUtils.getActualFieldName(field, clazz);
		Object actualValue = EntityUtils.getActualFieldValue(field, clazz, value);

		if (operator == FilterOperator.IN && EntityUtils.listSize(value) == 0) {
			throw new FalsePredicateException();
		}

		return new FilterPredicate(actualFieldName, operator, actualValue);
	}

	public String getField() {
		return field;
	}

	public FilterOperator getOperator() {
		return operator;
	}

	public Object getValue() {
		return value;
	}

	@Override
	public void normalizeIdRefs(Class<?> clazz, Repository r) {
		if (isFieldIdType(clazz)) {
			if (value instanceof String) {
				value = EntityUtils.convertToIdRef(r, (String) value);
			} else if (value instanceof List) {
				value = EntityUtils.convertToIdRefs(r, (List<?>) value);
			}
		}
	}

	public FilterOperator reverseOperator(FilterOperator op) {
		switch (op) {
		case EQUAL:
			return FilterOperator.NOT_EQUAL;
		case GREATER_THAN:
			return FilterOperator.LESS_THAN_OR_EQUAL;
		case GREATER_THAN_OR_EQUAL:
			return FilterOperator.LESS_THAN;
		case IN:
			throw new RuntimeException("Cannot invert (call not) on IN operators.");
		case LESS_THAN:
			return FilterOperator.GREATER_THAN_OR_EQUAL;
		case LESS_THAN_OR_EQUAL:
			return FilterOperator.GREATER_THAN;
		case NOT_EQUAL:
			return FilterOperator.EQUAL;
		default:
			throw new RuntimeException("Unexpected enum constant.");
		}
	}

	@Override
	public BaseCondition not() {
		return new SimpleCondition(field, reverseOperator(operator), value);
	}

	@Override
	public boolean evaluate(Object object) {
		Object objectValue = ReflectionUtils.getFieldValue(object, field);
		return value.equals(objectValue);
	}

}
