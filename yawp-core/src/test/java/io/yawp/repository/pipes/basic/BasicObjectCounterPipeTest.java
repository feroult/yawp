package io.yawp.repository.pipes.basic;

import io.yawp.commons.utils.EndpointTestCase;
import io.yawp.repository.IdRef;
import io.yawp.repository.models.basic.BasicObject;
import io.yawp.repository.models.basic.BasicObjectCounter;
import org.junit.Ignore;
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
    public void testDecrement() {
        if (pipesDriverNotImplemented()) {
            return;
        }

        IdRef<BasicObject> object1Id = id(BasicObject.class, 1L);
        yawp.save(createObjectWithId(object1Id, "xpto"));
        yawp.save(new BasicObject("xpto"));

        yawp.destroy(object1Id);
        BasicObjectCounter counter = yawp(BasicObjectCounter.class).only();
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


    @Test
    public void testCountByAttributeDecrement() {
        if (pipesDriverNotImplemented()) {
            return;
        }

        IdRef<BasicObject> objectInGroupBId = id(BasicObject.class, 1L);

        yawp.save(new BasicObject("group-a"));
        yawp.save(createObjectWithId(objectInGroupBId, "group-b"));
        yawp.save(new BasicObject("xpto"));

        yawp.destroy(objectInGroupBId);

        BasicObjectCounter counter = yawp(BasicObjectCounter.class).only();

        assertEquals((Integer) 2, counter.getCount());
        assertEquals((Integer) 1, counter.getCountGroupA());
        assertEquals((Integer) 0, counter.getCountGroupB());
    }

    @Test
    @Ignore
    public void testCountByAttributeDecrementByUpdate() {
        if (pipesDriverNotImplemented()) {
            return;
        }

        BasicObject objectInGroupB = new BasicObject("group-b");

        yawp.save(new BasicObject("group-a"));
        yawp.save(objectInGroupB);
        yawp.save(new BasicObject("xpto"));

        objectInGroupB.setStringValue("group-a");
        yawp.save(objectInGroupB);

        BasicObjectCounter counter = yawp(BasicObjectCounter.class).only();

        assertEquals((Integer) 3, counter.getCount());
        assertEquals((Integer) 2, counter.getCountGroupA());
        assertEquals((Integer) 0, counter.getCountGroupB());
    }

    private BasicObject createObjectWithId(IdRef<BasicObject> object1Id, String name) {
        BasicObject object1 = new BasicObject(name);
        object1.setId(object1Id);
        return object1;
    }

}
