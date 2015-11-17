package io.yawp.repository.query.condition;

import io.yawp.repository.Repository;

public class JoinedCondition extends BaseCondition {

    private LogicalOperator logicalOperator;

    private BaseCondition[] conditions;

    private boolean hasPreFilter = false;

    private boolean hasPostFilter = false;

    public JoinedCondition(LogicalOperator logicalOperator, BaseCondition[] conditions) {
        this.logicalOperator = logicalOperator;
        this.conditions = conditions;
    }

    @Override
    public void init(Repository r, Class<?> clazz) {

        boolean allSubConditionsHasPreFilter = true;
        boolean oneSubConditionHasPreFilter = false;

        for (BaseCondition c : conditions) {
            c.init(r, clazz);

            if (!c.hasPreFilter()) {
                allSubConditionsHasPreFilter = false;
            } else {
                oneSubConditionHasPreFilter = true;
            }

            if (c.hasPostFilter()) {
                hasPostFilter = true;
            }
        }

        hasPreFilter = (oneSubConditionHasPreFilter && logicalOperator == LogicalOperator.AND)
                || (allSubConditionsHasPreFilter && logicalOperator == LogicalOperator.OR);
    }

    public LogicalOperator getLogicalOperator() {
        return logicalOperator;
    }

    public BaseCondition[] getConditions() {
        return conditions;
    }

    @Override
    public boolean hasPreFilter() {
        return hasPreFilter;
    }

    @Override
    public boolean hasPostFilter() {
        return hasPostFilter;
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
