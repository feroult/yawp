package io.yawp.repository.pipes.pump;

import io.yawp.repository.models.basic.BasicObject;
import org.junit.Test;

import java.util.List;

import static io.yawp.repository.models.basic.BasicObject.saveManyBasicObjects;
import static org.junit.Assert.*;

public class ObjectPumpTest extends PumpTestCase<BasicObject> {

    @Test
    public void testBasicList() {
        List<BasicObject> list = saveManyBasicObjects(3);

        ObjectPump<BasicObject> pump = new ObjectPump<>(2);

        pumpTestBasicList(pump, list);
    }

    @Test
    public void testBasicQuery() {
        saveManyBasicObjects(3);
        ObjectPump<BasicObject> pump = new ObjectPump<>(2);

        pumpTestBasicQuery(pump);
    }

    @Test
    public void testMultipleQueries() {
        saveManyBasicObjects(11);

        ObjectPump<BasicObject> pump = new ObjectPump<>(2);

        pumpTestMultipleQueries(pump);
    }

    @Test
    public void testListAndMultipleQueries() {
        saveManyBasicObjects(11);
        List<BasicObject> list = saveManyBasicObjects(5, 11);

        ObjectPump<BasicObject> pump = new ObjectPump<>(2);

        pumpTestListAndMultipleQueries(pump, list);
    }


    @Override
    protected void assertList(List<BasicObject> list, int... values) {
        assertEquals(values.length, list.size());
        for (int i = 0; i < values.length; i++) {
            assertEquals(values[i], list.get(i).getIntValue());
        }
    }

}
