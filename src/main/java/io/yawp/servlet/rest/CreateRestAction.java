package io.yawp.servlet.rest;

import io.yawp.repository.FutureObject;
import io.yawp.utils.EntityUtils;
import io.yawp.utils.JsonUtils;

import java.util.ArrayList;
import java.util.List;

public class CreateRestAction extends RestAction {

	public CreateRestAction() {
		super("create");
	}

	@Override
	public void shield() {
		shield.protectCreate();
	}

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

		List<FutureObject<Object>> futures = new ArrayList<FutureObject<Object>>();
		List<Object> resultObjects = new ArrayList<Object>();

		for (Object object : objects) {
			futures.add(saveObjectAsync(object));
		}

		for (FutureObject<Object> future : futures) {
			resultObjects.add(transform(future.get()));
		}

		return resultObjects;
	}

	protected Object saveObject(Object object) {
		if (id != null) {
			EntityUtils.setParentId(object, id);
		}
		save(object);
		return transform(object);
	}

	protected FutureObject<Object> saveObjectAsync(Object object) {
		if (id != null) {
			EntityUtils.setParentId(object, id);
		}
		return saveAsync(object);
		// return transform(object);
	}

}
