package io.yawp.repository.pipes;

import io.yawp.commons.utils.EndpointTestCase;
import io.yawp.repository.models.basic.BasicObject;
import org.junit.Test;

import java.util.List;

import static io.yawp.repository.models.basic.BasicObject.saveManyBasicObjects;
import static org.junit.Assert.*;

public class PumpTest extends EndpointTestCase {

    @Test
    public void testBasicList() {
        Pump<BasicObject> pump = new Pump<>(BasicObject.class, 2);

        List<BasicObject> objects = saveManyBasicObjects(3);
        pump.addObject(objects.get(0));
        pump.addObjects(objects.subList(1, 3));

        assertTrue(pump.hasMore());

        assertList(pump.more(), 1, 2);
        assertList(pump.more(), 3);

        assertFalse(pump.hasMore());
    }

    @Test
    public void testBasicQuery() {
        saveManyBasicObjects(3);

        Pump<BasicObject> pump = new Pump<>(BasicObject.class, 2);
        pump.addQuery(yawp(BasicObject.class));

        assertTrue(pump.hasMore());

        assertList(pump.more(), 1, 2);
        assertList(pump.more(), 3);

        assertFalse(pump.hasMore());
    }

    @Test
    public void testMultipleQueries() {
        saveManyBasicObjects(11);

        Pump<BasicObject> pump = new Pump<>(BasicObject.class, 2);
        pump.addQuery(yawp(BasicObject.class).where("intValue", "<=", 3));
        pump.addQuery(yawp(BasicObject.class).where("intValue", ">", 3).and("intValue", "<=", 6));
        pump.addQuery(yawp(BasicObject.class).where("intValue", ">", 6).and("intValue", "<=", 11));

        assertTrue(pump.hasMore());
        assertList(pump.more(), 1, 2);
        assertList(pump.more(), 3, 4);
        assertList(pump.more(), 5, 6);

        assertTrue(pump.hasMore());
        assertList(pump.more(), 7, 8);
        assertList(pump.more(), 9, 10);
        assertList(pump.more(), 11);

        assertFalse(pump.hasMore());
    }

    @Test
    public void testListAndMultipleQueries() {
        saveManyBasicObjects(11);

        Pump<BasicObject> pump = new Pump<>(BasicObject.class, 2);
        pump.addQuery(yawp(BasicObject.class).where("intValue", "<=", 3));
        pump.addQuery(yawp(BasicObject.class).where("intValue", ">", 3).and("intValue", "<=", 6));
        pump.addQuery(yawp(BasicObject.class).where("intValue", ">", 6).and("intValue", "<=", 11));
        pump.addObjects(saveManyBasicObjects(5, 11));

        assertTrue(pump.hasMore());
        assertList(pump.more(), 12, 13);
        assertList(pump.more(), 14, 15);
        assertList(pump.more(), 16, 1);

        assertTrue(pump.hasMore());
        assertList(pump.more(), 2, 3);
        assertList(pump.more(), 4, 5);
        assertList(pump.more(), 6, 7);

        assertTrue(pump.hasMore());
        assertList(pump.more(), 8, 9);
        assertList(pump.more(), 10, 11);

        assertTrue(pump.hasMore());
        assertEquals(0, pump.more().size());
        assertFalse(pump.hasMore());
    }


    private void assertList(List<BasicObject> list, int... values) {
        assertEquals(values.length, list.size());
        for (int i = 0; i < values.length; i++) {
            assertEquals(values[i], list.get(i).getIntValue());
        }
    }

}
