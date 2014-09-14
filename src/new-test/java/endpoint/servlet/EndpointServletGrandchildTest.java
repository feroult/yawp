package endpoint.servlet;

import static org.junit.Assert.assertEquals;

import java.util.List;

import org.junit.Before;
import org.junit.Test;

import endpoint.repository.models.parents.Child;
import endpoint.repository.models.parents.Grandchild;
import endpoint.repository.models.parents.Parent;

public class EndpointServletGrandchildTest extends ServletTestCase {

	private Parent parent;

	private Child child;

	@Before
	public void before() {
		parent = new Parent();
		r.save(parent);

		child = new Child();
		child.setParentId(parent.getId());
		r.save(child);
	}

	@Test
	public void testCreate() {
		String json = post(uri("/parents/%d/children/%d/grandchildren", parent, child),
				String.format("{ name: 'xpto', childId: '%s' }", child.getId()));

		Grandchild grandchild = from(json, Grandchild.class);
		assertEquals("xpto", grandchild.getName());
		assertEquals(child.getId(), grandchild.getChildId());
	}

	@Test
	public void testCreateArray() {
		String json = post(uri("/parents/%d/children/%d/grandchildren", parent, child),
				String.format("[ { name: 'xpto1', childId: '%s' }, { name: 'xpto2', childId: '%s' } ]", child.getId(), child.getId()));
		List<Grandchild> grandchildren = fromList(json, Grandchild.class);

		assertEquals(2, grandchildren.size());
		assertEquals("xpto1", grandchildren.get(0).getName());
		assertEquals("xpto2", grandchildren.get(1).getName());
		assertEquals(child.getId(), grandchildren.get(0).getChildId());
		assertEquals(child.getId(), grandchildren.get(1).getChildId());
	}

	@Test
	public void testShow() {
		Grandchild grandchild = new Grandchild("xpto");
		grandchild.setChildId(child.getId());
		r.save(grandchild);

		String json = get(uri("/parents/%d/children/%d/grandchildren/%d", parent, child, grandchild));
		Grandchild retrievedGrandchild = from(json, Grandchild.class);

		assertEquals("xpto", retrievedGrandchild.getName());
		assertEquals(child.getId(), retrievedGrandchild.getChildId());
	}

	@Test
	public void testIndex() {
		Grandchild grandchild1 = new Grandchild("xpto1");
		grandchild1.setChildId(child.getId());
		r.save(grandchild1);

		Grandchild grandchild2 = new Grandchild("xpto2");
		grandchild2.setChildId(child.getId());
		r.save(grandchild2);

		String json = get(uri("/parents/%d/children/%d/grandchildren", parent, child));
		List<Grandchild> grandchildren = fromList(json, Grandchild.class);

		assertEquals(2, grandchildren.size());
		assertEquals("xpto1", grandchildren.get(0).getName());
		assertEquals("xpto2", grandchildren.get(1).getName());
		assertEquals(child.getId(), grandchildren.get(0).getChildId());
		assertEquals(child.getId(), grandchildren.get(1).getChildId());
	}

	@Test
	public void testGlobalIndex() {
		Child child1 = new Child();
		child1.setParentId(parent.getId());
		r.save(child1);

		Grandchild grandchild1 = new Grandchild("xpto1");
		grandchild1.setChildId(child1.getId());
		r.save(grandchild1);

		Child child2 = new Child();
		child2.setParentId(parent.getId());
		r.save(child2);

		Grandchild grandchild2 = new Grandchild("xpto2");
		grandchild2.setChildId(child2.getId());
		r.save(grandchild2);

		String json1 = get(uri("/parents/%d/children/%d/grandchildren", parent, child1));
		List<Grandchild> grandchildren1 = fromList(json1, Grandchild.class);
		assertEquals(1, grandchildren1.size());
		assertEquals("xpto1", grandchildren1.get(0).getName());
		assertEquals(child1.getId(), grandchildren1.get(0).getChildId());

		String json2 = get(uri("/parents/%d/children/%d/grandchildren", parent, child2));
		List<Grandchild> grandchildren2 = fromList(json2, Grandchild.class);
		assertEquals(1, grandchildren2.size());
		assertEquals("xpto2", grandchildren2.get(0).getName());
		assertEquals(child2.getId(), grandchildren2.get(0).getChildId());

		String jsonParentGlobal = get(uri("/parents/%d/grandchildren", parent));
		assertAllGrandchildren(jsonParentGlobal, child1, child2);

		String jsonGlobal = get("/grandchildren");
		assertAllGrandchildren(jsonGlobal, child1, child2);
	}

	private void assertAllGrandchildren(String jsonGlobal, Child child1, Child child2) {
		List<Grandchild> grandchildrenGlobal = fromList(jsonGlobal, Grandchild.class);

		assertEquals(2, grandchildrenGlobal.size());
		assertEquals("xpto1", grandchildrenGlobal.get(0).getName());
		assertEquals("xpto2", grandchildrenGlobal.get(1).getName());
		assertEquals(child1.getId(), grandchildrenGlobal.get(0).getChildId());
		assertEquals(child2.getId(), grandchildrenGlobal.get(1).getChildId());
	}

}
