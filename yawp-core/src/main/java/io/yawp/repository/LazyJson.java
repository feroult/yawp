package io.yawp.repository;

import io.yawp.commons.utils.JsonUtils;

import java.io.Serializable;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.List;

public final class LazyJson<T> implements Serializable {

    private static final long serialVersionUID = 6613992940340254518L;

    private Type type;

    private T object;

    private String json;

    public static <T> LazyJson<T> create(T object) {
        LazyJson<T> lazyJson = new LazyJson<T>();
        lazyJson.set(object);
        return lazyJson;
    }

    public void set(T value) {
        if (value == null) {
            setNull();
            return;
        }

        Type type = value.getClass();

        this.type = type;
        this.object = value;
    }


    public T get() {
        if (object == null && json == null) {
            return null;
        }

        if (object == null) {
            object = (T) JsonUtils.from(Yawp.yawp(), json, type);
        }

        return object;
    }

    public String getJson() {
        if (object != null) {
            json = JsonUtils.to(object);
        }
        return json;
    }

    public boolean isJsonArray() {
        if (type instanceof ParameterizedType) {
            return List.class.isAssignableFrom((Class<?>) ((ParameterizedType) type).getRawType());
        }
        return List.class.isAssignableFrom((Class<?>) type);
    }

    private void setNull() {
        this.type = null;
        this.object = null;
        this.json = null;
    }

    /**
     * Internal only method.
     */
    public static LazyJson<?> $create(Type type, String json) {
        LazyJson<?> lazyJson = new LazyJson<>();
        lazyJson.type = type;
        lazyJson.json = json;
        return lazyJson;
    }

    public boolean isParsed() {
        return object != null;
    }
}
