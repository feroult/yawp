package io.yawp.repository.actions.basic;

import io.yawp.commons.http.annotation.GET;
import io.yawp.repository.actions.Action;
import io.yawp.repository.models.basic.ShieldedObject;

public class HeaderAction extends Action<ShieldedObject> {

    @GET("header")
    public String header() {
        return "action:" + requestContext.req().getHeader("Secret-Panda");
    }

}
