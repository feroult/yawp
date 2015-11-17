package io.yawp.repository.models.parents;

import io.yawp.repository.IdRef;
import io.yawp.repository.annotations.Endpoint;
import io.yawp.repository.annotations.Id;
import io.yawp.repository.annotations.Index;
import io.yawp.repository.annotations.ParentId;

@Endpoint(path = "/children")
public class Child {

    @Id
    private IdRef<Child> id;

    @ParentId
    private IdRef<Parent> parentId;

    @Index
    private String name;

    public Child() {

    }

    public Child(String name) {
        this.name = name;
    }

    public Child(String name, IdRef<Parent> parentId) {
        this.name = name;
        this.parentId = parentId;
    }

    public IdRef<Child> getId() {
        return id;
    }

    public void setId(IdRef<Child> id) {
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
