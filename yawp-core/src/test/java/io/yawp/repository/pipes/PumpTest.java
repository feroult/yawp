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

        assertList(pump.more(), "xpto1", "xpto2");
        assertList(pump.more(), "xpto3");
    }

    @Test
    public void testBasicQuery() {
        yawp.save(new BasicObject("xpto1"));
        yawp.save(new BasicObject("xpto2"));
        yawp.save(new BasicObject("xpto3"));

        Pump<BasicObject> pump = new Pump<>(2);
        pump.addQuery(yawp(BasicObject.class));

        assertList(pump.more(), "xpto1", "xpto2");
        assertList(pump.more(), "xpto3");
    }

    private void assertList(List<BasicObject> list, String ... values) {
        assertEquals(values.length, list.size());
        for(int i = 0; i < values.length; i++) {
            assertEquals(values[i], list.get(i).getStringValue());
        }
    }

}
