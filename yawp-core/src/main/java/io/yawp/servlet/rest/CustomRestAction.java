package io.yawp.servlet.rest;

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

		applyGetFacade(object);
		if (hasTransformer()) {
			return transform(object);
		}

		return object;
	}
}
