package io.yawp.repository;

import com.google.gson.JsonObject;

import io.yawp.commons.utils.JsonUtils;

public class LazyJson {

	public String json;
	public Class<?> clazz;

	public void set(Object value) {
		this.clazz = value.getClass();
		this.json = JsonUtils.to(value);
	}

	public Object get() {
		return JsonUtils.from(Yawp.yawp(), json, clazz);
	}

	@Override
	public String toString() {
		return "{json : '" + json + "', clazz: '" + clazz.getName() + "' }";
	}

	public static LazyJson parse(JsonObject object) {
		try {
			LazyJson lazyJson = new LazyJson();
			lazyJson.clazz = Class.forName(object.get("clazz").getAsString());
			lazyJson.json = object.get("json").getAsString();
			return lazyJson;
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
			return null;
		}
	}

}
