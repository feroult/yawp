package io.yawp.servlet.rest;

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

		Object object = getObject();
		save(object);
		return transform(object);
	}

}
