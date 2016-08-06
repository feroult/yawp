package io.yawp.repository;

import io.yawp.commons.utils.EndpointTestCase;
import io.yawp.repository.models.basic.BasicObject;
import io.yawp.repository.models.basic.HookedObject;
import org.junit.Ignore;
import org.junit.Test;

import static org.junit.Assert.assertEquals;

public class AsyncRepositoryTest extends EndpointTestCase {

    @Test
    public void testSave() {
        BasicObject object = new BasicObject("xpto");

        FutureObject<BasicObject> future = yawp.async().save(object);

        assertEquals("xpto", future.get().getStringValue());
    }

    @Test
    public void testSaveParallel() {
        BasicObject object1 = new BasicObject("xpto1");
        BasicObject object2 = new BasicObject("xpto2");

        FutureObject<BasicObject> future1 = yawp.async().save(object1);
        FutureObject<BasicObject> future2 = yawp.async().save(object2);

        assertEquals("xpto1", future1.get().getStringValue());
        assertEquals("xpto2", future2.get().getStringValue());
    }

    @Test
    public void testBeforeSaveHook() {
        HookedObject object = new HookedObject("before_save");

        FutureObject<HookedObject> future = yawp.async().saveWithHooks(object);
        assertEquals("xpto before save", object.getStringValue());
        assertEquals("xpto before save", future.get().getStringValue());
    }

    @Test
    public void testAfterSaveHook() {
        HookedObject object = new HookedObject("after_save");

        FutureObject<HookedObject> future = yawp.async().saveWithHooks(object);
        assertEquals("after_save", object.getStringValue());
        assertEquals("xpto after save", future.get().getStringValue());
    }

    @Test
    @Ignore
    public void testDestroy() {
        IdRef<BasicObject> id = id(BasicObject.class, 1l);

        BasicObject object = new BasicObject();
        object.setId(id);

        yawp.save(object);

        FutureObject<Void> future = yawp.async().destroy(id);
    }

    @Test
    public void testFetchAsync() {
        IdRef<BasicObject> id = id(BasicObject.class, 1l);

        BasicObject object = new BasicObject("xpto");
        object.setId(id);

        yawp.save(object);

        FutureObject<BasicObject> future = yawp.async().fetch(id);
        BasicObject retrievedObject = future.get();
        assertEquals("xpto", retrievedObject.getStringValue());
    }
}
