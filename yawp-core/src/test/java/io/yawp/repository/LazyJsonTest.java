package io.yawp.repository;

import io.yawp.commons.utils.EndpointTestCase;
import io.yawp.commons.utils.JsonUtils;
import io.yawp.repository.models.parents.Job;
import io.yawp.repository.models.parents.LazyJob;

import org.junit.Assert;
import org.junit.Test;

public class LazyJsonTest extends EndpointTestCase {

	@Test
	public void parse() {
		Job job = new Job();
		job.setName("doctor");
		job.setId(IdRef.create(yawp, Job.class, 1l));

		LazyJson<Job> parsed = LazyJson.<Job> parse(JsonUtils.to(job), Job.class);
		Job job2 = parsed.get();
		Assert.assertEquals(job.getId(), job2.getId());
		Assert.assertEquals(job.getName(), job2.getName());
	}

	@Test
	public void lazyJobInsert() {
		Job job = basicJob();

		LazyJob lazyJob = new LazyJob();
		LazyJson<Job> lazyJosn = LazyJson.<Job> from(job);
		lazyJob.setJob(lazyJosn);
		lazyJob = yawp.save(lazyJob);

		Assert.assertEquals(JsonUtils.to(job), lazyJosn.getJson());
		Assert.assertEquals(job.getId(), lazyJosn.get().getId());
		Assert.assertEquals(job.getName(), lazyJosn.get().getName());
	}

	private Job basicJob() {
		Job job = new Job();
		job.setName("doctor");
		job = yawp.save(job);
		return job;
	}

	@Test
	public void lazyFromJsont() {
		Job job = basicJob();

		LazyJob lazyJob = new LazyJob();
		LazyJson<Job> lazyJosn = LazyJson.<Job> from(job);
		lazyJob.setJob(lazyJosn);
		lazyJob = yawp.save(lazyJob);

		String lazyJsonReturnedFromDataStore = "{id:'" + lazyJob.getId().getUri() + "', job : " + lazyJosn.toString() + "}";
		LazyJob lazyJobFetched = JsonUtils.from(yawp, lazyJsonReturnedFromDataStore, LazyJob.class);

		Assert.assertEquals(lazyJob.getId(), lazyJobFetched.getId());
		Assert.assertEquals(lazyJob.getJob().getJson(), lazyJobFetched.getJob().getJson());
		Assert.assertEquals(lazyJob.getJob().get().getId(), lazyJobFetched.getJob().get().getId());
		Assert.assertEquals(lazyJob.getJob().get().getName(), lazyJobFetched.getJob().get().getName());

	}

}
