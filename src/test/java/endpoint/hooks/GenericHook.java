package endpoint.hooks;

import endpoint.hooks.HookTest.Product;

public class GenericHook extends Hook {
	public void afterSave(Object o) {
		if (!Product.class.isInstance(o)) {
			return;
		}

		Product p = (Product) o;
		p.setName(p.getName() + " GenericHook touch");
	}
}
