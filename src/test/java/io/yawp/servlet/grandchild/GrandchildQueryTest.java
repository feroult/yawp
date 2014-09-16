package io.yawp.servlet.grandchild;

import static org.junit.Assert.assertEquals;
import io.yawp.repository.models.parents.Child;
import io.yawp.repository.models.parents.Grandchild;
import io.yawp.repository.models.parents.Parent;

import java.util.List;

import org.junit.Test;

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

	@Test
	public void testGlobalQueryFromParent() {
		saveGrandchild("xpto1", child);
		saveGrandchild("xpto2", child);

		Parent parentX = saveParent();
		saveGrandchild("xpto1", saveChild(parentX));

		String json = get(uri("/parents/%s/grandchildren", parent), params("q", "{ where: ['name', '=', 'xpto1' ] }"));
		List<Grandchild> grandchildren = fromList(json, Grandchild.class);
		assertEquals(1, grandchildren.size());
		assertEquals("xpto1", grandchildren.get(0).getName());
		assertEquals(parent.getId(), grandchildren.get(0).getChildId().getParentId());

		String jsonX = get(uri("/parents/%s/grandchildren", parentX), params("q", "{ where: ['name', '=', 'xpto1' ] }"));
		List<Grandchild> grandchildrenX = fromList(jsonX, Grandchild.class);
		assertEquals(1, grandchildrenX.size());
		assertEquals("xpto1", grandchildrenX.get(0).getName());
		assertEquals(parentX.getId(), grandchildrenX.get(0).getChildId().getParentId());
	}
}
