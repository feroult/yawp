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
        post("/hierarchy_subclasses/1", "{ name: 'john' }");
        post("/hierarchy_another-subclasses/1", "{ name: 'john' }");

        assertEquals("john + superclass hook + all objects action", from(get("/hierarchy_subclasses/1/all-objects"), String.class));
        assertEquals("john + more specific hook + all objects action", from(get("/hierarchy_another-subclasses/1/all-objects"), String.class));
    }

    @Test
    public void testSuperClassAction() {
        post("/hierarchy_subclasses/1", "{ name: 'john' }");

        String s = from(get("/hierarchy_subclasses/1/superclass-action"), String.class);

        assertEquals("john + superclass hook + superclass action", s);
    }

    @Test
    public void testMoreSpecificSubClassAction() {
        post("/hierarchy_another-subclasses/1", "{ name: 'john' }");

        String s = from(get("/hierarchy_another-subclasses/1/superclass-action"), String.class);

        assertEquals("john + more specific hook + action", s);
    }

}
