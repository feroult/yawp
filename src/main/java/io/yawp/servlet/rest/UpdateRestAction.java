package io.yawp.servlet.rest;

import io.yawp.utils.EntityUtils;
import io.yawp.utils.JsonUtils;

public class UpdateRestAction extends RestAction {

	public UpdateRestAction() {
		super("update");
	}

	@Override
	public void shield() {
		// TODO Auto-generated method stub

	}

	@Override
	public Object action() {
		assert !JsonUtils.isJsonArray(requestJson);

		Object object = JsonUtils.from(r, requestJson, endpointClazz);
		forceObjectIdFromRequest(object);
		save(object);

		return transform(object);
	}

	private void forceObjectIdFromRequest(Object object) {
		EntityUtils.setId(object, id);
	}

}
