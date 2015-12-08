package io.yawp.repository.actions;

import static org.junit.Assert.assertEquals;

import io.yawp.commons.http.HttpVerb;
import io.yawp.commons.http.annotation.*;
import io.yawp.repository.IdRef;
import io.yawp.repository.models.basic.BasicObject;
import io.yawp.repository.models.parents.Child;
import io.yawp.repository.models.parents.Parent;

import java.lang.reflect.Method;
import java.util.List;
import java.util.Map;

import org.junit.AfterClass;
import org.junit.Test;

public class ActionMethodTest {

    private class FakeAction<T> {
    }

    private class TestAction extends FakeAction<Child> {
        @PUT("invalid-1")
        public void invalid1(String xpto) {
        }

        @PUT("invalid-2")
        public void invalid2(IdRef<Child> id, String xpto) {
        }

        @PUT("invalid-3")
        public void invalid3(Map<String, String> params, String xpto) {
        }

        @PUT("invalid-4")
        public void invalid4(IdRef<BasicObject> id) {
        }

        @GET("root-collection")
        public String rootCollection() {
            return "root-collection-return";
        }

        @PUT("root-collection-params")
        public void rootCollectionParams(Map<String, String> params) {
        }

        @GET("single-object")
        @POST("single-object")
        @PUT("single-object")
        @PATCH("single-object")
        @DELETE("single-object")
        public void singleObject(IdRef<Child> id) {
        }

        @GET("single-object-params")
        public void singleObjectParams(IdRef<Child> id, Map<String, String> params) {
        }

        @PUT("parent-root-collection")
        public void parentRootCollection(IdRef<Parent> id) {
        }

        @PUT("parent-root-collection-params")
        public void parentRootCollectionParams(IdRef<Parent> id, Map<String, String> params) {
        }
    }

    @Test(expected = InvalidActionMethodException.class)
    public void testParseMethodInvalidParameters1() throws InvalidActionMethodException {
        getActionKeysFor("invalid1", String.class);
    }

    @Test(expected = InvalidActionMethodException.class)
    public void testParseMethodInvalidParameters2() throws InvalidActionMethodException {
        getActionKeysFor("invalid2", IdRef.class, String.class);
    }

    @Test(expected = InvalidActionMethodException.class)
    public void testParseMethodInvalidParameters3() throws InvalidActionMethodException {
        getActionKeysFor("invalid3", Map.class, String.class);
    }

    @Test(expected = InvalidActionMethodException.class)
    public void testParseMethodInvalidParameters4() throws InvalidActionMethodException {
        getActionKeysFor("invalid4", IdRef.class);
    }

    @Test
    public void testParseMethodRootCollection() throws InvalidActionMethodException {
        List<ActionKey> keys = getActionKeysFor("rootCollection");
        assertActionKey(HttpVerb.GET, "root-collection", true, keys.get(0));
    }

    @Test
    public void testParseMethodRootCollectionParams() throws InvalidActionMethodException {
        List<ActionKey> keys = getActionKeysFor("rootCollectionParams", Map.class);
        assertActionKey(HttpVerb.PUT, "root-collection-params", true, keys.get(0));
    }

    @Test
    public void testParseMethodSingleObject() throws InvalidActionMethodException {
        List<ActionKey> keys = getActionKeysFor("singleObject", IdRef.class);
        assertActionKey(HttpVerb.GET, "single-object", false, keys.get(0));
        assertActionKey(HttpVerb.POST, "single-object", false, keys.get(1));
        assertActionKey(HttpVerb.PUT, "single-object", false, keys.get(2));
        assertActionKey(HttpVerb.PATCH, "single-object", false, keys.get(3));
        assertActionKey(HttpVerb.DELETE, "single-object", false, keys.get(4));
    }

    @Test
    public void testParseMethodSingleObjectParams() throws InvalidActionMethodException {
        List<ActionKey> keys = getActionKeysFor("singleObjectParams", IdRef.class, Map.class);
        assertActionKey(HttpVerb.GET, "single-object-params", false, keys.get(0));
    }

    @Test
    public void testParseMethodParentRootCollection() throws InvalidActionMethodException {
        List<ActionKey> keys = getActionKeysFor("parentRootCollection", IdRef.class);
        assertActionKey(HttpVerb.PUT, "parent-root-collection", true, keys.get(0));
    }

    @Test
    public void testParseMethodParentRootCollectionParams() throws InvalidActionMethodException {
        List<ActionKey> keys = getActionKeysFor("parentRootCollectionParams", IdRef.class, Map.class);
        assertActionKey(HttpVerb.PUT, "parent-root-collection-params", true, keys.get(0));
    }

    private void assertActionKey(HttpVerb verb, String actionName, boolean overCollection, ActionKey actual) {
        ActionKey expected = new ActionKey(verb, actionName, overCollection);
        assertEquals(expected, actual);
    }

    private List<ActionKey> getActionKeysFor(String methodName, Class<?>... parameterTypes) throws InvalidActionMethodException {
        Method method = getMethod(methodName, parameterTypes);
        return ActionMethod.getActionKeysFor(method);
    }

    private Method getMethod(String methodName, Class<?>... parameterTypes) {
        try {
            return TestAction.class.getMethod(methodName, parameterTypes);
        } catch (NoSuchMethodException | SecurityException e) {
            throw new RuntimeException(e);
        }
    }
}