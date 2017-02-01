package io.yawp.repository;

import io.yawp.commons.utils.EndpointTestCase;
import io.yawp.repository.models.parents.Job;
import io.yawp.repository.models.parents.Parent;
import org.junit.Test;

import static org.junit.Assert.*;

public class IdRefHelpersTest extends EndpointTestCase {

    @Test
    public void testHasSameValue() {
        assertTrue(id(Parent.class, 1l).hasSameValue(id(Job.class, 1l)));
        assertTrue(id(Parent.class, "x").hasSameValue(id(Job.class, "x")));
        assertFalse(id(Parent.class, 1l).hasSameValue(id(Job.class, 2l)));
        assertFalse(id(Parent.class, "x").hasSameValue(id(Job.class, "y")));
    }

    @Test
    public void testFork() {
        IdRef<Job> jobId = id(Parent.class, 1l).createSiblingId(Job.class);
        assertEquals((Long) 1l, jobId.getId());
    }


}
