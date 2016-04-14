package io.yawp.repository.models.basic;

import io.yawp.repository.IdRef;
import io.yawp.repository.annotations.Endpoint;
import io.yawp.repository.annotations.Id;

import static io.yawp.repository.Yawp.yawp;

@Endpoint(path = "/shuffled_objects")
public class ShuffledObject {

    @Id(shuffle = true)
    private IdRef<ShuffledObject> id;

    public ShuffledObject() {

    }

    public ShuffledObject(String uri) {
        this.id = yawp().parseId(ShuffledObject.class, uri);
    }

    public IdRef<ShuffledObject> getId() {
        return id;
    }

    public void setId(IdRef<ShuffledObject> id) {
        this.id = id;
    }
}
