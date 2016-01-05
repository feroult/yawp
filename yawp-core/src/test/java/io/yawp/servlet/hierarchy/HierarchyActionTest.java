package io.yawp.servlet.hierarchy;

import io.yawp.commons.utils.ServletTestCase;
import io.yawp.repository.models.basic.BasicObject;
import io.yawp.repository.models.basic.HookedObject;
import io.yawp.repository.models.hierarchy.AnotherObjectSubClass;
import io.yawp.repository.models.hierarchy.ObjectSubClass;
import org.junit.Test;

import static org.junit.Assert.assertEquals;

public class HierarchyActionTest extends ServletTestCase {

    @Test
    public void testAllObjectsAction() {
        post("/basic_objects/1", "{ stringValue: 'xpto' }");

        String s = from(get("/basic_objects/1/all-objects"), String.class);

        assertEquals("xpto all objects action", s);
    }

    @Test
    public void testSuperClassAction() {
        post("/hierarchy_subclasses/1", "{ name: 'john' }");

        String s = from(get("/hierarchy_subclasses/1/superclass-action"), String.class);

        assertEquals("JOHN superclass action", s);
    }

}
