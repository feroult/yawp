package endpoint.models.hooks;

import endpoint.models.SimpleObject;
import endpoint.models.Target;
import endpoint.models.hooks.Hook;

@Target(SimpleObject.class)
public class SimpleObjectHook extends Hook {

	public void afterSave(SimpleObject object) {
		object.setChangeInCallback("just rock it");
	}

}
