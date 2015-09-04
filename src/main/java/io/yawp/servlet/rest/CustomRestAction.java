package io.yawp.servlet.rest;

import io.yawp.commons.http.HttpException;

public class CustomRestAction extends RestAction {

	public CustomRestAction() {
		super("custom");
	}

	@Override
	public void shield() {
		shield.protectCustom();
	}

	@Override
	public Object action() {
		Object object = r.action(id, endpointClazz, customActionKey, params);

		if (object == null) {
			return null;
		}

		if (object.getClass().equals(endpointClazz)) {
			applyGetFacade(object);
			if (hasTransformer()) {
				return transform(object);
			}

		} else if (hasTransformer()) {
			throw new HttpException(406);
		}

		return object;
	}

}
