package io.yawp.repository.shields;

import io.yawp.commons.http.annotation.PUT;
import io.yawp.repository.IdRef;
import io.yawp.repository.models.basic.ShieldedObjectWithDefaults;

public class ObjectShieldWithDefaults extends Shield<ShieldedObjectWithDefaults> {

    @Override
    public void defaults() {
        allow();
    }

    @Override
    public void destroy(IdRef<ShieldedObjectWithDefaults> id) {
    }

    @PUT("something")
    public void something(IdRef<ShieldedObjectWithDefaults> id) {
    }

}
