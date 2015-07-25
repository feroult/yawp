package io.yawp.repository.query.condition;

import io.yawp.commons.utils.EntityUtils;
import io.yawp.commons.utils.ReflectionUtils;
import io.yawp.repository.IdRef;
import io.yawp.repository.Repository;

import java.util.Collection;
import java.util.List;

import com.google.appengine.api.datastore.Query.Filter;
import com.google.appengine.api.datastore.Query.FilterPredicate;

public class SimpleCondition extends BaseCondition {

	private static final String PARENT_REF_KEYWORK = "parent";

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

		Object objectValue = null;

		if (isRefField()) {
			objectValue = getRefValue(object);
		} else {
			objectValue = ReflectionUtils.getFieldValue(object, field);
		}

		return whereOperator.evaluate(objectValue, whereValue);
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
		Class<?> valueClazz = value.getClass();
		if (!(valueClazz.isArray() || Collection.class.isAssignableFrom(valueClazz))) {
			throw new RuntimeException("Unsupported 'in' type: must be a primtive array or a Collection<?>. Found "
					+ valueClazz.getSimpleName());
		}
	}

	private Object getRefValue(Object object) {
		FieldRefs refs = parseFieldRefs();

		Object objectRef = object;

		while (refs.hasMoreRefs()) {
			IdRef<?> idRef = null;

			if (refs.isParentRef()) {
				idRef = EntityUtils.getParentId(objectRef);
				refs.nextRef();

				// advance all parents in a row
				while (refs.isParentRef()) {
					idRef = idRef.getParentId();
					refs.nextRef();
				}

			} else {
				idRef = (IdRef<?>) ReflectionUtils.getFieldValue(objectRef, refs.nextRef());
			}

			if (idRef == null) {
				return null;
			}

			objectRef = idRef.fetch();
		}

		return ReflectionUtils.getFieldValue(objectRef, refs.fieldName());
	}

	private FieldRefs parseFieldRefs() {
		return new FieldRefs(field.split("->"));
	}

	private class FieldRefs {

		private String[] split;

		private int current;

		public FieldRefs(String[] split) {
			this.split = split;
			this.current = 0;
		}

		public boolean hasMoreRefs() {
			return current < split.length - 1;
		}

		public String nextRef() {
			return split[current++];
		}

		public boolean isParentRef() {
			return split[current].equalsIgnoreCase(PARENT_REF_KEYWORK);
		}

		public String fieldName() {
			return split[split.length - 1];
		}
	}
}
