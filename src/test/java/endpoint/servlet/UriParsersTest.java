package endpoint.servlet;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import java.util.List;

import org.junit.Test;

public class UriParsersTest {

	@Test
	public void testRootCollection() {
		UriInfo uriInfo = UriInfo.parse("/objects");

		assertTrue(uriInfo.isOverCollection());
		assertFalse(uriInfo.isCustomAction());
		assertResources(uriInfo, 1, "/objects", null);
	}

	@Test
	public void testRootResource() {
		UriInfo uriInfo = UriInfo.parse("/objects/1");

		assertFalse(uriInfo.isOverCollection());
		assertFalse(uriInfo.isCustomAction());
		assertResources(uriInfo, 1, "/objects", 1l);
	}

//	@Test
//	public void testNestedCollection() {
//		UriInfo uriInfo = UriInfo.parse("/objects/1/children");
//
//		assertTrue(uriInfo.isOverCollection());
//		assertFalse(uriInfo.isCustomAction());
//		assertResources(uriInfo, 1, "/objects", 1l, "/chidren", null);
//	}

	private void assertResources(UriInfo uriParser, int size, Object... resourcesOptions) {
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
