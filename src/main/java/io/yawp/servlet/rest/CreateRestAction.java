package io.yawp.servlet.rest;

import io.yawp.repository.IdRef;
import io.yawp.utils.EntityUtils;
import io.yawp.utils.JsonUtils;

import java.util.ArrayList;
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
		return saveObject(object);
	}

	private Object createFromArray() {
		List<?> objects = JsonUtils.fromList(r, requestJson, endpointClazz);

		List<Object> resultObjects = new ArrayList<Object>();

		for (Object object : objects) {
			resultObjects.add(saveObject(object));
		}
		return resultObjects;
	}

	protected Object saveObject(Object object) {
		if (id != null) {
			saveWithParentId(object, id);
		} else {
			save(object);
		}

		return transformIfNecessary(object);
	}

	protected void saveWithParentId(Object object, IdRef<?> parentId) {
		EntityUtils.setParentId(object, parentId);
		save(object);
	}
}
