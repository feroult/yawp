package endpoint.servlet;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

import endpoint.repository.models.parents.Parent;

public class NewEndpointServletTest extends EndpointServletTestCase {

	@Test
	public void testCreateParent() {
		String postJson = post("/parents", "{ name: 'xpto' }");
		Parent postParent = from(postJson, Parent.class);
		assertEquals("xpto", postParent.getName());

		String getJson = get(postParent.getId().getUri());
		Parent getParent = from(getJson, Parent.class);
		assertEquals("xpto", getParent.getName());
	}

}
