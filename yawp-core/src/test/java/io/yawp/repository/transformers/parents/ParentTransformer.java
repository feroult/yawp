package io.yawp.repository.transformers.parents;

import io.yawp.repository.models.parents.Parent;
import io.yawp.repository.transformers.Transformer;

import java.util.Random;

public class ParentTransformer extends Transformer<Parent> {

    private final int random;

    public ParentTransformer() {
        this.random = new Random().nextInt();
    }

    public Parent upperCase(Parent parent) {
        parent.setName(parent.getName().toUpperCase() + "-" + random);
        return parent;
    }
}
