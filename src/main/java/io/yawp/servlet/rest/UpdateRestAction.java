package io.yawp.servlet.rest;

import io.yawp.utils.EntityUtils;
import io.yawp.utils.JsonUtils;

public class UpdateRestAction extends RestAction {

	@Override
	public Object action() {
		assert !JsonUtils.isJsonArray(requestJson);

		Object object = JsonUtils.from(r, requestJson, endpointClazz);
		EntityUtils.setId(object, id);
		save(object);

		return object;
	}

}
