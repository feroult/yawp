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
	//
	// @Test
	// public void testGlobalIndex() {
	// Parent parent1 = new Parent();
	// r.save(parent1);
	//
	// Child child1 = new Child("xpto1");
	// child1.setParentId(parent1.getId());
	// r.save(child1);
	//
	// Parent parent2 = new Parent();
	// r.save(parent2);
	//
	// Child child2 = new Child("xpto2");
	// child2.setParentId(parent2.getId());
	// r.save(child2);
	//
	// String json1 = get(uri("/parents/%d/children", parent1));
	// List<Child> children1 = fromList(json1, Child.class);
	// assertEquals(1, children1.size());
	// assertEquals("xpto1", children1.get(0).getName());
	// assertEquals(parent1.getId(), children1.get(0).getChildId());
	//
	// String json2 = get(uri("/parents/%d/children", parent2));
	// List<Child> children2 = fromList(json2, Child.class);
	// assertEquals(1, children2.size());
	// assertEquals("xpto2", children2.get(0).getName());
	// assertEquals(parent2.getId(), children2.get(0).getChildId());
	//
	// String jsonGlobal = get("/children");
	// List<Child> childrenGlobal = fromList(jsonGlobal, Child.class);
	//
	// assertEquals(2, childrenGlobal.size());
	// assertEquals("xpto1", childrenGlobal.get(0).getName());
	// assertEquals("xpto2", childrenGlobal.get(1).getName());
	// assertEquals(parent1.getId(), childrenGlobal.get(0).getChildId());
	// assertEquals(parent2.getId(), childrenGlobal.get(1).getChildId());
	// }
	//
	// @Test
	// public void testCreateAndShowGrandchild() {
	// Parent parent = new Parent();
	// r.save(parent);
	//
	// Child child = new Child();
	// r.save(child);
	//
	// String createJson = post(child.getId() + "/grandchildren",
	// String.format("{ name: 'xpto', childId: '%s' }", child.getId()));
	// Grandchild createGrandchild = from(createJson, Grandchild.class);
	// assertEquals("xpto", createGrandchild.getName());
	// assertEquals(child.getId(), createGrandchild.getChildId());
	//
	// String showJson = get(createGrandchild.getId().getUri());
	// Grandchild showGrandchild = from(showJson, Grandchild.class);
	// assertEquals("xpto", showGrandchild.getName());
	// assertEquals(child.getId(), showGrandchild.getChildId());
	// }

}
