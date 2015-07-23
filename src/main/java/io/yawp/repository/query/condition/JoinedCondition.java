package io.yawp.repository.query.condition;

import io.yawp.repository.Repository;

import com.google.appengine.api.datastore.Query.Filter;

public class JoinedCondition extends BaseCondition {

	private LogicalOperator logicalOperator;

	private BaseCondition[] conditions;

	public JoinedCondition(LogicalOperator logicalOperator, BaseCondition[] conditions) {
		this.logicalOperator = logicalOperator;
		this.conditions = conditions;
	}

	@Override
	public void init(Repository r, Class<?> clazz) {
		for (BaseCondition c : conditions) {
			c.init(r, clazz);
		}
	}

	public LogicalOperator getLogicalOperator() {
		return logicalOperator;
	}

	public BaseCondition[] getConditions() {
		return conditions;
	}

	@Override
	public Filter getPredicate() throws FalsePredicateException {
		return logicalOperator.join(conditions);
	}

	@Override
	public boolean evaluate(Object object) {
		if (logicalOperator == LogicalOperator.AND) {
			return evaluateAnd(object);
		}
		return evaluateOr(object);
	}

	private boolean evaluateOr(Object object) {
		boolean result = false;
		for (BaseCondition condition : conditions) {
			result = result || condition.evaluate(object);
			if (result) {
				return true;
			}
		}
		return false;
	}

	private boolean evaluateAnd(Object object) {
		boolean result = true;
		for (BaseCondition condition : conditions) {
			result = result && condition.evaluate(object);
			if (!result) {
				return false;
			}
		}
		return true;
	}

	@Override
	public BaseCondition not() {
		BaseCondition[] reversedConditions = new BaseCondition[conditions.length];
		for (int i = 0; i < conditions.length; i++) {
			reversedConditions[i] = conditions[i].not();
		}
		return new JoinedCondition(logicalOperator.not(), reversedConditions);
	}

}
