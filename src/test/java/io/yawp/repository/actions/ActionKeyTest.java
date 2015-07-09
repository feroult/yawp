package io.yawp.repository.actions;

import static org.junit.Assert.assertEquals;
import io.yawp.commons.http.HttpVerb;
import io.yawp.commons.http.annotation.GET;
import io.yawp.commons.http.annotation.PUT;
import io.yawp.repository.IdRef;
import io.yawp.repository.models.basic.BasicObject;
import io.yawp.repository.models.parents.Child;
import io.yawp.repository.models.parents.Parent;

import java.lang.reflect.Method;
import java.util.List;
import java.util.Map;

import org.junit.Test;

public class ActionKeyTest {

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

		@PUT("single-object")
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
	public void testParseMethodInvalidParameters() throws InvalidActionMethodException {
		ActionKey.parseMethod(getMethod("invalid1", String.class));
		ActionKey.parseMethod(getMethod("invalid2", IdRef.class, String.class));
		ActionKey.parseMethod(getMethod("invalid3", Map.class, String.class));
		ActionKey.parseMethod(getMethod("invalid4", IdRef.class));
	}

	@Test
	public void testParseMethodRootCollection() throws InvalidActionMethodException {
		List<ActionKey> keys = ActionKey.parseMethod(getMethod("rootCollection"));
		assertActionKey(HttpVerb.GET, "root-collection", true, keys.get(0));
	}

	@Test
	public void testParseMethodRootCollectionParams() throws InvalidActionMethodException {
		List<ActionKey> keys = ActionKey.parseMethod(getMethod("rootCollectionParams", Map.class));
		assertActionKey(HttpVerb.PUT, "root-collection-params", true, keys.get(0));
	}

	@Test
	public void testParseMethodSingleObject() throws InvalidActionMethodException {
		List<ActionKey> keys = ActionKey.parseMethod(getMethod("singleObject", IdRef.class));
		assertActionKey(HttpVerb.PUT, "single-object", false, keys.get(0));
	}

	@Test
	public void testParseMethodSingleObjectParams() throws InvalidActionMethodException {
		List<ActionKey> keys = ActionKey.parseMethod(getMethod("singleObjectParams", IdRef.class, Map.class));
		assertActionKey(HttpVerb.GET, "single-object-params", false, keys.get(0));
	}

	@Test
	public void testParseMethodParentRootCollection() throws InvalidActionMethodException {
		List<ActionKey> keys = ActionKey.parseMethod(getMethod("parentRootCollection", IdRef.class));
		assertActionKey(HttpVerb.PUT, "parent-root-collection", true, keys.get(0));
	}

	@Test
	public void testParseMethodParentRootCollectionParams() throws InvalidActionMethodException {
		List<ActionKey> keys = ActionKey.parseMethod(getMethod("parentRootCollectionParams", IdRef.class, Map.class));
		assertActionKey(HttpVerb.PUT, "parent-root-collection-params", true, keys.get(0));
	}

//	@Test
//	public void testCallActionMethod() throws InvalidActionMethodException {
//		TestAction testAction = new TestAction();
//
//		ActionKey actionKey = ActionKey.parseMethod(getMethod("rootCollection")).get(0);
//		assertEquals("root-collection-return", actionKey.callActionMethod(testAction));
//	}

	private void assertActionKey(HttpVerb verb, String actionName, boolean overCollection, ActionKey actual) {
		ActionKey expected = new ActionKey(verb, actionName, overCollection);
		assertEquals(expected, actual);
	}

	private Method getMethod(String methodName, Class<?>... parameterTypes) {
		try {
			return TestAction.class.getMethod(methodName, parameterTypes);
		} catch (NoSuchMethodException | SecurityException e) {
			throw new RuntimeException(e);
		}
	}
}
