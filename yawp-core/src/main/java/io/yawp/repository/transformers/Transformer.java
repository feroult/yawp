package io.yawp.repository.transformers;

import io.yawp.repository.Feature;

import java.util.Map;

import org.apache.commons.beanutils.PropertyUtils;

public abstract class Transformer<T> extends Feature {

    public Object defaults(T object) {
        return object;
    }

    public Object index(T object) {
        return defaults(object);
    }

    public Object show(T object) {
        return defaults(object);
    }

    public Object create(T object) {
        return defaults(object);
    }

    public Object update(T object) {
        return defaults(object);
    }

    public Object custom(T object) {
        return defaults(object);
    }

    protected Map<String, Object> asMap(Object object) {
        try {
            Map<String, Object> map = PropertyUtils.describe(object);
            map.remove("class");
            return map;
        } catch (ReflectiveOperationException e) {
            throw new RuntimeException(e);
        }
    }
}
