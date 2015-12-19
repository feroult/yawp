package io.yawp.repository;

import io.yawp.commons.utils.JsonUtils;
import io.yawp.repository.query.QueryBuilder;

import java.util.List;

public class Feature {

    protected Repository yawp;

    public void setRepository(Repository yawp) {
        this.yawp = yawp;
    }

    public <T> QueryBuilder<T> yawp(Class<T> clazz) {
        return yawp.query(clazz);
    }

    public <T> QueryBuilder<T> yawpWithHooks(Class<T> clazz) {
        return yawp.queryWithHooks(clazz);
    }

    public <T extends Feature> T feature(Class<T> clazz) {
        try {
            T feature = clazz.newInstance();
            feature.setRepository(yawp);
            return feature;
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public <T> IdRef<T> id(Class<T> clazz, Long id) {
        return IdRef.create(yawp, clazz, id);
    }

    public <T> IdRef<T> id(Class<T> clazz, String name) {
        return IdRef.create(yawp, clazz, name);
    }

    public <T> T from(String json, Class<T> clazz) {
        return JsonUtils.from(yawp, json, clazz);
    }

    public <T> List<T> fromList(String json, Class<T> clazz) {
        return JsonUtils.fromList(yawp, json, clazz);
    }

    public String to(Object object) {
        return JsonUtils.to(object);
    }

}
