package io.yawp.commons.utils;

import io.yawp.commons.utils.json.ParameterizedTypeImpl;
import io.yawp.repository.IdRef;
import io.yawp.repository.models.basic.BasicObject;
import org.junit.Test;

import java.lang.reflect.Type;
import java.util.*;

import static org.junit.Assert.assertEquals;

public class JsonUtilsTest extends EndpointTestCase {

    @Test
    public void testXpto() {
        IdRef from = JsonUtils.from(null, "\"/basic_objects/1\"", IdRef.class);
    }

    private static final String DATA_OBJECT_JSON = "{\"intValue\" : 1, \"longValue\" : 1, \"doubleValue\" : 1.1, \"booleanValue\" : true, \"dateValue\" : \"2013/12/26 23:55:01\", \"stringValue\" : \"object1\", \"textValue\": \"text\"}";


    @Test
    public void testFrom() {
        BasicObject object = JsonUtils.from(null, DATA_OBJECT_JSON, BasicObject.class);
        object.assertObject("object1", "text", 1, 1l, 1.1, true, "2013/12/26 23:55:01");
    }

    @Test
    public void testFromArray() {
        String json = String.format("[%s, %s, %s]", DATA_OBJECT_JSON, DATA_OBJECT_JSON, DATA_OBJECT_JSON);

        List<BasicObject> objects = JsonUtils.fromList(null, json, BasicObject.class);

        assertEquals(3, objects.size());

        objects.get(0).assertObject("object1", "text", 1, 1l, 1.1, true, "2013/12/26 23:55:01");
        objects.get(0).assertObject("object1", "text", 1, 1l, 1.1, true, "2013/12/26 23:55:01");
        objects.get(0).assertObject("object1", "text", 1, 1l, 1.1, true, "2013/12/26 23:55:01");
    }

    @Test
    public void testMapWithLongKey() {
        Map<Long, String> map = new HashMap<Long, String>();

        map.put(1l, "xpto1");
        map.put(2l, "xpto2");

        String json = JsonUtils.to(map);

        map = JsonUtils.fromMap(null, json, Long.class, String.class);

        assertEquals("xpto1", map.get(1l));
        assertEquals("xpto2", map.get(2l));
    }

    @Test
    public void testMapWithComplexObjectValue() {
        Map<Long, BasicObject> map = new HashMap<Long, BasicObject>();

        map.put(1l, new BasicObject("xpto1"));
        map.put(2l, new BasicObject("xpto2"));

        String json = JsonUtils.to(map);

        map = JsonUtils.fromMap(null, json, Long.class, BasicObject.class);

        assertEquals("xpto1", map.get(1l).getStringValue());
        assertEquals("xpto2", map.get(2l).getStringValue());
    }

    @Test
    public void testMapWithListOfComplexObjectValue() throws NoSuchFieldException, SecurityException {
        Map<Long, List<BasicObject>> map = new HashMap<Long, List<BasicObject>>();

        map.put(1l, Arrays.asList(new BasicObject("xpto1"), new BasicObject("xpto2")));
        map.put(2l, Arrays.asList(new BasicObject("xpto3"), new BasicObject("xpto4")));

        String json = JsonUtils.to(map);
        map = JsonUtils.fromMapList(null, json, Long.class, BasicObject.class);

        assertEquals("xpto1", map.get(1l).get(0).getStringValue());
        assertEquals("xpto2", map.get(1l).get(1).getStringValue());
        assertEquals("xpto3", map.get(2l).get(0).getStringValue());
        assertEquals("xpto4", map.get(2l).get(1).getStringValue());
    }

    @Test
    public void testMapWithComplexKeyAndValue() {
        Map<IdRef<BasicObject>, BasicObject> map = new HashMap<>();
        map.put(id(BasicObject.class, 1l), new BasicObject("xpto"));

        Type keyType = ParameterizedTypeImpl.create(IdRef.class, new Type[]{Long.class});

        String json = JsonUtils.to(map);
        map = (Map<IdRef<BasicObject>, BasicObject>) JsonUtils.fromMapRaw(yawp, json, keyType, BasicObject.class);

        assertEquals("xpto", map.get(id(BasicObject.class, 1l)).getStringValue());
    }

    @Test
    public void testDate() {
        String dateStr = "\"2013/12/26 23:55:01\"";
        Date date = JsonUtils.from(yawp, dateStr, Date.class);
        String json = JsonUtils.to(date);
        assertEquals(dateStr, json);
    }
}
