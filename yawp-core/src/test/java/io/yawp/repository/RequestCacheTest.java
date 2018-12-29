package io.yawp.repository;

import io.yawp.commons.utils.EndpointTestCase;
import io.yawp.repository.models.parents.Child;
import io.yawp.repository.query.NoResultException;
import org.junit.Test;

import static junit.framework.TestCase.assertNull;
import static org.hamcrest.core.Is.is;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.fail;

public class RequestCacheTest extends EndpointTestCase {

    @Test
    public void testFetchUseCache() {
        Child c1 = yawp.save(new Child("c1"));

        assertThat(c1.getId().fetch().getName(), is("c1"));
        yawp.destroy(c1.getId());
        assertThat(c1.getId().fetch().getName(), is("c1"));
    }

    @Test
    public void testRefecth() {
        Child c1 = yawp.save(new Child("c1"));

        assertThat(c1.getId().fetch().getName(), is("c1"));
        yawp.destroy(c1.getId());
        try {
            c1.getId().refetch();
        } catch (NoResultException ex) {
            return;
        }
        fail("Exception NoResultException should have been thrown...");
    }
}
