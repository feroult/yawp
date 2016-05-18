package io.yawp.repository;

import io.yawp.commons.utils.EndpointTestCase;
import io.yawp.commons.utils.JsonUtils;
import io.yawp.repository.models.parents.Job;
import io.yawp.repository.models.parents.LazyJob;

import org.junit.Assert;
import org.junit.Test;

import com.google.gson.JsonObject;

public class LazyJsonTest extends EndpointTestCase {

	@Test
	public void parse() {
		JsonObject object = new JsonObject();
		Job job = new Job();
		job.setName("doctor");
		job.setId(IdRef.create(yawp, Job.class, 1l));

		object.addProperty("json", JsonUtils.to(job));
		object.addProperty("clazz", job.getClass().getName());

		LazyJson<Job> parsed = LazyJson.<Job> parse(object);
		Job job2 = parsed.get();
		Assert.assertEquals(job.getId(), job2.getId());
		Assert.assertEquals(job.getName(), job2.getName());
	}

	@Test
	public void lazyJobInsert() {
		Job job = new Job();
		job.setName("doctor");
		job = yawp.save(job);

		LazyJob lazyJob = new LazyJob();
		LazyJson<Job> lazyJosn = LazyJson.<Job> from(job);
		lazyJob.setJob(lazyJosn);
		lazyJob = yawp.save(lazyJob);

		Assert.assertEquals(job.getClass(), lazyJosn.getClazz());
		Assert.assertEquals(JsonUtils.to(job), lazyJosn.getJson());
		Assert.assertEquals(job.getId(), lazyJosn.get().getId());
		Assert.assertEquals(job.getName(), lazyJosn.get().getName());

	}

}
