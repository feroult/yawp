package io.yawp.repository.actions;

import io.yawp.commons.http.HttpVerb;
import io.yawp.commons.http.annotation.*;
import io.yawp.repository.IdRef;
import io.yawp.repository.models.basic.BasicObject;
import io.yawp.repository.models.parents.Child;
import io.yawp.repository.models.parents.Parent;
import org.junit.Test;

import java.lang.reflect.Method;
import java.util.List;
import java.util.Map;

import static org.junit.Assert.assertEquals;

public class ActionMethodTest {

    private class FakeAction<T> {
    }

    private class TestAction extends FakeAction<Child> {
        @PUT("invalid-1")
        public void invalid1(Map<String, Long> map) {
        }

        @PUT("invalid-2")
        public void invalid2(IdRef<Child> id, IdRef<Child> id2) {
        }

        @PUT("invalid-3")
        public void invalid3(Map<String, String> params, Map<String, String> params2) {
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

        @GET
        @POST
        @PUT
        @PATCH
        @DELETE
        public void singleObjectWithoutName(IdRef<Child> id) {
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

        @POST("single-object-with-json-string")
        public void singleObjectWithJsonString(IdRef<Child> id, String json) {
        }

        @POST("single-object-with-json-object")
        public void singleObjectWithJsonObject(IdRef<Child> id, BasicObject object) {
        }

        @POST("single-object-with-json-string-and-params")
        public void singleObjectWithJsonStringAndParams(IdRef<Child> id, BasicObject object, Map<String, String> params) {
        }

        @POST("parent-root-collection-json-object-and-params")
        public void parentRootCollectionJsonObjectParams(Map<String, String> params, BasicObject object, IdRef<Parent> id) {
        }

        @POST("parent-root-collection-json-string-and-params")
        public void parentRootCollectionJsonStringParams(String json, Map<String, String> params, IdRef<Parent> id) {
        }
    }

    @Test(expected = InvalidActionMethodException.class)
    public void testInvalidParameters1() throws InvalidActionMethodException {
        getActionKeysFor("invalid1", Map.class);
    }

    @Test(expected = InvalidActionMethodException.class)
    public void testInvalidParameters2() throws InvalidActionMethodException {
        getActionKeysFor("invalid2", IdRef.class, IdRef.class);
    }

    @Test(expected = InvalidActionMethodException.class)
    public void testInvalidParameters3() throws InvalidActionMethodException {
        getActionKeysFor("invalid3", Map.class, Map.class);
    }

    @Test(expected = InvalidActionMethodException.class)
    public void testInvalidParameters4() throws InvalidActionMethodException {
        getActionKeysFor("invalid4", IdRef.class);
    }

    @Test
    public void testRootCollection() throws InvalidActionMethodException {
        List<ActionKey> keys = getActionKeysFor("rootCollection");
        assertActionKey(HttpVerb.GET, "root-collection", true, keys.get(0));
    }

    @Test
    public void testRootCollectionParams() throws InvalidActionMethodException {
        List<ActionKey> keys = getActionKeysFor("rootCollectionParams", Map.class);
        assertActionKey(HttpVerb.PUT, "root-collection-params", true, keys.get(0));
    }

    @Test
    public void testSingleObject() throws InvalidActionMethodException {
        List<ActionKey> keys = getActionKeysFor("singleObject", IdRef.class);
        assertActionKey(HttpVerb.GET, "single-object", false, keys.get(0));
        assertActionKey(HttpVerb.POST, "single-object", false, keys.get(1));
        assertActionKey(HttpVerb.PUT, "single-object", false, keys.get(2));
        assertActionKey(HttpVerb.PATCH, "single-object", false, keys.get(3));
        assertActionKey(HttpVerb.DELETE, "single-object", false, keys.get(4));
    }

    @Test
    public void testSingleObjectWithoutName() throws InvalidActionMethodException {
        List<ActionKey> keys = getActionKeysFor("singleObjectWithoutName", IdRef.class);
        assertActionKey(HttpVerb.GET, "single-object-without-name", false, keys.get(0));
        assertActionKey(HttpVerb.POST, "single-object-without-name", false, keys.get(1));
        assertActionKey(HttpVerb.PUT, "single-object-without-name", false, keys.get(2));
        assertActionKey(HttpVerb.PATCH, "single-object-without-name", false, keys.get(3));
        assertActionKey(HttpVerb.DELETE, "single-object-without-name", false, keys.get(4));
    }

    @Test
    public void testSingleObjectParams() throws InvalidActionMethodException {
        List<ActionKey> keys = getActionKeysFor("singleObjectParams", IdRef.class, Map.class);
        assertActionKey(HttpVerb.GET, "single-object-params", false, keys.get(0));
    }

    @Test
    public void testParentRootCollection() throws InvalidActionMethodException {
        List<ActionKey> keys = getActionKeysFor("parentRootCollection", IdRef.class);
        assertActionKey(HttpVerb.PUT, "parent-root-collection", true, keys.get(0));
    }

    @Test
    public void testParentRootCollectionParams() throws InvalidActionMethodException {
        List<ActionKey> keys = getActionKeysFor("parentRootCollectionParams", IdRef.class, Map.class);
        assertActionKey(HttpVerb.PUT, "parent-root-collection-params", true, keys.get(0));
    }

    @Test
    public void testSingleObjectWithJsonString() throws InvalidActionMethodException {
        List<ActionKey> keys = getActionKeysFor("singleObjectWithJsonString", IdRef.class, String.class);
        assertActionKey(HttpVerb.POST, "single-object-with-json-string", false, keys.get(0));
    }

    @Test
    public void testSingleObjectWithJsonObject() throws InvalidActionMethodException {
        List<ActionKey> keys = getActionKeysFor("singleObjectWithJsonObject", IdRef.class, BasicObject.class);
        assertActionKey(HttpVerb.POST, "single-object-with-json-object", false, keys.get(0));
    }

    @Test
    public void testSingleObjectWithJsonStringAndParams() throws InvalidActionMethodException {
        List<ActionKey> keys = getActionKeysFor("singleObjectWithJsonStringAndParams", IdRef.class, BasicObject.class, Map.class);
        assertActionKey(HttpVerb.POST, "single-object-with-json-string-and-params", false, keys.get(0));
    }

    @Test
    public void testParentRootCollectionJsonObjectParams() throws InvalidActionMethodException {
        List<ActionKey> keys = getActionKeysFor("parentRootCollectionJsonObjectParams", Map.class, BasicObject.class, IdRef.class);
        assertActionKey(HttpVerb.POST, "parent-root-collection-json-object-and-params", true, keys.get(0));
    }

    @Test
    public void testParentRootCollectionJsonStringParams() throws InvalidActionMethodException {
        List<ActionKey> keys = getActionKeysFor("parentRootCollectionJsonStringParams", String.class, Map.class, IdRef.class);
        assertActionKey(HttpVerb.POST, "parent-root-collection-json-string-and-params", true, keys.get(0));
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