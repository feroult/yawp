package endpoint.servlet;

import static org.junit.Assert.assertEquals;

import java.util.List;

import org.junit.Before;
import org.junit.Test;

import endpoint.repository.models.parents.Child;
import endpoint.repository.models.parents.Parent;

public class EndpointServletChildTest extends ServletTestCase {

	private Parent parent;

	@Before
	public void before() {
		parent = new Parent();
		r.save(parent);
	}

	@Test
	public void testCreate() {
		String json = post(uri("/parents/%d/children", parent), String.format("{ name: 'xpto', parentId: '%s' }", parent.getId()));

		Child child = from(json, Child.class);
		assertEquals("xpto", child.getName());
		assertEquals(parent.getId(), child.getParentId());
	}

	@Test
	public void testCreateArray() {
		String json = post(uri("/parents/%d/children", parent),
				String.format("[ { name: 'xpto1', parentId: '%s' }, { name: 'xpto2', parentId: '%s' } ]", parent.getId(), parent.getId()));
		List<Child> children = fromList(json, Child.class);

		assertEquals(2, children.size());
		assertEquals("xpto1", children.get(0).getName());
		assertEquals("xpto2", children.get(1).getName());
		assertEquals(parent.getId(), children.get(0).getParentId());
		assertEquals(parent.getId(), children.get(1).getParentId());
	}

	@Test
	public void testShow() {
		Child child = new Child("xpto");
		child.setParentId(parent.getId());
		r.save(child);

		String json = get(uri("/parents/%d/children/%d", parent, child));
		Child retrievedChild = from(json, Child.class);

		assertEquals("xpto", retrievedChild.getName());
		assertEquals(parent.getId(), retrievedChild.getParentId());
	}

	@Test
	public void testIndex() {
		Child child1 = new Child("xpto1");
		child1.setParentId(parent.getId());
		r.save(child1);

		Child child2 = new Child("xpto2");
		child2.setParentId(parent.getId());
		r.save(child2);

		String json = get(uri("/parents/%d/children", parent));
		List<Child> children = fromList(json, Child.class);

		assertEquals(2, children.size());
		assertEquals("xpto1", children.get(0).getName());
		assertEquals("xpto2", children.get(1).getName());
		assertEquals(parent.getId(), children.get(0).getParentId());
		assertEquals(parent.getId(), children.get(1).getParentId());
	}

	@Test
	public void testGlobalIndex() {
		Parent parent1 = new Parent();
		r.save(parent1);

		Child child1 = new Child("xpto1");
		child1.setParentId(parent1.getId());
		r.save(child1);

		Parent parent2 = new Parent();
		r.save(parent2);

		Child child2 = new Child("xpto2");
		child2.setParentId(parent2.getId());
		r.save(child2);

		String json1 = get(uri("/parents/%d/children", parent1));
		List<Child> children1 = fromList(json1, Child.class);
		assertEquals(1, children1.size());
		assertEquals("xpto1", children1.get(0).getName());
		assertEquals(parent1.getId(), children1.get(0).getParentId());

		String json2 = get(uri("/parents/%d/children", parent2));
		List<Child> children2 = fromList(json2, Child.class);
		assertEquals(1, children2.size());
		assertEquals("xpto2", children2.get(0).getName());
		assertEquals(parent2.getId(), children2.get(0).getParentId());

		String jsonGlobal = get("/children");
		List<Child> childrenGlobal = fromList(jsonGlobal, Child.class);

		assertEquals(2, childrenGlobal.size());
		assertEquals("xpto1", childrenGlobal.get(0).getName());
		assertEquals("xpto2", childrenGlobal.get(1).getName());
		assertEquals(parent1.getId(), childrenGlobal.get(0).getParentId());
		assertEquals(parent2.getId(), childrenGlobal.get(1).getParentId());
	}

}
