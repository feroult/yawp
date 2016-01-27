package io.yawp.repository.pipes.basic;

import io.yawp.commons.utils.EndpointTestCase;
import io.yawp.repository.IdRef;
import io.yawp.repository.models.basic.BasicObject;
import io.yawp.repository.models.basic.BasicObjectCounter;
import org.junit.Test;

import static org.junit.Assert.assertEquals;

public class BasicObjectCounterPipeTest extends EndpointTestCase {

    @Test
    public void testOnlyIncrement() {
        if (pipesDriverNotImplemented()) {
            return;
        }

        yawp.save(new BasicObject("xpto"));
        yawp.save(new BasicObject("xpto"));

        BasicObjectCounter counter = yawp(BasicObjectCounter.class).only();

        assertEquals((Integer) 2, counter.getCount());
    }

    @Test
    public void testIncrementAndDecrement() {
        if (pipesDriverNotImplemented()) {
            return;
        }

        IdRef<BasicObject> object1Id = id(BasicObject.class, 1L);
        yawp.save(createObjectWithId(object1Id, "xpto"));
        yawp.save(new BasicObject("xpto"));

        BasicObjectCounter counter;

        counter = yawp(BasicObjectCounter.class).only();
        assertEquals((Integer) 2, counter.getCount());

        yawp.destroy(object1Id);
        counter = yawp(BasicObjectCounter.class).only();
        assertEquals((Integer) 1, counter.getCount());
    }

    @Test
    public void testCountByAttributeOnlyIncrement() {
        if (pipesDriverNotImplemented()) {
            return;
        }

        yawp.save(new BasicObject("group-a"));
        yawp.save(new BasicObject("group-b"));
        yawp.save(new BasicObject("xpto"));

        BasicObjectCounter counter = yawp(BasicObjectCounter.class).only();

        assertEquals((Integer) 3, counter.getCount());
        assertEquals((Integer) 1, counter.getCountGroupA());
        assertEquals((Integer) 1, counter.getCountGroupB());
    }

    private BasicObject createObjectWithId(IdRef<BasicObject> object1Id, String name) {
        BasicObject object1 = new BasicObject(name);
        object1.setId(object1Id);
        return object1;
    }

}
