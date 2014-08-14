package endpoint.hooks;

import endpoint.SimpleObject;
import endpoint.Target;

@Target(SimpleObject.class)
public class SimpleObjectHook extends Hook {

	public void afterSave(SimpleObject object) {
		object.setChangeInCallback("just rock it");
	}

}
