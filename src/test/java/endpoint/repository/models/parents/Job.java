package endpoint.repository.models.parents;

import endpoint.repository.IdRef;
import endpoint.repository.annotations.Endpoint;
import endpoint.repository.annotations.Id;

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
