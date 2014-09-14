package endpoint.servlet.grandchild;

import static org.junit.Assert.assertEquals;

import java.util.List;

import org.junit.Before;
import org.junit.Test;

import endpoint.repository.models.parents.Child;
import endpoint.repository.models.parents.Grandchild;
import endpoint.repository.models.parents.Parent;
import endpoint.servlet.ServletTestCase;

public class GrandchildRestTest extends ServletTestCase {

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
		String json = post(uri("/parents/%s/children/%s/grandchildren", parent, child), json("{ name: 'xpto', childId: '%s' }", child));

		Grandchild grandchild = from(json, Grandchild.class);
		assertEquals("xpto", grandchild.getName());
		assertEquals(child.getId(), grandchild.getChildId());
	}

	@Test
	public void testCreateArray() {
		String json = post(uri("/parents/%s/children/%s/grandchildren", parent, child),
				json("[ { name: 'xpto1', childId: '%s' }, { name: 'xpto2', childId: '%s' } ]", child, child));
		List<Grandchild> grandchildren = fromList(json, Grandchild.class);

		assertEquals(2, grandchildren.size());
		assertEquals("xpto1", grandchildren.get(0).getName());
		assertEquals("xpto2", grandchildren.get(1).getName());
		assertEquals(child.getId(), grandchildren.get(0).getChildId());
		assertEquals(child.getId(), grandchildren.get(1).getChildId());
	}

	@Test
	public void testUpdate() {
		Grandchild grandchild = saveGrandchild("xpto", child);

		String json = put(uri("/parents/%s/children/%s/grandchildren/%s", parent, child, grandchild),
				json("{ name: 'changed xpto', childId: '%s', id: '%s' }", child, grandchild));
		Grandchild retrievedGrandchild = from(json, Grandchild.class);

		assertEquals("changed xpto", retrievedGrandchild.getName());
		assertEquals(child.getId(), retrievedGrandchild.getChildId());
	}

	@Test
	public void testShow() {
		Grandchild grandchild = saveGrandchild("xpto", child);

		String json = get(uri("/parents/%s/children/%s/grandchildren/%s", parent, child, grandchild));
		Grandchild retrievedGrandchild = from(json, Grandchild.class);

		assertEquals("xpto", retrievedGrandchild.getName());
		assertEquals(child.getId(), retrievedGrandchild.getChildId());
	}

	@Test
	public void testIndex() {
		saveGrandchild("xpto1", child);
		saveGrandchild("xpto2", child);

		String json = get(uri("/parents/%s/children/%s/grandchildren", parent, child));
		List<Grandchild> grandchildren = fromList(json, Grandchild.class);

		assertEquals(2, grandchildren.size());
		assertEquals("xpto1", grandchildren.get(0).getName());
		assertEquals("xpto2", grandchildren.get(1).getName());
		assertEquals(child.getId(), grandchildren.get(0).getChildId());
		assertEquals(child.getId(), grandchildren.get(1).getChildId());
	}

	@Test
	public void testGlobalIndex() {
		Parent parentX = saveParent();

		Child child1 = saveChild();
		Child child2 = saveChild();

		saveGrandchild("xpto1", child1);
		saveGrandchild("xpto2", child2);

		String json1 = get(uri("/parents/%s/children/%s/grandchildren", parent, child1));
		List<Grandchild> grandchildren1 = fromList(json1, Grandchild.class);
		assertEquals(1, grandchildren1.size());
		assertEquals("xpto1", grandchildren1.get(0).getName());
		assertEquals(child1.getId(), grandchildren1.get(0).getChildId());

		String json2 = get(uri("/parents/%s/children/%s/grandchildren", parent, child2));
		List<Grandchild> grandchildren2 = fromList(json2, Grandchild.class);
		assertEquals(1, grandchildren2.size());
		assertEquals("xpto2", grandchildren2.get(0).getName());
		assertEquals(child2.getId(), grandchildren2.get(0).getChildId());

		String jsonParentX = get(uri("/parents/%s/grandchildren", parentX));
		assertEquals("[]", jsonParentX);

		String jsonParent = get(uri("/parents/%s/grandchildren", parent));
		assertAllGrandchildren(jsonParent, child1, child2);

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

	private Parent saveParent() {
		Parent parentX = new Parent();
		r.save(parentX);
		return parentX;
	}

	private Child saveChild() {
		Child child = new Child();
		child.setParentId(parent.getId());
		r.save(child);
		return child;
	}

	private Grandchild saveGrandchild(String name, Child child) {
		Grandchild grandchild = new Grandchild(name);
		grandchild.setChildId(child.getId());
		r.save(grandchild);
		return grandchild;
	}
}
