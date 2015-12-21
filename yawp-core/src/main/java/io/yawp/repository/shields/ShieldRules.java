package io.yawp.repository.shields;

import io.yawp.repository.query.condition.BaseCondition;

import java.util.ArrayList;
import java.util.List;

public class ShieldRules<T> {

    private List<AllowRule> rules = new ArrayList<>();

    private boolean merged;

    private boolean hasCondition;

    private ShieldConditions conditions;

    private Class<? super T> facade;

    public void add(AllowRule rule) {
        rules.add(rule);
    }

    public boolean hasCondition() {
        merge();
        return hasCondition;
    }

    public boolean evaluateConditions() {
        merge();

        if (!hasCondition) {
            return true;
        }

        return conditions.evaluate();
    }

    public BaseCondition getWhere() {
        merge();
        return conditions.getWhere();
    }

    public boolean hasFacade() {
        merge();
        return facade != null;
    }

    public Class<? super T> getFacade() {
        merge();
        return facade;
    }

    private void merge() {
        if (merged) {
            return;
        }

        for (AllowRule rule : rules) {
            if (rule.hasConditions()) {
                this.hasCondition = true;

                if (this.conditions == null) {
                    this.conditions = rule.getConditions();
                } else {
                    this.conditions.or(rule.getConditions().getWhere());
                }
            }

            // last facade wins, even if it is null
            this.facade = rule.getFacade();
        }

        merged = true;
    }
}
