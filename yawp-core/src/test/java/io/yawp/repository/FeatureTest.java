package io.yawp.repository;

import io.yawp.commons.utils.EndpointTestCase;
import io.yawp.repository.models.parents.Job;
import org.junit.Test;

import static org.junit.Assert.assertEquals;

public class FeatureTest extends EndpointTestCase {

    @Test
    public void testIdHelper() {
        Feature feature = new Feature();
        feature.setRepository(yawp);

        IdRef<Job> jobId = feature.id("/jobs/1").of(Job.class);
        assertEquals((Long) 1l, jobId.getId());
        assertEquals(Job.class, jobId.getClazz());
    }

}
