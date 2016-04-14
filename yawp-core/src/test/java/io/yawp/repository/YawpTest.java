package io.yawp.repository;

import io.yawp.commons.utils.EndpointTestCase;
import io.yawp.repository.models.basic.BasicObject;
import org.junit.Test;

import static org.junit.Assert.assertEquals;

public class YawpTest extends EndpointTestCase {

    @Test
    public void testSimpleRepositoryAccess() {
        Yawp.yawp.save(new BasicObject("xpto"));
        BasicObject retrievedObject = Yawp.yawp(BasicObject.class).first();
        assertEquals("xpto", retrievedObject.getStringValue());
    }

}
