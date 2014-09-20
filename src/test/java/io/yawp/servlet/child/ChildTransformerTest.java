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

	@Test
	public void testCreateTransformer() {
		String json = post(uri("/parents/%s/children", parent), json("{ name: 'xpto', parentId: '%s' }", parent), params("t", "simple"));

		Child child = from(json, Child.class);
		assertEquals("transformed xpto", child.getName());
		assertEquals(parent.getId(), child.getParentId());
	}

	@Test
	public void testCreateArrayTransformer() {
		String json = post(uri("/parents/%s/children", parent),
				json("[ { name: 'xpto1', parentId: '%s' }, { name: 'xpto2', parentId: '%s' } ]", parent, parent), params("t", "simple"));
		List<Child> children = fromList(json, Child.class);

		assertEquals(2, children.size());
		assertEquals("transformed xpto1", children.get(0).getName());
		assertEquals("transformed xpto2", children.get(1).getName());
		assertEquals(parent.getId(), children.get(0).getParentId());
		assertEquals(parent.getId(), children.get(1).getParentId());
	}

	@Test
	public void testUpdateTransformer() {
		Child child = saveChild("xpto", parent);

		String json = put(uri("/parents/%s/children/%s", parent, child),
				json("{ name: 'changed xpto', parentId: '%s', id: '%s' }", parent, child), params("t", "simple"));
		Child retrievedChild = from(json, Child.class);

		assertEquals("transformed changed xpto", retrievedChild.getName());
		assertEquals(parent.getId(), retrievedChild.getParentId());
	}

}
