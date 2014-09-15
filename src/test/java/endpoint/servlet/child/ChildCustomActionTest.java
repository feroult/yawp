package endpoint.servlet.child;

import static org.junit.Assert.assertEquals;

import java.util.List;

import org.junit.Test;

import endpoint.repository.models.parents.Child;

public class ChildCustomActionTest extends ChildServletTestCase {

	@Test
	public void testOverObject() {
		Child child = saveChild("xpto", parent);

		String json = put(uri("/parents/%s/children/%s/touched", parent, child));
		Child retrievedChild = from(json, Child.class);

		assertEquals("touched xpto", retrievedChild.getName());
		assertEquals(parent.getId(), retrievedChild.getParentId());
	}

	@Test
	public void testOverCollection() {
		saveChild("xpto1", parent);
		saveChild("xpto2", parent);
		saveChild("xpto3", saveParent());

		String json = put(uri("/parents/%s/children/touched", parent));
		List<Child> children = fromList(json, Child.class);

		assertEquals(2, children.size());
		assertEquals("touched xpto1", children.get(0).getName());
		assertEquals("touched xpto2", children.get(1).getName());
		assertEquals(parent.getId(), children.get(0).getParentId());
		assertEquals(parent.getId(), children.get(1).getParentId());
	}
}
