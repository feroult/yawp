package io.yawp.repository.models.parents;

import io.yawp.repository.IdRef;
import io.yawp.repository.annotations.Endpoint;
import io.yawp.repository.annotations.Id;
import io.yawp.repository.annotations.Index;
import io.yawp.repository.annotations.ParentId;

@Endpoint(path = "/shielded_children")
public class ShieldedChild {

    @Id
    private IdRef<ShieldedChild> id;

    @ParentId
    private IdRef<Parent> parentId;

    @Index
    private String name;

    public ShieldedChild() {

    }

    public ShieldedChild(String name) {
        this.name = name;
    }

    public IdRef<ShieldedChild> getId() {
        return id;
    }

    public void setId(IdRef<ShieldedChild> id) {
        this.id = id;
    }

    public IdRef<Parent> getParentId() {
        return parentId;
    }

    public void setParentId(IdRef<Parent> parentId) {
        this.parentId = parentId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

}
