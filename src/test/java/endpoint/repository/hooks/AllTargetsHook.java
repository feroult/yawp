package endpoint.repository.hooks;

public class AllTargetsHook extends Hook<Object> {

	@Override
	public void afterSave(Object o) {
		if (!Product.class.isInstance(o)) {
			return;
		}

		Product p = (Product) o;
		p.setName(p.getName() + " GenericHook touch");
	}
}
