package io.yawp.servlet;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import io.yawp.commons.http.HttpException;
import io.yawp.commons.http.HttpVerb;
import io.yawp.commons.utils.ServletTestCase;
import io.yawp.repository.EndpointFeatures;
import io.yawp.repository.RepositoryFeatures;
import io.yawp.repository.actions.ActionKey;

import java.util.ArrayList;

import org.junit.Before;
import org.junit.Test;

public class EndpointRouterTest extends ServletTestCase {

    private class RepositoryFeaturesMock extends RepositoryFeatures {

        private RepositoryFeatures features;

        public RepositoryFeaturesMock(RepositoryFeatures features) {
            super(new ArrayList<EndpointFeatures<?>>());
            this.features = features;
        }

        @Override
        public boolean hasCustomAction(String endpointPath, ActionKey actionKey) {
            return actionKey.getActionName().equals("action") || features.hasCustomAction(endpointPath, actionKey);
        }

        @Override
        public boolean hasCustomAction(Class<?> clazz, ActionKey actionKey) {
            return actionKey.getActionName().equals("action") || features.hasCustomAction(clazz, actionKey);
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
        return EndpointRouter.parse(yawp, ctx("GET", uri));
    }

    private EndpointRouter parse(String method, String uri, String requestJson) {
        return EndpointRouter.parse(yawp, ctx(method, uri, requestJson));
    }

    @Test
    public void testWelcome() {
        try {
            get("");
            assertTrue(false);
        } catch (HttpException e) {
            assertEquals("Welcome to YAWP!", e.getText());
        }
        try {
            get("/");
            assertTrue(false);
        } catch (HttpException e) {
            assertEquals("Welcome to YAWP!", e.getText());
        }
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

    @Test
    public void testRouteHasValidIds() {
        assertTrue(parse("POST", "/parents", "{}").tryToAdjustIds());
        assertTrue(parse("POST", "/parents", "[{}, {}]").tryToAdjustIds());
        assertTrue(parse("POST", "/parents", "{id: '/parents/1'}").tryToAdjustIds());
        assertTrue(parse("POST", "/parents", "[{id: '/parents/1'}, {id: '/parents/2'}]").tryToAdjustIds());
        assertTrue(parse("POST", "/parents", "[{id: '/parents/1'}, {}]").tryToAdjustIds());
        assertTrue(parse("PUT", "/parents/1", "{}").tryToAdjustIds());
        assertTrue(parse("PUT", "/parents/1", "{id: '/parents/1'}").tryToAdjustIds());
        assertTrue(parse("PATCH", "/parents/1", "{}").tryToAdjustIds());
        assertTrue(parse("PATCH", "/parents/1", "{id: '/parents/1'}").tryToAdjustIds());

        assertTrue(parse("POST", "/children", "{}").tryToAdjustIds());
        assertTrue(parse("POST", "/children", "[{}, {}]").tryToAdjustIds());
        assertTrue(parse("POST", "/children", "{id: '/parents/1/children/1'}").tryToAdjustIds());
        assertTrue(parse("POST", "/children", "[{id: '/parents/1/children/1'}, {id: '/parents/2/children/2'}]").tryToAdjustIds());
        assertTrue(parse("POST", "/children", "[{id: '/parents/1/children/1'}, {}]").tryToAdjustIds());

        assertTrue(parse("POST", "/parents/1/children", "{}").tryToAdjustIds());
        assertTrue(parse("POST", "/parents/1/children", "[{}, {}]").tryToAdjustIds());
        assertTrue(parse("POST", "/parents/1/children", "{id: '/parents/1/children/1'}").tryToAdjustIds());
        assertTrue(parse("POST", "/parents/1/children", "[{id: '/parents/1/children/1'}, {id: '/parents/1/children/2'}]")
                .tryToAdjustIds());
        assertTrue(parse("POST", "/parents/1/children", "{}").tryToAdjustIds());
        assertTrue(parse("POST", "/parents/1/children", "[{id: '/parents/1/children/1'}, {}]").tryToAdjustIds());
        assertTrue(parse("PUT", "/parents/1/children/1", "{}").tryToAdjustIds());
        assertTrue(parse("PUT", "/parents/1/children/1", "{id: '/parents/1/children/1'}").tryToAdjustIds());
        assertTrue(parse("PATCH", "/parents/1/children/1", "{}").tryToAdjustIds());
        assertTrue(parse("PATCH", "/parents/1/children/1", "{id: '/parents/1/children/1'}").tryToAdjustIds());

        assertTrue(parse("POST", "/grandchildren",
                "[{id: '/parents/1/children/1/grandchildren/1'}, {id: '/parents/2/children/2/grandchildren/2'}]").tryToAdjustIds());
        assertTrue(parse("POST", "/parents/1/grandchildren",
                "[{id: '/parents/1/children/1/grandchildren/1'}, {id: '/parents/1/children/2/grandchildren/2'}]").tryToAdjustIds());
    }

    @Test
    public void testInvalidRouteIds() {
        assertFalse(parse("POST", "/parents", "{id: '/basic_objects/1'}").tryToAdjustIds());
        assertFalse(parse("POST", "/parents", "[{id: '/parents/1'}, {id: '/basic_objects/1'}]").tryToAdjustIds());
        assertFalse(parse("PUT", "/parents/1", "{id: '/basic_objects/1'}").tryToAdjustIds());
        assertFalse(parse("PUT", "/parents/1", "{id: '/parents/2'}").tryToAdjustIds());
        assertFalse(parse("PATCH", "/parents/1", "{id: '/parents/2'}").tryToAdjustIds());

        assertFalse(parse("POST", "/parents/1/children", "{id: '/basic_objects/1'}").tryToAdjustIds());
        assertFalse(parse("POST", "/parents/1/children", "{id: '/parents/1/children'}").tryToAdjustIds());
        assertFalse(parse("POST", "/parents/1/children", "[{id: '/parents/1/children/1'}, {id: '/basic_objects/1'}]")
                .tryToAdjustIds());
        assertFalse(parse("POST", "/parents/1/children", "[{id: '/parents/1/children/1'}, {id: '/parents/2/children/2'}]")
                .tryToAdjustIds());
        assertFalse(parse("PUT", "/parents/1/children", "{id: '/parents/1/children/1'}").tryToAdjustIds());
        assertFalse(parse("PUT", "/parents/1/children/1", "{id: '/parents/2/children/1'}").tryToAdjustIds());

        assertFalse(parse("POST", "/parents/1/grandchildren",
                "[{id: '/parents/1/children/1/grandchildren/1'}, {id: '/parents/2/children/2/grandchildren/2'}]").tryToAdjustIds());
    }

    @Test
    public void testInvalidRouteParentIds() {
        assertFalse(parse("PUT", "/parents/1/children/1", "{parentId: '/parents/2'}").tryToAdjustIds());
        assertFalse(parse("POST", "/parents/1/children", "{parentId: '/parents/2'}").tryToAdjustIds());
    }
}
