package io.yawp.servlet.rest;

import io.yawp.servlet.HttpException;

public class CustomRestAction extends RestAction {

	public CustomRestAction() {
		super("custom");
	}


	@Override
	public void shield() {
		// TODO Auto-generated method stub

	}

	@Override
	public Object action() {
		Object object = r.action(id, endpointClazz, customActionKey, params);

		if (object == null) {
			return null;
		}

		if (hasTransformer()) {
			if (!object.getClass().equals(endpointClazz)) {
				throw new HttpException(406);
			}
			return transform(object);
		}

		return object;
	}

}
