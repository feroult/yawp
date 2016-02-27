package io.yawp.repository.pipes.pump;

import io.yawp.commons.utils.EndpointTestCase;
import io.yawp.repository.models.basic.BasicObject;

import java.util.List;

import static org.junit.Assert.*;

public abstract class PumpTestCase<T> extends EndpointTestCase {

    protected void pumpTestBasicList(Pump<T> pump, List<T> list) {
        pump.add(list.get(0));
        pump.addAll(list.subList(1, 3));

        assertTrue(pump.hasMore());

        assertList(pump.more(), 1, 2);
        assertList(pump.more(), 3);

        assertFalse(pump.hasMore());
    }

    protected void pumpTestBasicQuery(Pump<T> pump) {
        pump.addQuery(yawp(BasicObject.class));

        assertTrue(pump.hasMore());

        assertList(pump.more(), 1, 2);
        assertList(pump.more(), 3);

        assertFalse(pump.hasMore());
    }

    protected void pumpTestMultipleQueries(Pump<T> pump) {
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

    protected void pumpTestListAndMultipleQueries(Pump<T> pump, List<T> list) {
        pump.addQuery(yawp(BasicObject.class).where("intValue", "<=", 3));
        pump.addQuery(yawp(BasicObject.class).where("intValue", ">", 3).and("intValue", "<=", 6));
        pump.addQuery(yawp(BasicObject.class).where("intValue", ">", 6).and("intValue", "<=", 11));
        pump.addAll(list);

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

    protected abstract void assertList(List<T> list, int... values);
}
