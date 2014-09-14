package endpoint.servlet;

import static org.junit.Assert.assertEquals;

import java.util.List;

import org.junit.Test;

import endpoint.repository.models.parents.Parent;

public class EndpointServletParentTest extends ServletTestCase {

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
	public void testShow() {
		Parent object = new Parent("xpto");
		r.save(object);

		String json = get(uri("/parents/%d", object));
		Parent retrivedObject = from(json, Parent.class);

		assertEquals("xpto", retrivedObject.getName());
	}

	@Test
	public void testIndex() {
		r.save(new Parent("xpto1"));
		r.save(new Parent("xpto2"));

		String json = get("/parents");
		List<Parent> parents = fromList(json, Parent.class);

		assertEquals(2, parents.size());
		assertEquals("xpto1", parents.get(0).getName());
		assertEquals("xpto2", parents.get(1).getName());
	}
}
