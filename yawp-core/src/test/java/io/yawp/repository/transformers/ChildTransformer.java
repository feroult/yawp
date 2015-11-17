package io.yawp.repository.transformers;

import io.yawp.repository.models.parents.Child;

public class ChildTransformer extends Transformer<Child> {

    public Child simple(Child child) {
        child.setName("transformed " + child.getName());
        return child;
    }

}
