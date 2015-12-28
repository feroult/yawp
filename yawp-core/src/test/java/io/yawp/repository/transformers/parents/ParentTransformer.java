package io.yawp.repository.transformers.parents;

import io.yawp.repository.models.parents.Parent;

public class ParentTransformer extends AbstractTransformer<Parent> {

    public Parent upperCase(Parent parent) {
        parent.setName(parent.getName().toUpperCase());
        return parent;
    }
}
