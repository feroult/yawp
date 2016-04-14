package io.yawp.repository.actions.hierarchy;

import io.yawp.commons.http.annotation.GET;
import io.yawp.repository.IdRef;
import io.yawp.repository.models.hierarchy.AnotherObjectSubClass;

import java.util.Map;

public class AnotherObjectSubClassAction extends ObjectSuperClassAction<AnotherObjectSubClass> {

    @GET
    public String superclassAction(IdRef<?> id) {
        Object object = id.fetch();
        Map<String, Object> map = asMap(object);
        return map.get("name") + " + action";
    }
}
