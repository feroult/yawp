package io.yawp.repository.models.parents;

import io.yawp.repository.IdRef;
import io.yawp.repository.LazyJson;
import io.yawp.repository.annotations.Endpoint;
import io.yawp.repository.annotations.Id;
import io.yawp.repository.annotations.Index;
import io.yawp.repository.annotations.Json;

import java.util.List;

@Endpoint(path = "/parents")
public class Parent {

    @Id
    private IdRef<Parent> id;

    @Index
    private String name;

    @Index
    private IdRef<Job> jobId;

    private LazyJson<Job> job;

    @Json
    private List<IdRef<Job>> pastJobIds;

    public Parent() {

    }

    public Parent(String name) {
        this.name = name;
    }

    public IdRef<Parent> getId() {
        return id;
    }

    public void setId(IdRef<Parent> id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public IdRef<Job> getJobId() {
        return jobId;
    }

    public void setJobId(IdRef<Job> jobId) {
        this.jobId = jobId;
    }

    public List<IdRef<Job>> getPastJobIds() {
        return pastJobIds;
    }

    public void setPastJobIds(List<IdRef<Job>> pastJobIds) {
        this.pastJobIds = pastJobIds;
    }
}
