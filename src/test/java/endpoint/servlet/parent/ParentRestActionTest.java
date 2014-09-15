package endpoint.servlet.parent;

import static org.junit.Assert.assertEquals;

import java.util.List;

import org.junit.Test;

import endpoint.repository.models.parents.Parent;

public class ParentRestActionTest extends ParentServletTestCase {

	@Test
	public void testCreate() {
		String json = post("/parents", "{ name: 'xpto' } ");
		Parent object = from(json, Parent.class);

		assertEquals("xpto", object.getName());
	}

	@Test
	public void testCreateArray() {
		String json = post("/parents", "[ { name: 'xpto1' }, { name: 'xpto2' } ]");
		List<Parent> parents = fromList(json, Parent.class);

		assertEquals(2, parents.size());
		assertEquals("xpto1", parents.get(0).getName());
		assertEquals("xpto2", parents.get(1).getName());
	}

	@Test
	public void testUpdate() {
		Parent parent = saveParent("xpto");

		String json = put(uri("/parents/%s", parent), "{ name: 'changed xpto' } ");
		Parent retrievedParent = from(json, Parent.class);

		assertEquals("changed xpto", retrievedParent.getName());
	}

	@Test
	public void testShow() {
		Parent parent = saveParent("xpto");

		String json = get(uri("/parents/%s", parent));
		Parent retrievedParent = from(json, Parent.class);

		assertEquals("xpto", retrievedParent.getName());
	}

	@Test
	public void testIndex() {
		saveParent("xpto1");
		saveParent("xpto2");

		String json = get("/parents");
		List<Parent> parents = fromList(json, Parent.class);

		assertEquals(2, parents.size());
		assertEquals("xpto1", parents.get(0).getName());
		assertEquals("xpto2", parents.get(1).getName());
	}

	@Test
	public void testDestroy() {
		Parent parent = saveParent("xpto1");

		delete(uri("/parents/%s", parent));
		assertGetWithStatus(uri("/parents/%s", parent), 404);
	}
}
