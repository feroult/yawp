package io.yawp.servlet.parent;

import static org.junit.Assert.assertEquals;
import io.yawp.repository.actions.FakeException;
import io.yawp.repository.models.parents.Parent;

import java.util.List;

import org.junit.Test;

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
	public void testActionWithTransformer() {
		Parent parent = saveParent("xpto1");

		String json = get(uri("/parents/%s/echo", parent), params("t", "upperCase"));
		Parent retrievedParent = from(json, Parent.class);

		assertEquals("XPTO1", retrievedParent.getName());
	}

	@Test
	public void testAtomicRollback() {
		try {
			put(uri("/parents/atomic_rollback"));
		} catch (FakeException e) {
		}

		assertEquals(0, yawp(Parent.class).list().size());
	}

}
