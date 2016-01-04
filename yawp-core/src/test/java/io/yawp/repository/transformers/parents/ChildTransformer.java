package io.yawp.repository.transformers.parents;

import io.yawp.repository.models.parents.Child;
import io.yawp.repository.transformers.Transformer;

public class ChildTransformer extends Transformer<Child> {

    public Child simple(Child child) {
        child.setName("transformed " + child.getName());
        return child;
    }

}
