package io.yawp.repository.shields;

import io.yawp.repository.query.condition.BaseCondition;

import java.util.ArrayList;
import java.util.List;

public class ShieldRules<T> {

    private List<Rule> rules = new ArrayList<>();

    private boolean merged;

    private RuleConditions conditions;

    private Class<? super T> facade;

    public void add(Rule rule) {
        rules.add(rule);
    }

    public boolean hasCondition() {
        merge();
        return conditions != null;
    }

    public boolean evaluateConditions() {
        merge();
        if (conditions == null) {
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

        boolean emptyCondition = false;

        for (Rule rule : rules) {
            if (!emptyCondition) {
                if (rule.hasConditions()) {
                    mergeConditions(rule);
                } else {
                    this.conditions = null;
                    emptyCondition = true;
                }
            }

            // last facade wins, even if it is null
            this.facade = rule.getFacade();
        }

        merged = true;
    }

    private void mergeConditions(Rule rule) {
        if (this.conditions == null) {
            this.conditions = rule.getConditions();
        } else {
            this.conditions.or(rule.getConditions().getWhere());
        }
    }
}
