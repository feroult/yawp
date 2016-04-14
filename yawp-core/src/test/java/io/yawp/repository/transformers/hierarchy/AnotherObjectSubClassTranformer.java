package io.yawp.repository.transformers.hierarchy;

import io.yawp.repository.models.hierarchy.AnotherObjectSubClass;

import java.util.Map;

public class AnotherObjectSubClassTranformer extends ObjectSuperClassTranformer<AnotherObjectSubClass> {

    public Map<String, Object> upperCase(AnotherObjectSubClass object) {
        Map<String, Object> map = asMap(object);
        map.put("name", object.getName() + " + transformer");
        return map;
    }

}
