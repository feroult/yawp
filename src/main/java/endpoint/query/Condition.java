package endpoint.query;

import com.google.appengine.api.datastore.Query.Filter;
import com.google.appengine.api.datastore.Query.FilterOperator;
import com.google.appengine.api.datastore.Query.FilterPredicate;

import endpoint.utils.EntityUtils;

public abstract class Condition {
	
	public abstract Filter getPredicate(Class<?> clazz);

	protected static class SimpleCondition extends Condition {
		private String field;
		private FilterOperator operator;
		private Object value;

		public SimpleCondition(String field, FilterOperator operator, Object value) {
			this.field = field;
			this.operator = operator;
			this.value = value;
		}

		@Override
		public Filter getPredicate(Class<?> clazz) {
			String actualFieldName = EntityUtils.getActualFieldName(field, clazz);
			Object actualValue = EntityUtils.getActualFieldValue(field, clazz, value);
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

	protected static class JoinedCondition extends Condition {
		private LogicalOperator operator;
		private Condition[] conditions;

		public JoinedCondition(LogicalOperator operator, Condition[] conditions) {
			this.operator = operator;
			this.conditions = conditions;
		}

		@Override
		public Filter getPredicate(Class<?> clazz) {
			return operator.join(clazz, conditions);
		}
	}

	public static Condition c(String field, String operator, Object comparison) {
		return c(field, toOperator(operator), comparison);
	}

	public static Condition c(String field, FilterOperator operator, Object comparison) {
		return new SimpleCondition(field, operator, comparison);
	}

	public static Condition and(Condition... conditions) {
		return new JoinedCondition(LogicalOperator.AND, conditions);
	}

	public static Condition or(Condition... conditions) {
		return new JoinedCondition(LogicalOperator.OR, conditions);
	}

	public static Condition equals(String field, Object comparison) {
		return c(field, FilterOperator.EQUAL, comparison);
	}

	public static FilterOperator toOperator(String operator) {
		if (operator.equals("=")) {
			return FilterOperator.EQUAL;
		}
		if (operator.equals(">")) {
			return FilterOperator.GREATER_THAN;
		}
		if (operator.equals(">=")) {
			return FilterOperator.GREATER_THAN_OR_EQUAL;
		}
		if (operator.equalsIgnoreCase("in")) {
			return FilterOperator.IN;
		}
		if (operator.equals("<")) {
			return FilterOperator.LESS_THAN;
		}
		if (operator.equals("<=")) {
			return FilterOperator.LESS_THAN_OR_EQUAL;
		}
		if (operator.equals("!=")) {
			return FilterOperator.NOT_EQUAL;
		}
		throw new RuntimeException("invalid filter operator " + operator);
	}
}
