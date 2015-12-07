package io.yawp.driver.postgresql.datastore.models;

import io.yawp.repository.IdRef;
import io.yawp.repository.annotations.Endpoint;
import io.yawp.repository.annotations.Id;
import io.yawp.repository.annotations.Index;
import io.yawp.repository.annotations.ParentId;

@Endpoint(path = "/grandchildren")
public class Grandchild {

    @Id
    private IdRef<Grandchild> id;

    @ParentId
    private IdRef<Child> childId;

    @Index
    private String name;

    @Index
    private Integer age;

    protected IdRef<Grandchild> getId() {
        return id;
    }

    protected void setId(IdRef<Grandchild> id) {
        this.id = id;
    }

    protected String getName() {
        return name;
    }

    protected void setName(String name) {
        this.name = name;
    }

    protected Integer getAge() {
        return age;
    }

    protected void setAge(Integer age) {
        this.age = age;
    }
}
