package io.yawp.repository.query;

import io.yawp.commons.utils.EntityUtils;
import io.yawp.commons.utils.ReflectionUtils;
import io.yawp.repository.IdRef;
import io.yawp.repository.Repository;

import java.util.Arrays;
import java.util.Collection;
import java.util.List;

import com.google.appengine.api.datastore.Key;
import com.google.appengine.api.datastore.Query.Filter;
import com.google.appengine.api.datastore.Query.FilterOperator;
import com.google.appengine.api.datastore.Query.FilterPredicate;

public abstract class BaseCondition {

	protected static final Class<?>[] VALID_ID_CLASSES = new Class<?>[] { IdRef.class, Long.class, String.class, Key.class };

	public abstract Filter getPredicate(Class<?> clazz) throws FalsePredicateException;

	public boolean evaluate(Object object) {
		return true;
	}

	public abstract BaseCondition not();

	public abstract void normalizeIdRefs(Class<?> clazz, Repository r);

	public abstract Class<?> getIdTypeFor(Class<?> clazz);

	public boolean isByIdFor(Class<?> clazz) {
		return this.getIdTypeFor(clazz) != null;
	}

	public BaseCondition and(BaseCondition c) {
		return Condition.and(this, c);
	}

	public BaseCondition or(BaseCondition c) {
		return Condition.or(this, c);
	}

	protected static void assertList(Object object) {
		getContentType(object);
	}

	protected static Class<?> getContentType(Object object) {
		Class<?> clazz = object.getClass();
		if (clazz.isArray()) {
			return clazz.getComponentType();
		}
		if (Collection.class.isAssignableFrom(clazz)) {
			Collection<?> c = (Collection<?>) object;
			if (c.isEmpty()) {
				return null;
			}
			return c.iterator().next().getClass();
		}
		throw new RuntimeException("Unsupported 'in' type: must be a primtive array or a Collection<?>. Found " + clazz.getSimpleName());
	}

	protected static boolean isValidIdClass(Object value, boolean list) {
		Class<?> actualClazz;
		if (list) {
			actualClazz = getContentType(value);
			if (actualClazz == null) {
				return true;
			}
		} else {
			actualClazz = value.getClass();
		}

		for (Class<?> validClass : VALID_ID_CLASSES) {
			if (validClass.isAssignableFrom(actualClazz)) {
				return true;
			}
		}
		return false;
	}

	static class SimpleCondition extends BaseCondition {
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

	static class JoinedCondition extends BaseCondition {
		private LogicalOperator operator;
		private BaseCondition[] conditions;

		public JoinedCondition(LogicalOperator operator, BaseCondition[] conditions) {
			this.operator = operator;
			this.conditions = conditions;
		}

		@Override
		public Class<?> getIdTypeFor(Class<?> clazz) {
			Class<?> mostLimitating = null;
			for (BaseCondition condition : conditions) {
				Class<?> current = condition.getIdTypeFor(clazz);
				if (isMoreLimitating(current, mostLimitating)) {
					mostLimitating = current;
				}
			}
			return mostLimitating;
		}

		private static boolean isMoreLimitating(Class<?> current, Class<?> mostLimitating) {
			if (mostLimitating == null) {
				return true;
			}

			if (current == null) {
				return false;
			}
			if (IdRef.class.isAssignableFrom(mostLimitating)) {
				return false;
			}
			if (IdRef.class.isAssignableFrom(current)) {
				return true;
			}
			return false;
		}

		@Override
		public Filter getPredicate(Class<?> clazz) throws FalsePredicateException {
			return operator.join(clazz, conditions);
		}

		public LogicalOperator getOperator() {
			return operator;
		}

		public BaseCondition[] getConditions() {
			return conditions;
		}

		@Override
		public void normalizeIdRefs(Class<?> clazz, Repository r) {
			for (BaseCondition c : conditions) {
				c.normalizeIdRefs(clazz, r);
			}
		}

		@Override
		public BaseCondition not() {
			BaseCondition[] reversedConditions = new BaseCondition[conditions.length];
			for (int i = 0; i < conditions.length; i++) {
				reversedConditions[i] = conditions[i].not();
			}
			return new JoinedCondition(operator.not(), reversedConditions);
		}
	}
}
