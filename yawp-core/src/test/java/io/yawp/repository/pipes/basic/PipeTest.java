package io.yawp.repository.pipes.basic;

import io.yawp.commons.utils.EndpointTestCase;
import io.yawp.repository.models.basic.BasicObject;
import io.yawp.repository.models.basic.BasicObjectCounter;
import org.junit.Ignore;
import org.junit.Test;

import static org.junit.Assert.assertEquals;

public class PipeTest extends EndpointTestCase {

    @Test
    @Ignore
    public void testSimpleCounter() {
        yawp.save(new BasicObject("xpto"));
        yawp.save(new BasicObject("xpto"));

        BasicObjectCounter counter = yawp(BasicObjectCounter.class).only();

        assertEquals((Integer) 2, counter.getCount());
    }


}
