package endpoint.repository.models.parents;

import java.util.List;

import endpoint.repository.IdRef;
import endpoint.repository.annotations.Endpoint;
import endpoint.repository.annotations.Id;
import endpoint.repository.annotations.Index;
import endpoint.repository.annotations.Json;

@Endpoint(path = "/parents")
public class Parent {

	@Id
	private IdRef<Parent> id;

	@Index
	private String name;

	@Index
	private IdRef<Job> jobId;

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
