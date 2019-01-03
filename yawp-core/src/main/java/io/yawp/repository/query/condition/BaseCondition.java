package io.yawp.repository.query.condition;

import io.yawp.repository.Repository;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public abstract class BaseCondition {

    public abstract void init(Repository r, Class<?> clazz);

    public abstract boolean hasPreFilter();

    public abstract boolean hasPostFilter();

    public abstract boolean evaluate(Object object);

    public abstract BaseCondition not();

    public abstract Map<String, Object> toMap();

    public BaseCondition and(BaseCondition c) {
        return Condition.and(this, c);
    }

    public BaseCondition or(BaseCondition c) {
        return Condition.or(this, c);
    }

    public <T> List<T> applyPostFilter(List<T> objects) {
        List<T> result = new ArrayList<>();

        for (T object : objects) {
            if (!evaluate(object)) {
                continue;
            }
            result.add(object);
        }

        return result;
    }
}
