package io.yawp.repository.query.condition;

import io.yawp.repository.FieldModel;
import io.yawp.repository.IdRef;
import io.yawp.repository.ObjectModel;
import io.yawp.repository.Repository;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public class SimpleCondition extends BaseCondition {

	private Repository r;

	private Class<?> clazz;

	private ObjectModel model;

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
		this.model = new ObjectModel(clazz);
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
		return field.equals(model.getIdField().getName());
	}

	@Override
	public boolean hasPreFilter() {
		if (isRefField()) {
			return false;
		}
		FieldModel fieldModel = model.getFieldModel(field);
		return fieldModel.hasIndex() || fieldModel.isId();
	}

	@Override
	public boolean hasPostFilter() {
		return !hasPreFilter();
	}

	private boolean isRefField() {
		return field.indexOf("->") != -1;
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
				whereValue = IdRef.parse(r, (String) whereValue);

			} else if (whereValue instanceof List) {
				whereValue = convertToIdRefs((List<?>) whereValue);
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

	private List<IdRef<?>> convertToIdRefs(List<?> rawIds) {
		List<IdRef<?>> ids = new ArrayList<>();
		for (Object rawId : rawIds) {
			if (rawId instanceof String) {
				ids.add(IdRef.parse(r, (String) rawId));
			} else {
				ids.add((IdRef<?>) rawId);
			}
		}
		return ids;
	}
}
