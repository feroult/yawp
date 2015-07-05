package io.yawp.servlet.rest;

import io.yawp.commons.utils.EntityUtils;
import io.yawp.repository.FutureObject;

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
		if (isJsonArray()) {
			return createFromArray(getObjects());
		}

		return createFromObject(getObject());
	}

	private Object createFromObject(Object object) {
		return saveObject(object);
	}

	private Object createFromArray(List<?> objects) {
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
	}

}
