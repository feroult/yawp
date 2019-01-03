package io.yawp.repository.query;

import io.yawp.commons.utils.EndpointTestCase;
import io.yawp.repository.IdRef;
import io.yawp.repository.models.basic.BasicObject;
import org.junit.Test;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static io.yawp.repository.models.basic.BasicObject.saveManyBasicObjects;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;

public class DatastoreQueryForcedResponseTest extends EndpointTestCase {

    @Test
    public void testForcedResponseList() {
        BasicObject xpto = yawp.save(new BasicObject("xpto"));

        List<BasicObject> nonForcedList = yawp(BasicObject.class).list();
        assertEquals(1, nonForcedList.size());

        List<BasicObject> forcedList = yawp(BasicObject.class).forceResponseList(Arrays.asList(xpto, xpto)).list();
        assertEquals(2, forcedList.size());
    }

    @Test
    public void testForcedResponseListViaOnly() {
        yawp.save(new BasicObject("xpto"));
        BasicObject fake = new BasicObject("fake");

        BasicObject nonForcedObj = yawp(BasicObject.class).only();
        assertEquals("xpto", nonForcedObj.getStringValue());

        BasicObject forcedObj = yawp(BasicObject.class).forceResponseList(Arrays.asList(fake)).only();
        assertEquals("fake", forcedObj.getStringValue());
    }

    @Test
    public void testForcedResponseListViaFirst() {
        yawp.save(new BasicObject("xpto"));
        BasicObject fake = new BasicObject("fake");

        BasicObject nonForcedObj = yawp(BasicObject.class).only();
        assertEquals("xpto", nonForcedObj.getStringValue());

        BasicObject forcedObj = yawp(BasicObject.class).forceResponseList(Arrays.asList(fake)).first();
        assertEquals("fake", forcedObj.getStringValue());
    }

    @Test
    public void testForcedResponseIds() {
        BasicObject xpto = yawp.save(new BasicObject("xpto"));

        List<IdRef<BasicObject>> nonForcedList = yawp(BasicObject.class).ids();
        assertEquals(1, nonForcedList.size());

        List<IdRef<BasicObject>> forcedList = yawp(BasicObject.class).forceResponseIds(Arrays.asList(xpto.getId(), xpto.getId())).ids();
        assertEquals(2, forcedList.size());
    }

    @Test
    public void testForcedResponseIdsViaOnlyId() {
        BasicObject xpto = yawp.save(new BasicObject("xpto"));
        BasicObject fake = new BasicObject("fake");
        fake.setId(IdRef.create(yawp, BasicObject.class, "oni"));

        IdRef<BasicObject> nonForcedId = yawp(BasicObject.class).onlyId();
        assertEquals(xpto.getId(), nonForcedId);

        IdRef<BasicObject> forcedId = yawp(BasicObject.class).forceResponseIds(Arrays.asList(fake.getId())).onlyId();
        assertEquals("/basic_objects/oni", forcedId.toString());
    }

    @Test
    public void testForcedResponseById() {
        BasicObject xpto = yawp.save(new BasicObject("xpto"));
        BasicObject fake = new BasicObject("fake");

        BasicObject nonForcedObj = yawp.query(BasicObject.class).fetch(xpto.getId());
        assertEquals("xpto", nonForcedObj.getStringValue());

        BasicObject forcedObj = yawp(BasicObject.class).forceResponseFetch(fake).fetch(xpto.getId());
        assertEquals("fake", forcedObj.getStringValue());
    }

    @Test
    public void testClearForcedResponse() {
        QueryBuilder<BasicObject> q = yawp(BasicObject.class).forceResponseIds(Collections.<IdRef<BasicObject>>emptyList()).clearForcedResponse();
        assertNull(q.getForcedResponse());
    }
}
