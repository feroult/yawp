package endpoint.hooks;

public class AllTargetsHook extends Hook {
	public void afterSave(Object o) {
		if (!Product.class.isInstance(o)) {
			return;
		}

		Product p = (Product) o;
		p.setName(p.getName() + " GenericHook touch");
	}
}
