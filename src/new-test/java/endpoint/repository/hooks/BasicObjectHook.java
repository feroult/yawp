package endpoint.repository.hooks;

import endpoint.repository.models.basic.BasicObject;

public class BasicObjectHook extends Hook<BasicObject> {

	@Override
	public void afterSave(BasicObject object) {
		if (!isHookTest(object)) {
			return;
		}
		object.setStringValue("xpto");
	}

	private boolean isHookTest(BasicObject object) {
		return object.getStringValue() != null && object.getStringValue().equals("hook_test");
	}

}
