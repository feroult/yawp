package endpoint.repository.transformers;

import endpoint.repository.models.parents.Child;

public class ChildTransformer extends Transformer<Child> {

	public Child simple(Child child) {
		child.setName("transformed " + child.getName());
		return child;
	}

}
