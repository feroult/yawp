package io.yawp.repository.query.condition;

import io.yawp.repository.Repository;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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
    public Map<String, Object> toMap() {
        Map<String, Object> map = new HashMap<>();
        map.put("logicalOperator", logicalOperator.toString());
        List<Map<String, Object>> cs = new ArrayList<>();
        for (BaseCondition c : conditions) {
            cs.add(c.toMap());
        }
        map.put("conditions", cs);
        return map;
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
        for (BaseCondition condition : conditions) {
            if (condition.evaluate(object)) {
                return true;
            }
        }
        return false;
    }

    private boolean evaluateAnd(Object object) {
        for (BaseCondition condition : conditions) {
            if (!condition.evaluate(object)) {
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
