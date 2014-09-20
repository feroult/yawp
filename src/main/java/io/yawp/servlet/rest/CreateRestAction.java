package io.yawp.servlet.rest;

import io.yawp.utils.JsonUtils;

import java.util.List;

public class CreateRestAction extends RestAction {

	@Override
	public Object action() {
		if (JsonUtils.isJsonArray(requestJson)) {
			return createArray();
		}

		return createObject();
	}

	private Object createObject() {
		Object object = JsonUtils.from(r, requestJson, endpointClazz);
		save(object);
		return object;
	}

	private Object createArray() {
		List<?> objects = JsonUtils.fromList(r, requestJson, endpointClazz);
		for (Object object : objects) {
			save(object);
		}
		return objects;
	}

}
