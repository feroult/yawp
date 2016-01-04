package io.yawp.repository.transformers.hierarchy;

import io.yawp.repository.models.hierarchy.ObjectSuperClass;

import java.util.Map;

public class ObjectSuperClassTranformer extends AbstractTransformer<ObjectSuperClass> {

    public Map<String, Object> upperCase(ObjectSuperClass object) {
        Map<String, Object> map = asMap(object);
        map.put("name", object.getName().toUpperCase());
        return map;
    }

}
