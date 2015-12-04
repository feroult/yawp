package io.yawp.repository;

import org.junit.Test;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

public class RepositoryFeaturesTest {

    @Test
    public void testValidEndpointPaths() {
        RepositoryFeatures features = new RepositoryFeatures();

        assertTrue(features.isValidEndpointPath("/parents"));
        assertTrue(features.isValidEndpointPath("/par_ents"));
        assertTrue(features.isValidEndpointPath("/par-ents"));
    }

    @Test
    public void testInvalidEndpointPaths() {
        RepositoryFeatures features = new RepositoryFeatures();

        assertFalse(features.isValidEndpointPath("parents"));
        assertFalse(features.isValidEndpointPath("/123parents"));
        assertFalse(features.isValidEndpointPath("/par?ents"));
    }

}
