package endpoint.repository.query;

import java.util.Arrays;
import java.util.Collection;

import com.google.appengine.api.datastore.Key;
import com.google.appengine.api.datastore.Query.Filter;
import com.google.appengine.api.datastore.Query.FilterOperator;
import com.google.appengine.api.datastore.Query.FilterPredicate;

import endpoint.repository.IdRef;
import endpoint.utils.EntityUtils;

public abstract class BaseCondition {

	protected static final Class<?>[] VALID_ID_CLASSES = new Class<?>[] { IdRef.class, Long.class, Key.class };

	public abstract Filter getPredicate(Class<?> clazz) throws FalsePredicateException;

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
			if (this.field.equals(EntityUtils.getIdFieldName(clazz))) {
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
			if (current == null) {
				return false;
			}
			if (IdRef.class.isAssignableFrom(mostLimitating.getClass())) {
				return false;
			}
			if (IdRef.class.isAssignableFrom(current.getClass())) {
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
	}
}
