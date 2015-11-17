package io.yawp.repository.models.basic;

import io.yawp.repository.IdRef;
import io.yawp.repository.annotations.Endpoint;
import io.yawp.repository.annotations.Id;

@Endpoint(path = "/shielded_objects_with_defaults")
public class ShieldedObjectWithDefaults {

    @Id
    private IdRef<ShieldedObjectWithDefaults> id;

    public IdRef<ShieldedObjectWithDefaults> getId() {
        return id;
    }

    public void setId(IdRef<ShieldedObjectWithDefaults> id) {
        this.id = id;
    }

}
