package io.yawp.repository.actions.hierarchy;

import io.yawp.commons.http.annotation.GET;
import io.yawp.repository.IdRef;
import io.yawp.repository.actions.Action;

import java.util.Map;

public class AllObjectsAction extends Action<Object> {

    @GET
    public String allObjects(IdRef<?> id) {
        Object object = id.fetch();
        Map<String, Object> map = asMap(object);
        return map.get("stringValue") + " all objects action";
    }
}
