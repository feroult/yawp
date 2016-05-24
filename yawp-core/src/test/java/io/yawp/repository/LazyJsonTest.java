package io.yawp.repository;

import io.yawp.commons.utils.EndpointTestCase;
import io.yawp.commons.utils.JsonUtils;
import io.yawp.repository.models.basic.BasicObject;
import io.yawp.repository.models.basic.Pojo;
import org.junit.Test;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

import static org.junit.Assert.assertEquals;

public class LazyJsonTest extends EndpointTestCase {

    @Test
    public void testSerialize() {
        Pojo pojo = new Pojo("xpto");

        LazyJson<Pojo> lazyJson = LazyJson.create(pojo);

        assertEquals(JsonUtils.to(pojo), JsonUtils.to(lazyJson));
    }

    @Test
    public void testDeserialize() {
        Pojo pojo = new Pojo("xpto");

        LazyJson<Pojo> lazyJson = LazyJson.create(pojo);

        assertEquals("xpto", lazyJson.get().getStringValue());
    }

    @Test
    public void testGetCacheChangeJson() {
        Pojo pojo = new Pojo("xpto");
        LazyJson<Pojo> lazyJson = LazyJson.create(pojo);

        lazyJson.get().setStringValue("otpx");

        assertEquals(JsonUtils.to(new Pojo("otpx")), lazyJson.getJson());
    }

    @Test
    public void testAsProperty() {
        BasicObject object = new BasicObject();
        object.setLazyPojo(new Pojo("xpto"));

        String json = JsonUtils.to(object);
        BasicObject parsedObject = from(json, BasicObject.class);

        // re-serialize / re-parse
        json = JsonUtils.to(parsedObject);
        parsedObject = from(json, BasicObject.class);

        assertEquals("xpto", parsedObject.getLazyPojo().getStringValue());
    }

    @Test
    public void testAsListProperty() {
        BasicObject object = new BasicObject();
        object.setLazyListPojo(Arrays.asList(new Pojo("xpto")));

        String json = JsonUtils.to(object);
        BasicObject parsedObject = from(json, BasicObject.class);

        // re-serialize / re-parse
        json = JsonUtils.to(parsedObject);
        parsedObject = from(json, BasicObject.class);

        assertEquals("xpto", parsedObject.getLazyListPojo().get(0).getStringValue());
    }

    @Test
    public void testAsMapProperty() {
        BasicObject object = new BasicObject();

        object.setLazyMapPojo(createPojoMap());

        String json = JsonUtils.to(object);
        BasicObject parsedObject = from(json, BasicObject.class);

        // re-serialize / re-parse
        json = JsonUtils.to(parsedObject);
        parsedObject = from(json, BasicObject.class);

        assertEquals("xpto", parsedObject.getLazyMapPojo().get(1l).getStringValue());
    }

    @Test
    public void testAsIdRefMapProperty() {
        BasicObject object = new BasicObject();

        object.setLazyIdRefMapPojo(createPojoIdRefMap());

        String json = JsonUtils.to(object);
        BasicObject parsedObject = from(json, BasicObject.class);

        // re-serialize / re-parse
        json = JsonUtils.to(parsedObject);
        parsedObject = from(json, BasicObject.class);

        assertEquals("xpto", parsedObject.getLazyIdRefMapPojo().get(id(BasicObject.class, 1l)).getStringValue());
    }

    @Test
    public void testSaveAndLoad() {
        BasicObject object = new BasicObject();
        object.setLazyPojo(new Pojo("xpto"));
        object.setLazyListPojo(Arrays.asList(new Pojo("xpto")));
        object.setLazyMapPojo(createPojoMap());
        object.setLazyIdRefMapPojo(createPojoIdRefMap());

        yawp.save(object);
        BasicObject retrievedObject = object.getId().fetch();

        assertEquals("xpto", retrievedObject.getLazyPojo().getStringValue());
        assertEquals("xpto", retrievedObject.getLazyListPojo().get(0).getStringValue());
        assertEquals("xpto", retrievedObject.getLazyMapPojo().get(1l).getStringValue());
        assertEquals("xpto", retrievedObject.getLazyIdRefMapPojo().get(id(BasicObject.class, 1l)).getStringValue());
    }

    private Map<Long, Pojo> createPojoMap() {
        Map<Long, Pojo> map = new HashMap<>();
        map.put(1l, new Pojo("xpto"));
        return map;
    }


    private Map<IdRef<BasicObject>, Pojo> createPojoIdRefMap() {
        Map<IdRef<BasicObject>, Pojo> map = new HashMap<>();
        map.put(id(BasicObject.class, 1l), new Pojo("xpto"));
        return map;
    }

}
