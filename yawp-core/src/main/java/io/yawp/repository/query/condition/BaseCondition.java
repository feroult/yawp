package io.yawp.repository.query.condition;

import io.yawp.repository.IdRef;
import io.yawp.repository.Repository;

import java.util.ArrayList;
import java.util.List;

public abstract class BaseCondition {

    protected static final Class<?>[] VALID_ID_CLASSES = new Class<?>[]{IdRef.class, Long.class, String.class};

    public abstract void init(Repository r, Class<?> clazz);

    public abstract boolean hasPreFilter();

    public abstract boolean hasPostFilter();

    public abstract boolean evaluate(Object object);

    public abstract BaseCondition not();

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
