package io.yawp.repository.features;

import org.junit.Test;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

public class FeaturesTest {

    @Test
    public void testValidEndpointPaths() {
        Features features = new Features();

        assertTrue(features.isValidEndpointPath("/parents"));
        assertTrue(features.isValidEndpointPath("/par_ents"));
        assertTrue(features.isValidEndpointPath("/par-ents"));
    }

    @Test
    public void testInvalidEndpointPaths() {
        Features features = new Features();

        assertFalse(features.isValidEndpointPath("parents"));
        assertFalse(features.isValidEndpointPath("/123parents"));
        assertFalse(features.isValidEndpointPath("/par?ents"));
    }

}
