package io.yawp.repository;

import io.yawp.commons.utils.JsonUtils;


public final class LazyJson<T> {

	private String json;
	private transient Class<T> clazz;

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
		return json;
	}

	public static <T> LazyJson<T> parse(String object, Class<T> clazz) {
		LazyJson<T> lazyJson = new LazyJson<T>();
		lazyJson.clazz = clazz;
		lazyJson.json = object;
		return lazyJson;
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
