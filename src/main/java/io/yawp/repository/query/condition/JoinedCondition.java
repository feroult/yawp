package io.yawp.repository.query.condition;

import io.yawp.repository.IdRef;
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
		return logicalOperator.join(clazz, conditions);
	}

	public LogicalOperator getLogicalOperator() {
		return logicalOperator;
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
		return new JoinedCondition(logicalOperator.not(), reversedConditions);
	}

}
