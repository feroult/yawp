package io.yawp.repository.transformers.parents;

import io.yawp.repository.models.parents.Parent;
import io.yawp.repository.transformers.Transformer;

public class ParentTransformer extends Transformer<Parent> {

    public Parent upperCase(Parent parent) {
        parent.setName(parent.getName().toUpperCase());
        return parent;
    }
}
