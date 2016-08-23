package io.yawp.servlet.parent;

import io.yawp.repository.actions.parents.FakeException;
import io.yawp.repository.models.parents.Parent;
import org.junit.Test;

import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

public class ParentCustomActionTest extends ParentServletTestCase {

    @Test
    public void testOverObject() {
        Parent parent = saveParent("xpto");

        String json = put(uri("/parents/%s/touched", parent));
        Parent retrievedParent = from(json, Parent.class);

        assertEquals("touched xpto", retrievedParent.getName());
    }

    @Test
    public void testOverCollection() {
        saveParent("xpto1");
        saveParent("xpto2");

        String json = put(uri("/parents/touched"));
        List<Parent> parents = fromList(json, Parent.class);

        assertEquals(2, parents.size());
        assertEquals("touched xpto1", parents.get(0).getName());
        assertEquals("touched xpto2", parents.get(1).getName());
    }

    @Test
    public void testOverObjectWithParams() {
        Parent parent = saveParent("xpto");

        String json = put(uri("/parents/%s/touched_with_params", parent), params("x", "y"));
        Parent retrievedParent = from(json, Parent.class);

        assertEquals("touched xpto y", retrievedParent.getName());
    }

    @Test
    public void testOverCollectionWithParams() {
        saveParent("xpto1");
        saveParent("xpto2");

        String json = put(uri("/parents/touched_with_params"), params("x", "y"));
        List<Parent> parents = fromList(json, Parent.class);

        assertEquals(2, parents.size());
        assertEquals("touched xpto1 y", parents.get(0).getName());
        assertEquals("touched xpto2 y", parents.get(1).getName());
    }

    @Test
    public void testAllHttpVerbs() {
        Parent parent = saveParent("xpto");

        assertEquals("\"ok\"", get(uri("/parents/%s/all-http-verbs", parent)));
        assertEquals("\"ok\"", post(uri("/parents/%s/all-http-verbs", parent)));
        assertEquals("\"ok\"", put(uri("/parents/%s/all-http-verbs", parent)));
        assertEquals("\"ok\"", patch(uri("/parents/%s/all-http-verbs", parent)));
        assertEquals("\"ok\"", delete(uri("/parents/%s/all-http-verbs", parent)));
    }

    @Test
    public void testActionWithTransformer() {
        Parent parent = saveParent("xpto1");

        String json = get(uri("/parents/%s/echo", parent), params("t", "upperCase"));
        Parent retrievedParent = from(json, Parent.class);

        assertTrue(retrievedParent.getName().startsWith("XPTO1"));
    }

    @Test
    public void testActionWithTransformerForList() {
        saveParent("xpto1");
        saveParent("xpto2");

        String json = get(uri("/parents/echo"), params("t", "upperCase"));
        List<Parent> parents = fromList(json, Parent.class);

        assertEquals(2, parents.size());
        String upperCaseName1 = parents.get(0).getName();
        String upperCaseName2 = parents.get(1).getName();
        assertTrue(upperCaseName1.startsWith("XPTO1"));
        assertTrue(upperCaseName2.startsWith("XPTO2"));

        // asserts if only one instance of the transformer was created
        String random1 = upperCaseName1.substring(7);
        String random2 = upperCaseName2.substring(7);
        assertEquals(random1, random2);
    }

    @Test
    public void testActionWithTransformAndDifferentClazzResult() {
        String json = get(uri("/parents/something"), params("t", "upperCase"));
        String responseString = from(json, String.class);
        assertEquals("touched", responseString);
    }

    @Test
    public void testAtomicRollback() {
        try {
            put(uri("/parents/atomic_rollback"));
        } catch (FakeException e) {
        }

        assertEquals(0, yawp(Parent.class).list().size());
    }

    @Test
    public void testOverObjectWithJsonString() {
        Parent parent = saveParent("xpto");
        String json = post(uri("/parents/%s/with-json-string", parent), "{ \"stringValue\": \"json string\" }");
        assertEquals("json string", from(json, String.class));
    }

    @Test
    public void testOverCollectiontWithJsonObject() {
        String json = post("/parents/collection-with-json-object", "{ \"stringValue\": \"json object\" }");
        assertEquals("json object", from(json, String.class));
    }

    @Test
    public void testOverObjectWithJsonObject() {
        Parent parent = saveParent("xpto");
        String json = post(uri("/parents/%s/with-json-object", parent), "{ \"stringValue\": \"json object\" }");
        assertEquals("json object", from(json, String.class));
    }

    @Test
    public void testOverObjectWithJsonList() {
        Parent parent = saveParent("xpto");
        String json = post(uri("/parents/%s/with-json-list", parent), "[{ \"stringValue\": \"pojo\" }, { \"stringValue\": \"list\" }]");
        assertEquals("pojo list", from(json, String.class));
    }

    @Test
    public void testReturningEmptyList() {
        Parent parent = saveParent("xpto");
        String json = get(uri("/parents/%s/empty-list", parent));
        assertEquals("[]", json);
    }
}
