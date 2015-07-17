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
		if (isRequestWithArray()) {
			return createFromArray(getObjects());
		}

		return createFromObject(getObject());
	}

	private Object createFromObject(Object object) {
		return saveObject(object);
	}

	private Object createFromArray(List<?> objects) {
		return saveObjecs(objects);
	}

	private Object saveObjecs(List<?> objects) {
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
		assertIdsAreCorrect(object);
		save(object);
		return transform(object);
	}

	protected FutureObject<Object> saveObjectAsync(Object object) {
		assertIdsAreCorrect(object);
		return saveAsync(object);
	}

	private void assertIdsAreCorrect(Object object) {
		if (id != null) {
			EntityUtils.setParentId(object, id);
		}
		// TODO assert id != null && object has parent && id.getClazz ==
		// object.getParentClazz
		// TODO assert if id == null && object has parent? ->
		// "object.getParentId()" != null
		// TODO assert object.getId().getParentId() == "object.getParentId()"
	}

}
