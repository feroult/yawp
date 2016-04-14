package io.yawp.repository.models.basic;

import io.yawp.repository.IdRef;
import io.yawp.repository.annotations.Endpoint;
import io.yawp.repository.annotations.Id;
import io.yawp.repository.annotations.ParentId;

@Endpoint(kind = "not_endpoint_object_kind")
public class NotEndpointObject {

    @Id
    private IdRef<NotEndpointObject> id;

    @ParentId
    private IdRef<BasicObject> parentId;

    private String name;

    public NotEndpointObject() {
    }

    public NotEndpointObject(String name) {
        this.name = name;
    }

    public IdRef<NotEndpointObject> getId() {
        return id;
    }

    public void setId(IdRef<NotEndpointObject> id) {
        this.id = id;
    }

    public IdRef<BasicObject> getParentId() {
        return parentId;
    }

    public void setParentId(IdRef<BasicObject> parentId) {
        this.parentId = parentId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
