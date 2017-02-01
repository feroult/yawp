package io.yawp.repository;

import io.yawp.commons.utils.EndpointTestCase;
import io.yawp.repository.models.parents.Job;
import io.yawp.repository.models.parents.Parent;
import org.junit.Test;

import static org.junit.Assert.*;

public class IdRefHelpersTest extends EndpointTestCase {

    @Test
    public void testCreateSiblingId() {
        IdRef<Job> jobId = id(Parent.class, 1l).createSiblingId(Job.class);
        assertEquals((Long) 1l, jobId.getId());
    }

    @Test
    public void testIsSibling() {
        assertTrue(id(Parent.class, 1l).isSibling(id(Job.class, 1l)));
        assertTrue(id(Parent.class, "x").isSibling(id(Job.class, "x")));
        assertFalse(id(Parent.class, 1l).isSibling(id(Job.class, 2l)));
        assertFalse(id(Parent.class, "x").isSibling(id(Job.class, "y")));
    }

}
