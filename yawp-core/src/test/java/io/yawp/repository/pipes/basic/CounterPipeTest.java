package io.yawp.repository.pipes.basic;

import io.yawp.commons.utils.EndpointTestCase;
import io.yawp.repository.IdRef;
import io.yawp.repository.models.basic.PipedObject;
import io.yawp.repository.models.basic.PipedObjectCounter;
import org.junit.Ignore;
import org.junit.Test;

import java.util.Random;
import java.util.concurrent.TimeUnit;

import static org.junit.Assert.assertEquals;

public class CounterPipeTest extends EndpointTestCase {

    private String[] groups = new String[]{"group-a", "group-b"};

    @Test
    public void testOnlyIncrement() {
        if (pipesDriverNotImplemented()) {
            return;
        }

        yawp.save(new PipedObject("xpto"));
        yawp.save(new PipedObject("xpto"));
        awaitAsync(20, TimeUnit.SECONDS);

        PipedObjectCounter counter = yawp(PipedObjectCounter.class).only();
        assertEquals((Integer) 2, counter.getCount());
    }

    @Test
    public void testDecrement() {
        if (pipesDriverNotImplemented()) {
            return;
        }

        IdRef<PipedObject> object100Id = saveObjectInGroup(100L, "xpto");
        yawp.save(new PipedObject("xpto"));
        awaitAsync(20, TimeUnit.SECONDS);

        PipedObjectCounter counter;

        counter = yawp(PipedObjectCounter.class).only();
        assertEquals((Integer) 2, counter.getCount());

        yawp.destroy(object100Id);
        awaitAsync(20, TimeUnit.SECONDS);

        counter = yawp(PipedObjectCounter.class).only();
        assertEquals((Integer) 1, counter.getCount());
    }


    @Test
    public void testCountByAttributeOnlyIncrement() {
        if (pipesDriverNotImplemented()) {
            return;
        }

        yawp.save(new PipedObject("group-a"));
        yawp.save(new PipedObject("group-b"));
        yawp.save(new PipedObject("xpto"));
        awaitAsync(20, TimeUnit.SECONDS);

        PipedObjectCounter counter = yawp(PipedObjectCounter.class).only();

        assertEquals((Integer) 3, counter.getCount());
        assertEquals((Integer) 1, counter.getCountGroupA());
        assertEquals((Integer) 1, counter.getCountGroupB());
    }


    @Test
    public void testCountByAttributeDecrement() {
        if (pipesDriverNotImplemented()) {
            return;
        }

        yawp.save(new PipedObject("group-a"));
        IdRef<PipedObject> objectInGroupBId = saveObjectInGroup(100L, "group-b");
        yawp.save(new PipedObject("xpto"));
        awaitAsync(20, TimeUnit.SECONDS);

        PipedObjectCounter counter;

        counter = yawp(PipedObjectCounter.class).only();
        assertEquals((Integer) 3, counter.getCount());
        assertEquals((Integer) 1, counter.getCountGroupA());
        assertEquals((Integer) 1, counter.getCountGroupB());

        yawp.destroy(objectInGroupBId);
        awaitAsync(20, TimeUnit.SECONDS);

        counter = yawp(PipedObjectCounter.class).only();
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

        PipedObject objectInGroupB = new PipedObject("group-b");

        yawp.save(new PipedObject("group-a"));
        yawp.save(objectInGroupB);
        yawp.save(new PipedObject("xpto"));

        PipedObjectCounter counter;

        counter = yawp(PipedObjectCounter.class).only();
        assertEquals((Integer) 3, counter.getCount());
        assertEquals((Integer) 1, counter.getCountGroupA());
        assertEquals((Integer) 1, counter.getCountGroupB());

        objectInGroupB.setGroup("group-a");
        yawp.save(objectInGroupB);

        counter = yawp(PipedObjectCounter.class).only();
        assertEquals((Integer) 3, counter.getCount());
        assertEquals((Integer) 2, counter.getCountGroupA());
        assertEquals((Integer) 0, counter.getCountGroupB());
    }

    private IdRef<PipedObject> saveObjectInGroup(long idAsLong, String group) {
        IdRef<PipedObject> id = id(PipedObject.class, idAsLong);
        PipedObject object = new PipedObject();
        object.setId(id);
        object.setGroup(group);
        yawp.save(object);
        return id;
    }

    private int random(int minimum, int maximum) {
        Random rand = new Random();
        return minimum + rand.nextInt((maximum - minimum) + 1);
    }

}