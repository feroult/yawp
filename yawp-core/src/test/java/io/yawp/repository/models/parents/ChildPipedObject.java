package io.yawp.repository.models.parents;

import io.yawp.repository.IdRef;
import io.yawp.repository.annotations.Endpoint;
import io.yawp.repository.annotations.Id;
import io.yawp.repository.annotations.ParentId;
import io.yawp.repository.models.basic.BasicObject;

@Endpoint(path = "/child_piped_objects")
public class ChildPipedObject {

    @Id
    private IdRef<ChildPipedObject> id;

    @ParentId
    private IdRef<BasicObject> parentId;

    private IdRef<ChildPipedObjectSum> sumId;

    private Integer value;

    public IdRef<ChildPipedObject> getId() {
        return id;
    }

    public void setId(IdRef<ChildPipedObject> id) {
        this.id = id;
    }

    public IdRef<BasicObject> getParentId() {
        return parentId;
    }

    public void setParentId(IdRef<BasicObject> parentId) {
        this.parentId = parentId;
    }

    public IdRef<ChildPipedObjectSum> getSumId() {
        return sumId;
    }

    public void setSumId(IdRef<ChildPipedObjectSum> sumId) {
        this.sumId = sumId;
    }

    public Integer getValue() {
        return value;
    }

    public void setValue(Integer value) {
        this.value = value;
    }
}
