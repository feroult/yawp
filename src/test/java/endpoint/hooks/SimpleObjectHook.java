package endpoint.hooks;

import endpoint.SimpleObject;

public class SimpleObjectHook extends Hook<SimpleObject> {

	@Override
	public void afterSave(SimpleObject object) {
		object.setChangeInCallback("just rock it");
	}

}
