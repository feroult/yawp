package io.yawp.repository.query.condition;

import io.yawp.commons.utils.EntityUtils;
import io.yawp.commons.utils.ReflectionUtils;
import io.yawp.repository.Repository;

import java.util.Arrays;
import java.util.List;

import com.google.appengine.api.datastore.Query.Filter;
import com.google.appengine.api.datastore.Query.FilterPredicate;

public class SimpleCondition extends BaseCondition {

	private String field;
	private WhereOperator whereOperator;
	private Object value;

	public SimpleCondition(String field, WhereOperator whereOperator, Object value) {
		this.field = field;
		this.whereOperator = whereOperator;
		this.value = value;

		if (whereOperator == WhereOperator.IN) {
			assertList(value);
		}
	}

	@Override
	public Class<?> getIdTypeFor(Class<?> clazz) {
		if (isFieldIdType(clazz)) {
			boolean isList = whereOperator == WhereOperator.IN;
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

		if (whereOperator == WhereOperator.IN && EntityUtils.listSize(value) == 0) {
			throw new FalsePredicateException();
		}

		return new FilterPredicate(actualFieldName, whereOperator.getFilterOperator(), actualValue);
	}

	public String getField() {
		return field;
	}

	public WhereOperator getWhereOperator() {
		return this.whereOperator;
	}

	public boolean isEqualOperator() {
		return this.whereOperator == WhereOperator.EQUAL;
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

	@Override
	public BaseCondition not() {
		return new SimpleCondition(field, whereOperator.reverse(), value);
	}

	@Override
	public boolean evaluate(Object object) {
		Object objectValue = ReflectionUtils.getFieldValue(object, field);
		return value.equals(objectValue);
	}
}
