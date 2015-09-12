package io.yawp.repository.transformers;

import io.yawp.repository.models.parents.Parent;

public class ParentTransformer extends Transformer<Parent> {

	public Parent upperCase(Parent parent) {
		parent.setName(parent.getName().toUpperCase());
		return parent;
	}
}
