package io.yawp.repository;

import io.yawp.commons.utils.JsonUtils;

import com.google.gson.JsonObject;

public class LazyJson<T> {

	private String json;
	private Class<T> clazz;

	@SuppressWarnings("unchecked")
	public void set(T value) {
		this.clazz = ((Class<T>) value.getClass());
		this.json = JsonUtils.to(value);
	}

	public static <T> LazyJson<T> from(T object) {
		LazyJson<T> lazyJson = new LazyJson<T>();
		lazyJson.set(object);
		return lazyJson;
	}

	public T get() {
		return JsonUtils.from(Yawp.yawp(), json, clazz);
	}

	@Override
	public String toString() {
		return "{json : '" + json + "', clazz: '" + clazz.getName() + "' }";
	}

	@SuppressWarnings("unchecked")
	public static <T> LazyJson<T> parse(JsonObject object) {
		//FIXME pass clazz as parameter
		try {
			LazyJson<T> lazyJson = new LazyJson<T>();
			lazyJson.clazz = (Class<T>) Class.forName(object.get("clazz").getAsString());
			lazyJson.json = object.get("json").getAsString();
			return lazyJson;
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		}
		return null;
	}

	public String getJson() {
		return json;
	}

	public void setJson(String json) {
		this.json = json;
	}

	public Class<T> getClazz() {
		return clazz;
	}

	public void setClazz(Class<T> clazz) {
		this.clazz = clazz;
	}

}
