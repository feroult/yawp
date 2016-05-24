package io.yawp.repository;

import io.yawp.commons.utils.JsonUtils;

import java.io.Serializable;
import java.util.List;
import java.util.Map;

public final class LazyJson<T> implements Serializable {

    enum JsonType {
        OBJECT, LIST, MAP
    }

    private static final long serialVersionUID = 6613992940340254518L;

    private Class<?> clazz;

    private JsonType jsonType;

    private Class<?> keyClazz;

    private Class<?> valueClazz;

    private T object;

    private String json;

    public static <T> LazyJson<T> $create(T object) {
        LazyJson<T> lazyJson = new LazyJson<T>();
        lazyJson.set(object);
        return lazyJson;
    }

    public void set(T value) {
        if (value == null) {
            setNull();
            return;
        }

        Class<T> clazz = (Class<T>) value.getClass();
        JsonType jsonType = resolveJsonType(clazz);

        if (isEmpty(jsonType, value)) {
            setNull();
            return;
        }

        this.clazz = clazz;
        this.jsonType = jsonType;
        this.object = value;
    }


    public T get() {
        if (jsonType == null) {
            return null;
        }

        if (object == null) {
            switch (jsonType) {
                case OBJECT:
                    object = (T) JsonUtils.from(Yawp.yawp(), json, clazz);
                    break;

                case LIST:
                    object = (T) JsonUtils.fromList(Yawp.yawp(), json, clazz);
                    break;

                case MAP:
                    object = (T) JsonUtils.fromMap(Yawp.yawp(), json, keyClazz, clazz);
                    break;
            }
        }
        
        return object;
    }


    public static LazyJson<?> $create(Class<?> clazz, String json) {
        LazyJson<?> lazyJson = new LazyJson<>();
        lazyJson.clazz = clazz;
        lazyJson.jsonType = JsonType.OBJECT;
        lazyJson.json = json;
        return lazyJson;
    }

    public static LazyJson<?> $createList(Class<?> clazz, String json) {
        LazyJson<?> lazyJson = new LazyJson<>();
        lazyJson.clazz = clazz;
        lazyJson.jsonType = JsonType.LIST;
        lazyJson.json = json;
        return lazyJson;
    }

    public static LazyJson<?> $createMap(Class<?> keyClazz, Class<?> clazz, String json) {
        LazyJson<?> lazyJson = new LazyJson<>();
        lazyJson.keyClazz = keyClazz;
        lazyJson.clazz = clazz;
        lazyJson.jsonType = JsonType.MAP;
        lazyJson.json = json;
        return lazyJson;

    }

    public String getJson() {
        if (object != null) {
            json = JsonUtils.to(object);
        }
        return json;
    }

    public boolean isJsonArray() {
        return jsonType == JsonType.LIST;
    }

    private void setNull() {
        this.clazz = null;
        this.jsonType = null;
        this.keyClazz = null;
        this.valueClazz = null;
        this.object = null;
        this.json = null;
    }

    private boolean isEmpty(JsonType jsonType, T value) {
        // TODO: implement
        return false;
    }

    private static JsonType resolveJsonType(Class<?> clazz) {
        if (List.class.isAssignableFrom(clazz)) {
            return JsonType.LIST;
        }
        if (Map.class.isAssignableFrom(clazz)) {
            return JsonType.MAP;
        }
        return JsonType.OBJECT;
    }

    private static Class<?> resolveValueClazz(JsonType jsonType, Class<?> clazz, Object value) {
        return null;
    }

    private static Class<?> resolveKeyClazz(JsonType jsonType, Class<?> clazz, Object value) {
        return null;
    }

    private static <T> Class<?> resolveValueClazz(JsonType jsonType, Class<T> clazz) {
        return null;
    }

    private static <T> Class<?> resolveKeyClazz(JsonType jsonType, Class<T> clazz) {
        return null;
    }

}
