package io.yawp.repository.pipes;

import io.yawp.commons.utils.EndpointTestCase;
import io.yawp.repository.models.basic.BasicObject;
import org.junit.Test;

import java.util.Arrays;
import java.util.List;

import static org.junit.Assert.assertEquals;

public class PumpTest extends EndpointTestCase {

    @Test
    public void testBasicList() {
        Pump<BasicObject> pump = new Pump<>(2);
        pump.addObject(new BasicObject("xpto1"));
        pump.addObjects(Arrays.asList(new BasicObject("xpto2"), new BasicObject("xpto3")));

        assertBasicPump(pump);
    }

    @Test
    public void testBasicQuery() {
        yawp.save(new BasicObject("xpto1"));
        yawp.save(new BasicObject("xpto2"));
        yawp.save(new BasicObject("xpto3"));

        Pump<BasicObject> pump = new Pump<>(2);
        pump.addQuery(yawp(BasicObject.class));

        assertBasicPump(pump);
    }

    private void assertBasicPump(Pump<BasicObject> pump) {
        List<BasicObject> list;

        list = pump.more();
        assertEquals(2, list.size());
        assertEquals("xpto1", list.get(0).getStringValue());
        assertEquals("xpto2", list.get(1).getStringValue());

        list = pump.more();
        assertEquals(1, list.size());
        assertEquals("xpto3", list.get(0).getStringValue());
    }

}
