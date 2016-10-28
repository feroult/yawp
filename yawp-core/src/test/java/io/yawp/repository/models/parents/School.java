package io.yawp.repository.models.parents;

import io.yawp.repository.IdRef;
import io.yawp.repository.annotations.Endpoint;
import io.yawp.repository.annotations.Id;

import java.util.Arrays;
import java.util.List;

@Endpoint(path = "/schools")
public class School {

    @Id
    private IdRef<School> id;

    private String name;

    private List<IdRef<Child>> children;

    public School() {}

    public School(String name, IdRef<Child>... ids) {
        this.name = name;
        this.children = Arrays.asList(ids);
    }

    public IdRef<School> getId() {
        return id;
    }

    public void setId(IdRef<School> id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public List<IdRef<Child>> getChildren() {
        return children;
    }

    public void setChildren(List<IdRef<Child>> children) {
        this.children = children;
    }
}
