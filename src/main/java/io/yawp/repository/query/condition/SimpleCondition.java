package io.yawp.repository.query.condition;

import io.yawp.commons.utils.EntityUtils;
import io.yawp.repository.Repository;

import java.util.Collection;
import java.util.List;

import com.google.appengine.api.datastore.Query.Filter;
import com.google.appengine.api.datastore.Query.FilterPredicate;

public class SimpleCondition extends BaseCondition {

	private Repository r;

	private Class<?> clazz;

	private String field;

	private WhereOperator whereOperator;

	private Object whereValue;

	public SimpleCondition(String field, WhereOperator whereOperator, Object value) {
		this.field = field;
		this.whereOperator = whereOperator;
		this.whereValue = value;

		if (whereOperator == WhereOperator.IN) {
			assertIsList(value);
		}
	}

	@Override
	public void init(Repository r, Class<?> clazz) {
		this.r = r;
		this.clazz = clazz;
		normalizeIdRefs();
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

	public boolean isIdField() {
		return field.equals(EntityUtils.getIdFieldName(clazz));
	}

	@Override
	public boolean hasPreFilter() {
		return !isRefField() && (EntityUtils.hasIndex(clazz, field) || EntityUtils.isId(clazz, field));
	}

	@Override
	public boolean hasPostFilter() {
		return !hasPreFilter();
	}

	private boolean isRefField() {
		return field.indexOf("->") != -1;
	}

	@Override
	public Filter createPreFilter() throws FalsePredicateException {
		String actualFieldName = EntityUtils.getActualFieldName(field, clazz);
		Object actualValue = EntityUtils.getActualFieldValue(field, clazz, whereValue);

		if (whereOperator == WhereOperator.IN && EntityUtils.listSize(whereValue) == 0) {
			throw new FalsePredicateException();
		}

		return new FilterPredicate(actualFieldName, whereOperator.getFilterOperator(), actualValue);
	}

	@Override
	public boolean evaluate(Object object) {
		try {
			Object objectValue = new ConditionReference(field, clazz, object).getValue();
			return whereOperator.evaluate(objectValue, whereValue);
		} catch (ConditionForChildException e) {
			return true;
		}
	}

	@Override
	public BaseCondition not() {
		return new SimpleCondition(field, whereOperator.reverse(), whereValue);
	}

	private void normalizeIdRefs() {
		if (isIdField()) {
			if (whereValue instanceof String) {
				whereValue = EntityUtils.convertToIdRef(r, (String) whereValue);
			} else if (whereValue instanceof List) {
				whereValue = EntityUtils.convertToIdRefs(r, (List<?>) whereValue);
			}
		}
	}

	private void assertIsList(Object value) {
		if (value == null) {
			return;
		}
		Class<?> valueClazz = value.getClass();
		if (!(valueClazz.isArray() || Collection.class.isAssignableFrom(valueClazz))) {
			throw new RuntimeException("Unsupported 'in' type: must be a primtive array or a Collection<?>. Found "
					+ valueClazz.getSimpleName());
		}
	}

}
