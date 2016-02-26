package io.yawp.repository.pipes;

import io.yawp.commons.utils.EndpointTestCase;
import io.yawp.repository.models.basic.BasicObject;
import org.junit.Ignore;
import org.junit.Test;

import java.util.List;

import static io.yawp.repository.models.basic.BasicObject.saveManyBasicObjects;
import static org.junit.Assert.assertEquals;

public class PumpTest extends EndpointTestCase {

    @Test
    public void testBasicList() {
        Pump<BasicObject> pump = new Pump<>(2);

        List<BasicObject> objects = saveManyBasicObjects(3);
        pump.addObject(objects.get(0));
        pump.addObjects(objects.subList(1, 3));

        assertList(pump.more(), 1, 2);
        assertList(pump.more(), 3);
    }

    @Test
    public void testBasicQuery() {
        saveManyBasicObjects(3);

        Pump<BasicObject> pump = new Pump<>(2);
        pump.addQuery(yawp(BasicObject.class));

        assertList(pump.more(), 1, 2);
        assertList(pump.more(), 3);
    }

    @Test
    @Ignore
    public void testTwoQueries() {
        saveManyBasicObjects(6);

        Pump<BasicObject> pump = new Pump<>(2);
        pump.addQuery(yawp(BasicObject.class).where("intValue", "<=", 3));
        pump.addQuery(yawp(BasicObject.class).where("intValue", ">", 3).and("intValue", "<=", 6));

        assertList(pump.more(), 1, 2);
        assertList(pump.more(), 3, 4);
        assertList(pump.more(), 5, 6);
    }

    private void assertList(List<BasicObject> list, int... values) {
        assertEquals(values.length, list.size());
        for (int i = 0; i < values.length; i++) {
            assertEquals(values[i], list.get(i).getIntValue());
        }
    }

}
