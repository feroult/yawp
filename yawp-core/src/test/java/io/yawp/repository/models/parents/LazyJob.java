package io.yawp.repository.models.parents;

import io.yawp.repository.IdRef;
import io.yawp.repository.LazyJson;
import io.yawp.repository.annotations.Endpoint;
import io.yawp.repository.annotations.Id;
import io.yawp.repository.annotations.Json;

@Endpoint(path = "/lazyJobs")
public class LazyJob {

	@Id
	private IdRef<LazyJob> id;
	
	@Json
	private LazyJson<Job> job;

	public LazyJob() {
	}

	public IdRef<LazyJob> getId() {
		return id;
	}

	public void setId(IdRef<LazyJob> id) {
		this.id = id;
	}

	public LazyJson<Job> getJob() {
		return job;
	}

	public void setJob(LazyJson<Job> job) {
		this.job = job;
	}

}
