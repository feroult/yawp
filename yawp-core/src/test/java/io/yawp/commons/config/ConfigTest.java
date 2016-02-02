package io.yawp.commons.config;

import org.junit.Test;

import static org.junit.Assert.assertEquals;

public class ConfigTest {

    @Test
    public void testConfigPackage() {
        Config config = Config.load();
        assertEquals("io.yawp", config.getDefaultFeatures().getPackagePrefix());
        assertEquals("default", config.getDefaultRepository().getFeatures());
        assertEquals("io.yawp", config.getDefaultRepositoryFeatures().getPackagePrefix());
    }
}
