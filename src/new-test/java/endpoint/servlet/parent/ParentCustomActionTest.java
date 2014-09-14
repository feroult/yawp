package endpoint.servlet.parent;

import static org.junit.Assert.assertEquals;

import java.util.List;

import org.junit.Test;

import endpoint.repository.models.parents.Parent;

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

}
