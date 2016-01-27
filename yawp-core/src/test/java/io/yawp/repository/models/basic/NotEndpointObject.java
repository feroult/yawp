package io.yawp.repository.models.basic;

import io.yawp.repository.IdRef;
import io.yawp.repository.annotations.Id;
import io.yawp.repository.annotations.Kind;
import io.yawp.repository.annotations.ParentId;

@Kind("not_endpoint_object_kind")
public class NotEndpointObject {

    @Id
    private IdRef<NotEndpointObject> id;

    @ParentId
    private IdRef<?> parentId;

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
