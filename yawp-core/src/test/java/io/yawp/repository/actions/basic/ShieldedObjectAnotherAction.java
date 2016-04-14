package io.yawp.repository.actions.basic;

import io.yawp.commons.http.annotation.GET;
import io.yawp.repository.actions.Action;
import io.yawp.repository.models.basic.ShieldedObject;

public class ShieldedObjectAnotherAction extends Action<ShieldedObject> {

    @GET("another-action-class")
    public void collection() {
    }

}
