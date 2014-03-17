package endpoint.models.hooks;

import endpoint.models.DatastoreObject;
import endpoint.models.hooks.Hook;
import endpoint.models.hooks.HookTest.Product;

public class GenericHook extends Hook {
	public void afterSave(DatastoreObject o) {
		if (!Product.class.isInstance(o)) {
			return;
		}

		Product p = (Product) o;
		p.setName(p.getName() + " GenericHook touch");
	}
}
