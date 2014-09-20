package io.yawp.servlet.rest;

public class CustomRestAction extends RestAction {

	@Override
	public Object action() {
		return r.action(id, endpointClazz, customActionKey, params);
	}

}
