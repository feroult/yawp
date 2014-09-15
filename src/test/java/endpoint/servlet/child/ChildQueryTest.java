package endpoint.servlet.child;

import static org.junit.Assert.assertEquals;

import java.util.List;

import org.junit.Test;

import endpoint.repository.models.parents.Child;
import endpoint.repository.models.parents.Parent;

public class ChildQueryTest extends ChildServletTestCase {

	@Test
	public void testQuery() {
		saveChild("xpto1", parent);
		saveChild("xpto2", parent);
		saveChild("xpto1", saveParent());

		String json = get(uri("/parents/%s/children", parent), params("q", "{ where: ['name', '=', 'xpto1' ] }"));
		List<Child> children = fromList(json, Child.class);

		assertEquals(1, children.size());
		assertEquals("xpto1", children.get(0).getName());
	}

	@Test
	public void testGlobalQuery() {
		saveChild("xpto1", parent);
		saveChild("xpto2", parent);

		Parent parentX = saveParent();
		saveChild("xpto1", parentX);

		String json = get("/children", params("q", "{ where: ['name', '=', 'xpto1' ] }"));
		List<Child> children = fromList(json, Child.class);

		assertEquals(2, children.size());
		assertEquals("xpto1", children.get(0).getName());
		assertEquals("xpto1", children.get(1).getName());
		assertEquals(parent.getId(), children.get(0).getParentId());
		assertEquals(parentX.getId(), children.get(1).getParentId());
	}
}
