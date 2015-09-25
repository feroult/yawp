package io.yawp.driver.postgresql.datastore;

import io.yawp.commons.utils.JsonUtils;

import java.util.HashMap;
import java.util.Map;

public class Entity {

	private Key key;

	private Map<String, Object> properties = new HashMap<String, Object>();

	public Entity() {

	}

	public Entity(Key key) {
		this.key = key;
	}

	public Entity(String kind, Key parentKey) {
		this.key = Key.create(kind, parentKey);
	}

	public Entity(String kind) {
		this.key = Key.create(kind);
	}

	public Key getKey() {
		return key;
	}

	public String serialize() {
		return JsonUtils.to(this);
	}

	public static Entity deserialize(String json) {
		return JsonUtils.from(null, json, Entity.class);
	}

	public void setProperty(String property, Object value) {
		properties.put(property, value);
	}

	public Object getProperty(String property) {
		return properties.get(property);
	}

}
