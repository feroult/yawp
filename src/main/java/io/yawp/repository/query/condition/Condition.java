package io.yawp.repository.query.condition;

public abstract class Condition {

	public static BaseCondition c(String field, String operator, Object comparison) {
		return c(field, WhereOperator.toOperator(operator), comparison);
	}

	public static BaseCondition c(String field, WhereOperator whereOperator, Object comparison) {
		return new SimpleCondition(field, whereOperator, comparison);
	}

	public static BaseCondition and(BaseCondition... conditions) {
		if (conditions.length == 1) {
			return conditions[0];
		}
		return new JoinedCondition(LogicalOperator.AND, conditions);
	}

	public static BaseCondition or(BaseCondition... conditions) {
		if (conditions.length == 1) {
			return conditions[0];
		}
		return new JoinedCondition(LogicalOperator.OR, conditions);
	}

	public static BaseCondition not(BaseCondition c) {
		return c.not();
	}

	public static BaseCondition equals(String field, Object value) {
		return c(field, WhereOperator.EQUAL, value);
	}
}
