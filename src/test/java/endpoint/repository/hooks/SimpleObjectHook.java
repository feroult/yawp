package endpoint.repository.hooks;

import endpoint.repository.SimpleObject;

public class SimpleObjectHook extends Hook<SimpleObject> {

	@Override
	public void afterSave(SimpleObject object) {
		object.setChangeInCallback("just rock it");
	}

}
