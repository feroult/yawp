package io.yawp.servlet.rest;

import io.yawp.utils.EntityUtils;

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
		assert !isJsonArray();

		Object object = getObject();

		forceObjectIdFromRequest(object);
		save(object);

		return transform(object);
	}

	private void forceObjectIdFromRequest(Object object) {
		EntityUtils.setId(object, id);
	}

}
