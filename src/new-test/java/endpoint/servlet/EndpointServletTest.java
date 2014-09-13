package endpoint.servlet;

import static org.junit.Assert.assertEquals;

import java.util.List;

import org.junit.Test;

import endpoint.repository.models.basic.BasicObject;
import endpoint.repository.models.parents.Child;
import endpoint.repository.models.parents.Grandchild;
import endpoint.repository.models.parents.Parent;

public class EndpointServletTest extends TestCase {

	@Test
	public void testCreateAndShow() {
		String createJson = post("/basic_objects", "{ stringValue: 'xpto' } ");
		BasicObject createObject = from(createJson, BasicObject.class);
		assertEquals("xpto", createObject.getStringValue());

		String showJson = get(createObject.getId().getUri());
		BasicObject showObject = from(showJson, BasicObject.class);
		assertEquals("xpto", showObject.getStringValue());
	}

	@Test
	public void testCreateFromArrayAndIndex() {
		String createJson = post("/basic_objects", "[ { stringValue: 'xpto1' }, { stringValue: 'xpto2' } ]");
		List<BasicObject> createObjects = fromList(createJson, BasicObject.class);
		assertEquals(2, createObjects.size());
		assertEquals("xpto1", createObjects.get(0).getStringValue());
		assertEquals("xpto2", createObjects.get(1).getStringValue());

		String indexJson = get("/basic_objects");
		List<BasicObject> indexObjects = fromList(indexJson, BasicObject.class);
		assertEquals(2, indexObjects.size());
		assertEquals("xpto1", indexObjects.get(0).getStringValue());
		assertEquals("xpto2", indexObjects.get(1).getStringValue());
	}

	@Test
	public void testCreateAndShowParent() {
		String createJson = post("/parents", "{ name: 'xpto' }");
		Parent createParent = from(createJson, Parent.class);
		assertEquals("xpto", createParent.getName());

		String showJson = get(createParent.getId().getUri());
		Parent showParent = from(showJson, Parent.class);
		assertEquals("xpto", showParent.getName());
	}

	@Test
	public void testCreateAndShowChild() {
		Parent parent = new Parent();
		r.save(parent);

		String createJson = post(parent.getId() + "/children", String.format("{ name: 'xpto', parentId: '%s' }", parent.getId()));
		Child createChild = from(createJson, Child.class);
		assertEquals("xpto", createChild.getName());
		assertEquals(parent.getId(), createChild.getParentId());

		String showJson = get(createChild.getId().getUri());
		Child showChild = from(showJson, Child.class);
		assertEquals("xpto", showChild.getName());
		assertEquals(parent.getId(), showChild.getParentId());
	}

	@Test
	public void testCreateAndShowGrandchild() {
		Parent parent = new Parent();
		r.save(parent);

		Child child = new Child();
		r.save(child);

		String createJson = post(child.getId() + "/grandchildren", String.format("{ name: 'xpto', childId: '%s' }", child.getId()));
		Grandchild createGrandchild = from(createJson, Grandchild.class);
		assertEquals("xpto", createGrandchild.getName());
		assertEquals(child.getId(), createGrandchild.getChildId());

		String showJson = get(createGrandchild.getId().getUri());
		Grandchild showGrandchild = from(showJson, Grandchild.class);
		assertEquals("xpto", showGrandchild.getName());
		assertEquals(child.getId(), showGrandchild.getChildId());
	}
}
