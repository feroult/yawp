package io.yawp.repository.features;

import org.junit.Test;

import static org.junit.Assert.assertTrue;

public class FeaturesConfigTest {

    @Test
    public void testSimple() {
        FeaturesConfig config = new FeaturesConfig("yawp.features.test.yml");

        Features features = config.load();

        assertTrue(true);
    }

}
