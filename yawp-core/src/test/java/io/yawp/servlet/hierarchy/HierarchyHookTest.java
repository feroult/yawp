package io.yawp.servlet.hierarchy;

import io.yawp.commons.utils.ServletTestCase;
import io.yawp.repository.models.basic.HookedObject;
import io.yawp.repository.models.hierarchy.AnotherObjectSubClass;
import io.yawp.repository.models.hierarchy.ObjectSubClass;
import org.junit.Ignore;
import org.junit.Test;

import static org.junit.Assert.assertEquals;

public class HierarchyHookTest extends ServletTestCase {

    @Test
    public void testSuperClassHook() {
        String json = post("/hierarchy_subclasses/1", "{ name: 'john' }");

        ObjectSubClass object = from(json, ObjectSubClass.class);

        assertEquals("JOHN", object.getName());
    }

    @Test
    public void testMoreSpecificSubClassHook() {
        String json = post("/hierarchy_another-subclasses/1", "{ name: 'john' }");

        AnotherObjectSubClass object = from(json, AnotherObjectSubClass.class);

        assertEquals("john more specific hook", object.getName());
    }

    @Test
    public void testAllObjectsHook() {
        String json = post("/hooked_objects", "{ stringValue: 'all_objects' }");
        HookedObject object = from(json, HookedObject.class);
        assertEquals("xpto all objects", object.getStringValue());
    }
}
