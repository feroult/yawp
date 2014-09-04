package endpoint.servlet;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import java.util.ArrayList;
import java.util.List;

import org.junit.Before;
import org.junit.Test;

import endpoint.repository.EndpointFeatures;
import endpoint.repository.RepositoryFeatures;
import endpoint.repository.SimpleObject;
import endpoint.repository.actions.ActionKey;
import endpoint.servlet.EndpointRouter.RouteResource;
import endpoint.utils.EndpointTestCase;
import endpoint.utils.HttpVerb;

public class EndpointRouterTest extends EndpointTestCase {

	private class RepositoryFeaturesMock extends RepositoryFeatures {

		public RepositoryFeaturesMock() {
			super(new ArrayList<EndpointFeatures<?>>());
		}

		@Override
		public boolean hasCustomAction(String path, ActionKey actionKey) {
			return actionKey.getActionName().equals("action");
		}

		@Override
		public EndpointFeatures<?> get(String path) {
			return new EndpointFeatures<SimpleObject>(SimpleObject.class);
		}
	}

	@Before
	public void before() {
		r.setFeatures(new RepositoryFeaturesMock());
	}

	private EndpointRouter parse(String uri) {
		return EndpointRouter.parse(r, HttpVerb.GET, uri);
	}

	@Test
	public void testRootCollection() {
		EndpointRouter router = parse("/objects");

		assertTrue(router.isOverCollection());
		assertFalse(router.isCustomAction());
		assertResources(router, 1, "/objects", null);
	}

	@Test
	public void testRootResource() {
		EndpointRouter router = parse("/objects/1");

		assertFalse(router.isOverCollection());
		assertFalse(router.isCustomAction());
		assertResources(router, 1, "/objects", 1l);
	}

	@Test
	public void testNestedCollection() {
		EndpointRouter router = parse("/objects/1/children");

		assertTrue(router.isOverCollection());
		assertFalse(router.isCustomAction());
		assertResources(router, 2, "/objects", 1l, "/children", null);
	}

	@Test
	public void testNestedResource() {
		EndpointRouter router = parse("/objects/1/children/1");

		assertFalse(router.isOverCollection());
		assertFalse(router.isCustomAction());
		assertResources(router, 2, "/objects", 1l, "/children", 1l);
	}

	@Test
	public void testTwoNestedCollection() {
		EndpointRouter router = parse("/objects/1/children/1/grandchildren");

		assertTrue(router.isOverCollection());
		assertFalse(router.isCustomAction());
		assertResources(router, 3, "/objects", 1l, "/children", 1l, "/grandchildren", null);
	}

	@Test
	public void testTwoNestedResource() {
		EndpointRouter router = parse("/objects/1/children/1/grandchildren/1");

		assertFalse(router.isOverCollection());
		assertFalse(router.isCustomAction());
		assertResources(router, 3, "/objects", 1l, "/children", 1l, "/grandchildren", 1l);
	}

	@Test
	public void testRootCollectionAction() {
		EndpointRouter router = parse("/objects/action");
		assertTrue(router.isOverCollection());
		assertTrue(router.isCustomAction());
		assertEquals("action", router.getCustomActionName());
		assertResources(router, 1, "/objects", null);
	}

	@Test
	public void testRootResourceAction() {
		EndpointRouter router = parse("/objects/1/action");
		assertFalse(router.isOverCollection());
		assertTrue(router.isCustomAction());
		assertEquals("action", router.getCustomActionName());
		assertResources(router, 1, "/objects", 1l);
	}

	@Test
	public void testNestedCollectionAction() {
		EndpointRouter router = parse("/objects/1/children/action");
		assertTrue(router.isOverCollection());
		assertTrue(router.isCustomAction());
		assertEquals("action", router.getCustomActionName());
		assertResources(router, 2, "/objects", 1l, "/children", null);
	}

	@Test
	public void testNestedResourceAction() {
		EndpointRouter router = parse("/objects/1/children/1/action");
		assertFalse(router.isOverCollection());
		assertTrue(router.isCustomAction());
		assertEquals("action", router.getCustomActionName());
		assertResources(router, 2, "/objects", 1l, "/children", 1l);
	}

	@Test
	public void testTwoNestedCollectionAction() {
		EndpointRouter router = parse("/objects/1/children/1/grandchildren/action");

		assertTrue(router.isOverCollection());
		assertTrue(router.isCustomAction());
		assertEquals("action", router.getCustomActionName());
		assertResources(router, 3, "/objects", 1l, "/children", 1l, "/grandchildren", null);
	}

	@Test
	public void testTwoNestedResourceAction() {
		EndpointRouter router = parse("/objects/1/children/1/grandchildren/1/action");

		assertFalse(router.isOverCollection());
		assertTrue(router.isCustomAction());
		assertEquals("action", router.getCustomActionName());
		assertResources(router, 3, "/objects", 1l, "/children", 1l, "/grandchildren", 1l);
	}

	private void assertResources(EndpointRouter uriParser, int size, Object... resourcesOptions) {
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
