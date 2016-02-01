package io.yawp.repository.pipes.basic;

import io.yawp.commons.utils.EndpointTestCase;
import io.yawp.repository.IdRef;
import io.yawp.repository.models.basic.BasicObject;
import io.yawp.repository.models.basic.BasicObjectCounter;
import org.junit.Test;

import java.util.Random;

import static org.junit.Assert.assertEquals;

public class BasicObjectCounterPipeTest extends EndpointTestCase {

    private String[] groups = new String[]{"group-a", "group-b"};

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

        IdRef<BasicObject> object100Id = saveObjectInGroup(100L, "xpto");
        yawp.save(new BasicObject("xpto"));

        BasicObjectCounter counter;

        counter = yawp(BasicObjectCounter.class).only();
        assertEquals((Integer) 2, counter.getCount());

        yawp.destroy(object100Id);

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


    @Test
    public void testCountByAttributeDecrement() {
        if (pipesDriverNotImplemented()) {
            return;
        }

        yawp.save(new BasicObject("group-a"));
        IdRef<BasicObject> objectInGroupBId = saveObjectInGroup(100L, "group-b");
        yawp.save(new BasicObject("xpto"));

        BasicObjectCounter counter;

        counter = yawp(BasicObjectCounter.class).only();
        assertEquals((Integer) 3, counter.getCount());
        assertEquals((Integer) 1, counter.getCountGroupA());
        assertEquals((Integer) 1, counter.getCountGroupB());

        yawp.destroy(objectInGroupBId);

        counter = yawp(BasicObjectCounter.class).only();
        assertEquals((Integer) 2, counter.getCount());
        assertEquals((Integer) 1, counter.getCountGroupA());
        assertEquals((Integer) 0, counter.getCountGroupB());
    }

    @Test
    public void testCountByAttributeDecrementByUpdate() {
        if (pipesDriverNotImplemented()) {
            return;
        }

        BasicObject objectInGroupB = new BasicObject("group-b");

        yawp.save(new BasicObject("group-a"));
        yawp.save(objectInGroupB);
        yawp.save(new BasicObject("xpto"));

        BasicObjectCounter counter;

        counter = yawp(BasicObjectCounter.class).only();
        assertEquals((Integer) 3, counter.getCount());
        assertEquals((Integer) 1, counter.getCountGroupA());
        assertEquals((Integer) 1, counter.getCountGroupB());

        objectInGroupB.setStringValue("group-a");
        yawp.save(objectInGroupB);

        counter = yawp(BasicObjectCounter.class).only();
        assertEquals((Integer) 3, counter.getCount());
        assertEquals((Integer) 2, counter.getCountGroupA());
        assertEquals((Integer) 0, counter.getCountGroupB());
    }

    private IdRef<BasicObject> saveObjectInGroup(long idAsLong, String group) {
        IdRef<BasicObject> id = id(BasicObject.class, idAsLong);
        BasicObject object = new BasicObject();
        object.setId(id);
        object.setStringValue(group);
        yawp.save(object);
        return id;
    }

    private int random(int minimum, int maximum) {
        Random rand = new Random();
        return minimum + rand.nextInt((maximum - minimum) + 1);
    }

}
