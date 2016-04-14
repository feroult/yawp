package io.yawp.repository.hooks.hierarchy;

import io.yawp.repository.models.hierarchy.AnotherObjectSubClass;

public class AnotherObjectSubClassHook extends ObjectSuperClassHook<AnotherObjectSubClass> {

    @Override
    public void beforeSave(AnotherObjectSubClass object) {
        object.setName(object.getName() + " + more specific hook");
    }
}
