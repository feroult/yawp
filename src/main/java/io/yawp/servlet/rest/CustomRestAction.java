package io.yawp.servlet.rest;

public class CustomRestAction extends RestAction {

	public CustomRestAction() {
		super("custom");
	}

	@Override
	public Object action() {
		return r.action(id, endpointClazz, customActionKey, params);
	}

}
