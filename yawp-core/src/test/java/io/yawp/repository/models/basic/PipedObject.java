package io.yawp.repository.models.basic;

import io.yawp.repository.IdRef;
import io.yawp.repository.annotations.Endpoint;
import io.yawp.repository.annotations.Id;

@Endpoint(path = "/piped_objects")
public class PipedObject {

    @Id
    private IdRef<PipedObject> id;

    private String group;

    public PipedObject() {
    }

    public PipedObject(String group) {
        this.group = group;
    }

    public IdRef<PipedObject> getId() {
        return id;
    }

    public void setId(IdRef<PipedObject> id) {
        this.id = id;
    }

    public String getGroup() {
        return group;
    }

    public void setGroup(String group) {
        this.group = group;
    }
}