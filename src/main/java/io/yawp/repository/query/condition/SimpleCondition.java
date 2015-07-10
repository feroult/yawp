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
	private Object whereValue;

	public SimpleCondition(String field, WhereOperator whereOperator, Object value) {
		this.field = field;
		this.whereOperator = whereOperator;
		this.whereValue = value;

		if (whereOperator == WhereOperator.IN) {
			assertList(value);
		}
	}

	public String getField() {
		return field;
	}

	public WhereOperator getWhereOperator() {
		return this.whereOperator;
	}

	public Object getWhereValue() {
		return whereValue;
	}

	public boolean isEqualOperator() {
		return this.whereOperator == WhereOperator.EQUAL;
	}

	@Override
	public Class<?> getIdTypeFor(Class<?> clazz) {
		if (isFieldIdType(clazz)) {
			boolean isList = whereOperator == WhereOperator.IN;
			if (isValidIdClass(whereValue, isList)) {
				return whereValue.getClass();
			} else {
				throw new RuntimeException("If you are searching by @Id, you can only use the valid @Id types classes: "
						+ Arrays.toString(VALID_ID_CLASSES) + ". Found " + whereValue.getClass().getSimpleName());
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
		Object actualValue = EntityUtils.getActualFieldValue(field, clazz, whereValue);

		if (whereOperator == WhereOperator.IN && EntityUtils.listSize(whereValue) == 0) {
			throw new FalsePredicateException();
		}

		return new FilterPredicate(actualFieldName, whereOperator.getFilterOperator(), actualValue);
	}

	@Override
	public void normalizeIdRefs(Class<?> clazz, Repository r) {
		if (isFieldIdType(clazz)) {
			if (whereValue instanceof String) {
				whereValue = EntityUtils.convertToIdRef(r, (String) whereValue);
			} else if (whereValue instanceof List) {
				whereValue = EntityUtils.convertToIdRefs(r, (List<?>) whereValue);
			}
		}
	}

	@Override
	public BaseCondition not() {
		return new SimpleCondition(field, whereOperator.reverse(), whereValue);
	}

	@Override
	public boolean evaluate(Object object) {
		Object objectValue = ReflectionUtils.getFieldValue(object, field);
		return whereOperator.evaluate(objectValue, whereValue);
	}
}
