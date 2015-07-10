package io.yawp.repository.query;

import com.google.appengine.api.datastore.Query.FilterOperator;

public abstract class Condition {

	public static BaseCondition c(String field, String operator, Object comparison) {
		return c(field, toOperator(operator), comparison);
	}

	public static BaseCondition c(String field, FilterOperator operator, Object comparison) {
		return new BaseCondition.SimpleCondition(field, operator, comparison);
	}

	public static BaseCondition and(BaseCondition... conditions) {
		if (conditions.length == 1) {
			return conditions[0];
		}
		return new BaseCondition.JoinedCondition(LogicalOperator.AND, conditions);
	}

	public static BaseCondition or(BaseCondition... conditions) {
		if (conditions.length == 1) {
			return conditions[0];
		}
		return new BaseCondition.JoinedCondition(LogicalOperator.OR, conditions);
	}

	public static BaseCondition not(BaseCondition c) {
		return c.not();
	}

	public static BaseCondition equals(String field, Object comparison) {
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
