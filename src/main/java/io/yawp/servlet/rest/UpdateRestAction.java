package io.yawp.servlet.rest;

import io.yawp.commons.utils.EntityUtils;

public class UpdateRestAction extends RestAction {

	public UpdateRestAction() {
		super("update");
	}

	@Override
	public void shield() {
		shield.protectUpdate();
	}

	@Override
	public Object action() {
		assert !isRequestBodyJsonArray();

		Object object = getObjectWithRightId();
		save(object);
		return transform(object);
	}

	private Object getObjectWithRightId() {
		Object object = getObject();
		forceObjectIdFromRequest(object);
		return object;
	}

	private void forceObjectIdFromRequest(Object object) {
		EntityUtils.setId(object, id);
		// TODO assert id != null && id.getObjectClazz = object.getClazz
		// TODO set parentId also
		// TODO assert request has id
	}

}
