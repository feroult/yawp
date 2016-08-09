package io.yawp.testing.appengine.models;

import io.yawp.repository.IdRef;
import io.yawp.repository.annotations.Endpoint;
import io.yawp.repository.annotations.Id;

@Endpoint(path = "/likes")
public class Like {

    @Id
    private IdRef<Like> id;

    private IdRef<Counter> counterId;

    public IdRef<Counter> getCounterId() {
        return counterId;
    }

    public void setId(IdRef<Like> id) {
        this.id = id;
    }

    public IdRef<Like> getId() {
        return id;
    }

    public void setCounterId(IdRef<Counter> counterId) {
        this.counterId = counterId;
    }
}
