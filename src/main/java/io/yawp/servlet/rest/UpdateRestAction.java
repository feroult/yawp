package io.yawp.servlet.rest;

import io.yawp.utils.EntityUtils;
import io.yawp.utils.JsonUtils;

public class UpdateRestAction extends RestAction {

	@Override
	public Object action() {
		assert !JsonUtils.isJsonArray(requestJson);

		Object object = JsonUtils.from(r, requestJson, endpointClazz);
		forceObjectIdFromRequest(object);
		save(object);

		return object;
	}

	private void forceObjectIdFromRequest(Object object) {
		EntityUtils.setId(object, id);
	}

}
