package endpoint.servlet.grandchild;

import static org.junit.Assert.assertEquals;

import java.util.List;

import org.junit.Test;

import endpoint.repository.models.parents.Grandchild;
import endpoint.servlet.grandchild.GrandchildServletTestCase;

public class GrandchildCustomActionTest extends GrandchildServletTestCase {

	@Test
	public void testOverObject() {
		Grandchild grandchild = saveGrandchild("xpto", child);

		String json = put(uri("/parents/%s/children/%s/grandchildren/%s/touched", parent, child, grandchild));
		Grandchild retrievedGrandchild = from(json, Grandchild.class);

		assertEquals("touched xpto", retrievedGrandchild.getName());
		assertEquals(child.getId(), retrievedGrandchild.getChildId());
	}

	@Test
	public void testOverCollection() {
		saveGrandchild("xpto1", child);
		saveGrandchild("xpto2", child);
		saveGrandchild("xpto3", saveChild());

		String json = put(uri("/parents/%s/children/%s/grandchildren/touched", parent, child));
		List<Grandchild> grandchildren = fromList(json, Grandchild.class);

		assertEquals(2, grandchildren.size());
		assertEquals("touched xpto1", grandchildren.get(0).getName());
		assertEquals("touched xpto2", grandchildren.get(1).getName());
		assertEquals(child.getId(), grandchildren.get(0).getChildId());
		assertEquals(child.getId(), grandchildren.get(1).getChildId());
	}
}
