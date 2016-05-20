package io.yawp.repository;

import io.yawp.commons.utils.JsonUtils;


public final class LazyJson<T> {

    private transient Class<T> clazz;

    private transient T cache;

    private String json;

    @SuppressWarnings("unchecked")
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
        return json;
    }

}
