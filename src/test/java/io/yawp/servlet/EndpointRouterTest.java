package io.yawp.servlet;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import io.yawp.repository.EndpointFeatures;
import io.yawp.repository.RepositoryFeatures;
import io.yawp.repository.actions.ActionKey;
import io.yawp.utils.EndpointTestCase;
import io.yawp.utils.HttpVerb;

import java.util.ArrayList;

import org.junit.Before;
import org.junit.Test;

public class EndpointRouterTest extends EndpointTestCase {

	private class RepositoryFeaturesMock extends RepositoryFeatures {

		private RepositoryFeatures features;

		public RepositoryFeaturesMock(RepositoryFeatures features) {
			super(new ArrayList<EndpointFeatures<?>>());
			this.features = features;
		}

		@Override
		public boolean hasCustomAction(String endpointPath, ActionKey actionKey) {
			if (actionKey.getActionName().equals("action")) {
				return true;
			}
			return features.hasCustomAction(endpointPath, actionKey);
		}

		@Override
		public boolean hasCustomAction(Class<?> clazz, ActionKey actionKey) {
			if (actionKey.getActionName().equals("action")) {
				return true;
			}
			return features.hasCustomAction(clazz, actionKey);
		}

		@Override
		public EndpointFeatures<?> get(String endpointPath) {
			return features.get(endpointPath);
		}

		@Override
		public EndpointFeatures<?> get(Class<?> clazz) {
			return features.get(clazz);
		}

	}

	@Before
	public void before() {
		yawp.setFeatures(new RepositoryFeaturesMock(yawp.getFeatures()));
	}

	private EndpointRouter parse(String uri) {
		return EndpointRouter.parse(yawp, HttpVerb.GET, uri);
	}

	@Test
	public void testRootCollection() {
		EndpointRouter router = parse("/parents");

		assertTrue(router.isOverCollection());
		assertFalse(router.isCustomAction());
	}

	@Test
	public void testRootResource() {
		EndpointRouter router = parse("/parents/1");

		assertFalse(router.isOverCollection());
		assertFalse(router.isCustomAction());
	}

	@Test
	public void testNestedCollection() {
		EndpointRouter router = parse("/parents/1/children");

		assertTrue(router.isOverCollection());
		assertFalse(router.isCustomAction());
	}

	@Test
	public void testNestedResource() {
		EndpointRouter router = parse("/parents/1/children/1");

		assertFalse(router.isOverCollection());
		assertFalse(router.isCustomAction());
	}

	@Test
	public void testTwoNestedCollection() {
		EndpointRouter router = parse("/parents/1/children/1/grandchildren");

		assertTrue(router.isOverCollection());
		assertFalse(router.isCustomAction());
	}

	@Test
	public void testTwoNestedResource() {
		EndpointRouter router = parse("/parents/1/children/1/grandchildren/1");

		assertFalse(router.isOverCollection());
		assertFalse(router.isCustomAction());
	}

	@Test
	public void testRootCollectionAction() {
		EndpointRouter router = parse("/parents/action");
		assertTrue(router.isOverCollection());
		assertTrue(router.isCustomAction());
		assertEquals("action", router.getCustomActionName());
	}

	@Test
	public void testRootResourceAction() {
		EndpointRouter router = parse("/parents/1/action");
		assertFalse(router.isOverCollection());
		assertTrue(router.isCustomAction());
		assertEquals("action", router.getCustomActionName());
	}

	@Test
	public void testNestedCollectionAction() {
		EndpointRouter router = parse("/parents/1/children/action");
		assertTrue(router.isOverCollection());
		assertTrue(router.isCustomAction());
		assertEquals("action", router.getCustomActionName());
	}

	@Test
	public void testNestedResourceAction() {
		EndpointRouter router = parse("/parents/1/children/1/action");
		assertFalse(router.isOverCollection());
		assertTrue(router.isCustomAction());
		assertEquals("action", router.getCustomActionName());
	}

	@Test
	public void testTwoNestedCollectionAction() {
		EndpointRouter router = parse("/parents/1/children/1/grandchildren/action");

		assertTrue(router.isOverCollection());
		assertTrue(router.isCustomAction());
		assertEquals("action", router.getCustomActionName());
	}

	@Test
	public void testTwoNestedResourceAction() {
		EndpointRouter router = parse("/parents/1/children/1/grandchildren/1/action");

		assertFalse(router.isOverCollection());
		assertTrue(router.isCustomAction());
		assertEquals("action", router.getCustomActionName());
	}
}
