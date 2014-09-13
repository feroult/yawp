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
	public void testCreate() {
		String json = post("/basic_objects", "{ stringValue: 'xpto' } ");
		BasicObject object = from(json, BasicObject.class);
		assertEquals("xpto", object.getStringValue());
	}

	@Test
	public void testCreateArray() {
		String json = post("/basic_objects", "[ { stringValue: 'xpto1' }, { stringValue: 'xpto2' } ]");
		List<BasicObject> objects = fromList(json, BasicObject.class);
		assertEquals(2, objects.size());
		assertEquals("xpto1", objects.get(0).getStringValue());
		assertEquals("xpto2", objects.get(1).getStringValue());
	}

	@Test
	public void testShow() {
		BasicObject object = new BasicObject("xpto");
		r.save(object);

		String json = get(object.getId().getUri());
		BasicObject retrivedObject = from(json, BasicObject.class);
		assertEquals("xpto", retrivedObject.getStringValue());
	}

	@Test
	public void testIndex() {
		r.save(new BasicObject("xpto1"));
		r.save(new BasicObject("xpto2"));

		String json = get("/basic_objects");
		List<BasicObject> objects = fromList(json, BasicObject.class);
		assertEquals(2, objects.size());
		assertEquals("xpto1", objects.get(0).getStringValue());
		assertEquals("xpto2", objects.get(1).getStringValue());
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
