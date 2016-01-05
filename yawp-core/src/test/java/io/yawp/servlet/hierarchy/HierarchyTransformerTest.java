package io.yawp.servlet.hierarchy;

import io.yawp.commons.utils.ServletTestCase;
import io.yawp.repository.models.hierarchy.AnotherObjectSubClass;
import io.yawp.repository.models.hierarchy.ObjectSubClass;
import org.junit.Test;

import static org.junit.Assert.assertEquals;

public class HierarchyTransformerTest extends ServletTestCase {

    @Test
    public void testAllObjectsTransformer() {
        post("/hierarchy_subclasses/1", "{ name: 'john' }");

        String json = get("/hierarchy_subclasses/1", params("t", "allObjectsUpperCase"));
        ObjectSubClass object = from(json, ObjectSubClass.class);

        assertEquals("JOHN + SUPERCLASS HOOK", object.getName());
    }

    @Test
    public void testSuperClassTransformer() {
        post("/hierarchy_subclasses/1", "{ name: 'john' }");

        String json = get("/hierarchy_subclasses/1", params("t", "upperCase"));
        ObjectSubClass object = from(json, ObjectSubClass.class);

        assertEquals("JOHN + SUPERCLASS HOOK", object.getName());
    }

    @Test
    public void testMoreSpecificSubClassTransformer() {
        post("/hierarchy_another-subclasses/1", "{ name: 'john' }");

        String json = get("/hierarchy_another-subclasses/1", params("t", "upperCase"));
        AnotherObjectSubClass object = from(json, AnotherObjectSubClass.class);

        assertEquals("john + more specific hook + transformer", object.getName());
    }

}
