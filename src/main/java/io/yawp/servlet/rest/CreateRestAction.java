package io.yawp.servlet.rest;

import io.yawp.repository.IdRef;
import io.yawp.utils.EntityUtils;
import io.yawp.utils.JsonUtils;

import java.util.List;

public class CreateRestAction extends RestAction {

	@Override
	public Object action() {
		if (JsonUtils.isJsonArray(requestJson)) {
			return createFromArray();
		}

		return createFromObject();
	}

	private Object createFromObject() {
		Object object = JsonUtils.from(r, requestJson, endpointClazz);
		saveObject(object);
		return object;
	}

	private Object createFromArray() {
		List<?> objects = JsonUtils.fromList(r, requestJson, endpointClazz);
		for (Object object : objects) {
			saveObject(object);
		}
		return objects;
	}

	protected void saveObject(Object object) {
		if (id != null) {
			saveWithParentId(object, id);
		} else {
			save(object);
		}
	}

	protected void saveWithParentId(Object object, IdRef<?> parentId) {
		EntityUtils.setParentId(object, parentId);
		save(object);
	}
}
