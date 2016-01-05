package io.yawp.repository.shields.hierarchy;

import io.yawp.repository.models.hierarchy.ObjectSuperClass;

import java.util.List;

public class ObjectSuperClassShield<T extends ObjectSuperClass> extends AbstractShield<T> {

    @Override
    public void create(List<T> objects) {
        if (objects.size() != 1) {
            allow();
        }
        String name = objects.get(0).getName();
        if (name == null || !name.equals("block this case")) {
            allow();
        }
    }
}
