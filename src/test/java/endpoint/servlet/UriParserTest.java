package endpoint.servlet;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import java.util.List;

import org.junit.Test;

public class UriParserTest {

	@Test
	public void testRootCollection() {
		UriParser uriInfo = UriParser.parse("/objects");

		assertTrue(uriInfo.isOverCollection());
		assertFalse(uriInfo.isCustomAction());
		assertResources(uriInfo, 1, "/objects", null);
	}

	@Test
	public void testRootResource() {
		UriParser uriInfo = UriParser.parse("/objects/1");

		assertFalse(uriInfo.isOverCollection());
		assertFalse(uriInfo.isCustomAction());
		assertResources(uriInfo, 1, "/objects", 1l);
	}

	@Test
	public void testNestedCollection() {
		UriParser uriInfo = UriParser.parse("/objects/1/children");

		assertTrue(uriInfo.isOverCollection());
		assertFalse(uriInfo.isCustomAction());
		assertResources(uriInfo, 2, "/objects", 1l, "/children", null);
	}

	@Test
	public void testNestedResource() {
		UriParser uriInfo = UriParser.parse("/objects/1/children/1");

		assertFalse(uriInfo.isOverCollection());
		assertFalse(uriInfo.isCustomAction());
		assertResources(uriInfo, 2, "/objects", 1l, "/children", 1l);
	}

	@Test
	public void testTwoNestedCollection() {
		UriParser uriInfo = UriParser.parse("/objects/1/children/1/grandchildren");

		assertTrue(uriInfo.isOverCollection());
		assertFalse(uriInfo.isCustomAction());
		assertResources(uriInfo, 3, "/objects", 1l, "/children", 1l, "/grandchildren", null);
	}

	@Test
	public void testTwoNestedResource() {
		UriParser uriInfo = UriParser.parse("/objects/1/children/1/grandchildren/1");

		assertFalse(uriInfo.isOverCollection());
		assertFalse(uriInfo.isCustomAction());
		assertResources(uriInfo, 3, "/objects", 1l, "/children", 1l, "/grandchildren", 1l);
	}

	private void assertResources(UriParser uriParser, int size, Object... resourcesOptions) {
		List<RouteResource> resources = uriParser.getResources();

		assertEquals(size, resources.size());

		for (int i = 0; i < size; i++) {
			String endpointPath = (String) resourcesOptions[i * 2];
			Long id = (Long) resourcesOptions[i * 2 + 1];

			assertEquals(endpointPath, resources.get(i).getEndpointPath());
			if (id == null) {
				assertNull(resources.get(i).getId());
			} else {
				assertEquals(id, resources.get(i).getId());
			}
		}
	}

}
