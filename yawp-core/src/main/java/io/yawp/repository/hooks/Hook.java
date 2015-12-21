package io.yawp.repository.hooks;

import io.yawp.repository.Feature;
import io.yawp.repository.IdRef;
import io.yawp.repository.query.QueryBuilder;

import java.util.Map;

public class Hook<T> extends Feature {

    public void beforeShield(T object) {
    }

    public void beforeSave(T object) {
    }

    public void afterSave(T object) {
    }

    public void beforeQuery(QueryBuilder<T> q) {
    }

    public void beforeDestroy(IdRef<T> object) {
    }

    public void afterDestroy(IdRef<T> object) {
    }
}
