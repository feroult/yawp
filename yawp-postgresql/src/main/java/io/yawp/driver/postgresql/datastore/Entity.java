package io.yawp.driver.postgresql.datastore;

import io.yawp.commons.utils.JsonUtils;

import java.util.HashMap;
import java.util.Map;

public class Entity {

    public static final String KEY_RESERVED_PROPERTY = "__key__";

    public static final String NORMALIZED_FIELD_PREFIX = "__";

    private Key key;

    private Map<String, Object> properties = new HashMap<String, Object>();

    public Entity() {

    }

    public Entity(Key key) {
        this.key = key;
    }

    public Entity(String kind, Key parentKey) {
        this.key = KeyFactory.createKey(parentKey, kind);
    }

    public Entity(String kind) {
        this.key = KeyFactory.createKey(kind);
    }

    public Key getKey() {
        return key;
    }

    public String getKind() {
        return key.getKind();
    }

    public String serializeProperties() {
        return JsonUtils.to(properties);
    }

    @SuppressWarnings("unchecked")
    public void deserializeProperties(String json) {
        this.properties = JsonUtils.from(null, json, Map.class);
    }

    public void setProperty(String property, Object value) {
        properties.put(property, value);
    }

    public void setUnindexedProperty(String property, Object value) {
        setProperty(property, value);
    }

    public Object getProperty(String property) {
        return properties.get(property);
    }

}
