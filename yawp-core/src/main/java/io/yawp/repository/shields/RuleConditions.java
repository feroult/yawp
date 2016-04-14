package io.yawp.repository.shields;

import io.yawp.repository.IdRef;
import io.yawp.repository.models.ObjectHolder;
import io.yawp.repository.Repository;
import io.yawp.repository.query.NoResultException;
import io.yawp.repository.query.condition.BaseCondition;
import io.yawp.repository.query.condition.Condition;

import java.util.List;

public class RuleConditions {

    private BaseCondition condition;

    private Repository r;

    private Class<?> endpointClazz;

    private IdRef<?> id;

    private List<?> objects;

    public RuleConditions(Repository r, Class<?> endpointClazz, IdRef<?> id, List<?> objects) {
        this.r = r;
        this.endpointClazz = endpointClazz;
        this.id = id;
        this.objects = objects;
    }

    public void where(BaseCondition condition) {
        or(condition);
    }

    public void or(BaseCondition condition) {
        if (this.condition != null) {
            this.condition = Condition.or(this.condition, condition);
            return;
        }
        this.condition = condition;
    }

    public void and(BaseCondition condition) {
        if (this.condition != null) {
            this.condition = Condition.and(this.condition, condition);
            return;
        }
        this.condition = condition;
    }

    public BaseCondition getWhere() {
        return condition;
    }

    private void initConditions() {
        if (condition == null) {
            return;
        }
        condition.init(r, endpointClazz);
    }

    public boolean evaluate() {
        initConditions();
        return evaluateIncoming() && evaluateExisting();
    }

    private boolean evaluateIncoming() {
        if (condition == null) {
            return true;
        }

        if (objects == null) {
            return true;
        }

        return evaluateObjects(new EvaluateIncoming());
    }

    private boolean evaluateExisting() {
        if (condition == null) {
            return true;
        }

        if (objects == null) {
            return condition.evaluate(id.fetch());
        }

        return evaluateObjects(new EvaluateExisting());
    }

    private boolean evaluateObjects(Evaluate e) {
        boolean result = true;
        for (Object object : objects) {
            result = result && e.evaluate(object);
            if (!result) {
                return false;
            }
        }
        return true;
    }

    private interface Evaluate {
        boolean evaluate(Object object);
    }

    private class EvaluateIncoming implements Evaluate {
        @Override
        public boolean evaluate(Object object) {
            return condition.evaluate(object);
        }
    }

    private class EvaluateExisting implements Evaluate {
        @Override
        public boolean evaluate(Object object) {
            ObjectHolder objectHolder = new ObjectHolder(object);
            IdRef<?> id = objectHolder.getId();
            if (id == null) {
                return true;
            }

            Object existingObject;
            try {
                existingObject = id.fetch();
            } catch (NoResultException e) {
                return true;
            }

            return condition.evaluate(existingObject);
        }
    }

}
