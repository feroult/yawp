package endpoint.query;

import com.google.appengine.api.datastore.Query.Filter;
import com.google.appengine.api.datastore.Query.FilterOperator;
import com.google.appengine.api.datastore.Query.FilterPredicate;

import endpoint.utils.EntityUtils;

public abstract class BaseCondition {
	public abstract Filter getPredicate(Class<?> clazz) throws FalsePredicateException;
	
	public BaseCondition and(BaseCondition c) {
		return Condition.and(this, c);
	}

	public BaseCondition or(BaseCondition c) {
		return Condition.or(this, c);
	}

	static class SimpleCondition extends BaseCondition {
		private String field;
		private FilterOperator operator;
		private Object value;

		public SimpleCondition(String field, FilterOperator operator, Object value) {
			this.field = field;
			this.operator = operator;
			this.value = value;
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
