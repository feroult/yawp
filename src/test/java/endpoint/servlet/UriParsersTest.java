package endpoint.servlet;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;

import java.util.List;

import org.junit.Ignore;
import org.junit.Test;

public class UriParsersTest {

	@Test
	@Ignore
	public void testParseResources() {
		assertResources(UriParser.parse("/objects"), 1, "/objects", null);
		assertResources(UriParser.parse("/objects/1"), 1, "/objects", 1l);
		assertResources(UriParser.parse("/objects/1/active"), 1, "/objects", 1l);
		assertResources(UriParser.parse("/objects/1/chidren"), 1, "/objects", 1l, "/children", null);
	}

	private void assertResources(UriParser uriParser, int size, Object... resourcesOptions) {
		List<RouteResource> resources = uriParser.getResources();

		assertEquals(size, resources.size());

		for (int i = 0; i < size; i++) {
			String endpointPath = (String) resourcesOptions[i * 2];
			Long id = (Long) resourcesOptions[i * 2 + 1];

			assertEquals(endpointPath, resources.get(i).getEndpointPath());
			if (id != null) {
				assertNull(resources.get(i).getId());
			} else {
				assertEquals(id, resources.get(i).getId());
			}
		}
	}

}
