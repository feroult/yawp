package io.yawp.repository.actions.basic;

import io.yawp.commons.http.annotation.PUT;
import io.yawp.repository.IdRef;
import io.yawp.repository.actions.Action;
import io.yawp.repository.models.basic.ShieldedObjectWithDefaults;

public class ShieldedObjectWithDefaultsAction extends Action<ShieldedObjectWithDefaults> {

    @PUT("something")
    public void something(IdRef<ShieldedObjectWithDefaults> id) {
    }

}
