package io.yawp.repository.hooks.hierarchy;

import io.yawp.repository.models.hierarchy.ObjectSuperClass;

public class ObjectSuperClassHook extends AbstractHook<ObjectSuperClass> {

    @Override
    public void beforeSave(ObjectSuperClass object) {
        object.setName(object.getName().toUpperCase());
    }
}
