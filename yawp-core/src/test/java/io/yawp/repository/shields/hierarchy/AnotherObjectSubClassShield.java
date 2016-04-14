package io.yawp.repository.shields.hierarchy;

import io.yawp.repository.models.hierarchy.AnotherObjectSubClass;

import java.util.List;

public class AnotherObjectSubClassShield extends ObjectSuperClassShield<AnotherObjectSubClass> {

    @Override
    public void create(List<AnotherObjectSubClass> objects) {
        if (objects.size() != 1) {
            allow();
        }
        String name = objects.get(0).getName();
        if (name == null || !name.contains("block this case")) {
            allow();
        }
    }
}
