package io.yawp.servlet.child;

import static org.junit.Assert.assertEquals;
import io.yawp.repository.models.parents.Child;

import java.util.List;

import org.junit.Test;

public class ChildTransformerTest extends ChildServletTestCase {

	@Test
	public void testShowTransformer() {
		Child child = saveChild("xpto1", parent);

		String json = get(uri("/parents/%s/children/%s", parent, child), params("t", "simple"));
		Child retrievedChild = from(json, Child.class);

		assertEquals("transformed xpto1", retrievedChild.getName());
	}

	@Test
	public void testIndexTransformer() {
		saveChild("xpto1", parent);
		saveChild("xpto2", parent);

		String json = get(uri("/parents/%s/children", parent), params("t", "simple"));
		List<Child> children = fromList(json, Child.class);

		assertEquals(2, children.size());
		assertEquals("transformed xpto1", children.get(0).getName());
		assertEquals("transformed xpto2", children.get(1).getName());
	}
}
