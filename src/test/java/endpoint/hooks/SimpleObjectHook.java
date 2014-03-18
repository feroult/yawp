package endpoint.hooks;

import endpoint.SimpleObject;
import endpoint.Target;
import endpoint.hooks.Hook;

@Target(SimpleObject.class)
public class SimpleObjectHook extends Hook {

	public void afterSave(SimpleObject object) {
		object.setChangeInCallback("just rock it");
	}

}
