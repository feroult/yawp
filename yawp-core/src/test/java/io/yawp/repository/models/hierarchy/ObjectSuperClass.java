package io.yawp.repository.models.hierarchy;

import io.yawp.repository.IdRef;
import io.yawp.repository.annotations.Id;
import io.yawp.repository.annotations.Index;

public class ObjectSuperClass<T> {

    @Index
    protected String name;

    @Id
    private IdRef<T> id;

    public ObjectSuperClass(String name) {
        this.name = name;
    }

    public IdRef<T> getId() {
        return id;
    }

    public void setId(IdRef<T> id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

}
