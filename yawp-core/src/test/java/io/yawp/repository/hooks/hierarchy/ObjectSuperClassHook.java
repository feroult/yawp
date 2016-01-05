package io.yawp.repository.hooks.hierarchy;

import io.yawp.repository.models.hierarchy.ObjectSuperClass;

public class ObjectSuperClassHook<T extends ObjectSuperClass> extends AbstractHook<T> {

    @Override
    public void beforeSave(ObjectSuperClass object) {
        object.setName(object.getName() + " + superclass hook");
    }
}
