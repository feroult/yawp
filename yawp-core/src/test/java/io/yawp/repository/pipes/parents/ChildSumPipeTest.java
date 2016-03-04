package io.yawp.repository.pipes.parents;

import io.yawp.commons.utils.EndpointTestCase;
import io.yawp.repository.IdRef;
import io.yawp.repository.models.basic.BasicObject;
import io.yawp.repository.models.parents.ChildPipedObject;
import io.yawp.repository.models.parents.ChildPipedObjectSum;
import org.junit.Test;

import java.util.concurrent.TimeUnit;

import static org.junit.Assert.assertEquals;

public class ChildSumPipeTest extends EndpointTestCase {

    @Test
    public void testSaveDifferentChildreen() {
        if (pipesDriverNotImplemented()) {
            return;
        }

        saveChildPipedObject(10, 1L);
        saveChildPipedObject(20, 1L);
        saveChildPipedObject(20, 2L);
        saveChildPipedObject(5, 2L);
        saveChildPipedObject(15, 2L);
        saveChildPipedObject(2, 3L);
        saveChildPipedObject(1, 3L);
        awaitAsync(20, TimeUnit.SECONDS);

        assertEquals((Integer) 30, createSumId(1L).fetch().getSum());
        assertEquals((Integer) 40, createSumId(2L).fetch().getSum());
        assertEquals((Integer) 3, createSumId(3L).fetch().getSum());
    }

    private void saveChildPipedObject(int value, long sumId) {
        ChildPipedObject child = new ChildPipedObject();
        child.setParentId(id(BasicObject.class, 1L));
        child.setSumId(createSumId(sumId));
        child.setValue(value);
        yawp.save(child);
    }

    private IdRef<ChildPipedObjectSum> createSumId(long sumId) {
        return id(BasicObject.class, 2L).createChildId(ChildPipedObjectSum.class, sumId);
    }

}