package endpoint.servlet;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;

import org.junit.Ignore;
import org.junit.Test;

import endpoint.repository.SimpleObject;
import endpoint.utils.EndpointTestCase;

public class EndpointRouterTest extends EndpointTestCase {

	@Test
	public void testBasicRoute() {
		EndpointRouter router;

		router = EndpointRouter.generateRouteFor(r, "GET", "/simpleobjects/1");
		assertRouter(router, SimpleObject.class, RestAction.SHOW, 1l);

		router = EndpointRouter.generateRouteFor(r, "POST", "/simpleobjects/1");
		assertRouter(router, SimpleObject.class, RestAction.CREATE, 1l);

		router = EndpointRouter.generateRouteFor(r, "PUT", "/simpleobjects/1");
		assertRouter(router, SimpleObject.class, RestAction.UPDATE, 1l);

		router = EndpointRouter.generateRouteFor(r, "DELETE", "/simpleobjects/1");
		assertRouter(router, SimpleObject.class, RestAction.DELETE, 1l);
	}

	@Test
	public void testCollectionRoute() {
		EndpointRouter router = EndpointRouter.generateRouteFor(r, "POST", "/simpleobjects");
		assertRouter(router, SimpleObject.class, RestAction.CREATE, null);
	}

	@Test
	@Ignore
	public void testCustomActionRoute() {
		EndpointRouter router = EndpointRouter.generateRouteFor(r, "PUT", "/simpleobjects/1/active");
		assertRouter(router, SimpleObject.class, RestAction.CUSTOM, 1l);
		assertEquals("active", router.getCustomAction());
	}

	private void assertRouter(EndpointRouter router, Class<SimpleObject> clazz, RestAction restAction, Long id) {
		assertEquals(clazz, router.getEndpoint().getClazz());
		assertEquals(restAction, router.getRestAction());
		if (id != null) {
			assertEquals(id, router.getIdRef().asLong());
		} else {
			assertNull(router.getIdRef());
		}
	}

}
