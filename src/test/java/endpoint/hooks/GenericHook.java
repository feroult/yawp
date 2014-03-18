package endpoint.hooks;

import endpoint.DatastoreObject;
import endpoint.hooks.Hook;
import endpoint.hooks.HookTest.Product;

public class GenericHook extends Hook {
	public void afterSave(DatastoreObject o) {
		if (!Product.class.isInstance(o)) {
			return;
		}

		Product p = (Product) o;
		p.setName(p.getName() + " GenericHook touch");
	}
}
