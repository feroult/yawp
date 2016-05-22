package io.yawp.repository;

import io.yawp.commons.utils.JsonUtils;

import java.io.Serializable;

public final class LazyJson<T> implements Serializable {

    private static final long serialVersionUID = 6613992940340254518L;

    private Class<T> clazz;

    private T cache;

    private String json;

    public void set(T value) {
        if (value == null) {
            this.clazz = null;
            this.cache = null;
            this.json = null;
            return;
        }
        this.clazz = ((Class<T>) value.getClass());
        this.cache = value;
        this.json = JsonUtils.to(value);
    }

    public T get() {
        if (json == null) {
            return null;
        }
        if (cache == null) {
            cache = JsonUtils.from(Yawp.yawp(), json, clazz);
        }
        return cache;
    }

    public static <T> LazyJson<T> create(Class<T> clazz, String json) {
        LazyJson<T> lazyJson = new LazyJson<T>();
        lazyJson.clazz = clazz;
        lazyJson.json = json;
        return lazyJson;
    }

    public static <T> LazyJson<T> create(T object) {
        LazyJson<T> lazyJson = new LazyJson<T>();
        lazyJson.set(object);
        return lazyJson;
    }

    public String getJson() {
        if (cache != null) {
            json = JsonUtils.to(cache);
        }
        return json;
    }

}
