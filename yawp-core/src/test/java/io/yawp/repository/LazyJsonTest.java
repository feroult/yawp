package io.yawp.repository;

import io.yawp.commons.utils.EndpointTestCase;
import io.yawp.commons.utils.JsonUtils;
import io.yawp.repository.models.parents.Job;

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

		LazyJson parsed = LazyJson.parse(object);
		Job job2 = (Job) parsed.get();
		Assert.assertEquals(job.getId(), job2.getId());
		Assert.assertEquals(job.getName(), job2.getName());
	}

}
