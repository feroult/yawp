package io.yawp.repository.models.parents;

import io.yawp.repository.IdRef;
import io.yawp.repository.annotations.Endpoint;
import io.yawp.repository.annotations.Id;

@Endpoint(path = "/jobs")
public class Job {

    @Id
    private IdRef<Job> id;

    private String name;

    public Job() {

    }

    public Job(String name) {
        this.name = name;
    }

    public IdRef<Job> getId() {
        return id;
    }

    public void setId(IdRef<Job> id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

}
