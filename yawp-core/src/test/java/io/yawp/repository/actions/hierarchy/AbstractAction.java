package io.yawp.repository.actions.hierarchy;

import io.yawp.commons.http.annotation.GET;
import io.yawp.repository.IdRef;
import io.yawp.repository.actions.Action;

import java.util.Map;

public class AbstractAction<T> extends Action<T> {

    @GET
    public String allObjects(IdRef<?> id) {
        Object object = id.fetch();
        Map<String, Object> map = asMap(object);
        return map.get("name") + " + all objects action";
    }

}
