package endpoint.servlet.grandchild;

import static org.junit.Assert.assertEquals;

import java.util.List;

import org.junit.Test;

import endpoint.repository.models.parents.Child;
import endpoint.repository.models.parents.Grandchild;

public class GrandchildQueryTest extends GrandchildServletTestCase {

	@Test
	public void testQuery() {
		saveGrandchild("xpto1", child);
		saveGrandchild("xpto2", child);
		saveGrandchild("xpto1", saveChild());

		String json = get(uri("/parents/%s/children/%s/grandchildren", parent, child), params("q", "{ where: ['name', '=', 'xpto1' ] }"));
		List<Grandchild> grandchildren = fromList(json, Grandchild.class);

		assertEquals(1, grandchildren.size());
		assertEquals("xpto1", grandchildren.get(0).getName());
	}

	@Test
	public void testGlobalQuery() {
		saveGrandchild("xpto1", child);
		saveGrandchild("xpto2", child);

		Child childX = saveChild();
		saveGrandchild("xpto1", childX);

		String json = get("/grandchildren", params("q", "{ where: ['name', '=', 'xpto1' ] }"));
		List<Grandchild> grandchildren = fromList(json, Grandchild.class);

		assertEquals(2, grandchildren.size());
		assertEquals("xpto1", grandchildren.get(0).getName());
		assertEquals("xpto1", grandchildren.get(1).getName());
		assertEquals(child.getId(), grandchildren.get(0).getChildId());
		assertEquals(childX.getId(), grandchildren.get(1).getChildId());
	}
}
