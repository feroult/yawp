package io.yawp.commons.config;

import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

public class ConfigTest {

    @Test
    public void testConfigPackage() {
        Config config = Config.load();
        assertEquals("io.yawp", config.getDefaultFeatures().getPackagePrefix());
        assertTrue(config.getDefaultFeatures().isEnableHooks());
    }
}
