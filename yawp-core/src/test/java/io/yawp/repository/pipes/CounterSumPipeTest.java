package io.yawp.repository.pipes;

import io.yawp.commons.utils.EndpointTestCase;
import io.yawp.repository.IdRef;
import io.yawp.repository.models.basic.PipedObject;
import io.yawp.repository.models.basic.PipedObjectCounter;
import io.yawp.repository.models.basic.PipedObjectCounterSum;
import org.junit.Test;

import java.util.concurrent.TimeUnit;

import static org.junit.Assert.assertEquals;

public class CounterSumPipeTest extends EndpointTestCase {

    @Test
    public void testOnlyIncrement() {
        if (pipesDriverNotImplemented()) {
            return;
        }

        IdRef<PipedObjectCounter> counterId1 = id(PipedObjectCounter.class, 1L);
        IdRef<PipedObjectCounter> counterId2 = id(PipedObjectCounter.class, 2L);
        IdRef<PipedObjectCounter> counterId3 = id(PipedObjectCounter.class, 3L);

        yawp.save(new PipedObject("xpto", counterId1));
        yawp.save(new PipedObject("xpto", counterId1));
        yawp.save(new PipedObject("xpto", counterId2));
        yawp.save(new PipedObject("xpto", counterId2));
        yawp.save(new PipedObject("xpto", counterId2));
        yawp.save(new PipedObject("xpto", counterId3));
        awaitAsync(20, TimeUnit.SECONDS);

        PipedObjectCounterSum sum = yawp(PipedObjectCounterSum.class).only();
        assertEquals((Integer) 6, sum.getSum());
    }


}