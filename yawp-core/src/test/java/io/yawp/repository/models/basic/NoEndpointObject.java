package io.yawp.repository.models.basic;

import io.yawp.repository.IdRef;
import io.yawp.repository.annotations.Id;
import io.yawp.repository.annotations.ParentId;

public class NoEndpointObject {

    @Id
    private IdRef<NoEndpointObject> id;

    @ParentId
    private IdRef<?> parentId;

    private String name;

    public NoEndpointObject() {
    }

    public NoEndpointObject(String name) {
        this.name = name;
    }

    public IdRef<NoEndpointObject> getId() {
        return id;
    }

    public void setId(IdRef<NoEndpointObject> id) {
        this.id = id;
    }

    public IdRef<?> getParentId() {
        return parentId;
    }

    public void setParentId(IdRef<?> parentId) {
        this.parentId = parentId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
