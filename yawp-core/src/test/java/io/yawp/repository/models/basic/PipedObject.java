package io.yawp.repository.models.basic;

import io.yawp.repository.IdRef;
import io.yawp.repository.annotations.Endpoint;
import io.yawp.repository.annotations.Id;

import static io.yawp.repository.Yawp.yawp;

@Endpoint(path = "/piped_objects")
public class PipedObject {

    @Id
    private IdRef<PipedObject> id;

    private IdRef<PipedObjectCounter> counterId;

    private String group;

    public PipedObject() {
        this(null);
    }

    public PipedObject(String group) {
        this(group, yawp.parseId(PipedObjectCounter.class, "/piped_object_counters/1"));
    }

    public PipedObject(String group, IdRef<PipedObjectCounter> counterId) {
        this.group = group;
        this.counterId = counterId;
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

    public IdRef<PipedObjectCounter> getCounterId() {
        return counterId;
    }

    public void setCounterId(IdRef<PipedObjectCounter> counterId) {
        this.counterId = counterId;
    }
}