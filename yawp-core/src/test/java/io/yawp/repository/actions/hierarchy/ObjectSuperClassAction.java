package io.yawp.repository.actions.hierarchy;

import io.yawp.commons.http.annotation.GET;
import io.yawp.repository.IdRef;
import io.yawp.repository.models.hierarchy.ObjectSuperClass;

import java.util.Map;

public class ObjectSuperClassAction<T extends ObjectSuperClass> extends AbstractAction<T> {

    @GET
    public String superclassAction(IdRef<?> id) {
        Object object = id.fetch();
        Map<String, Object> map = asMap(object);
        return map.get("name") + " + superclass action";
    }
}
