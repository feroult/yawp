package io.yawp.commons.config;

import org.junit.Test;

import static org.junit.Assert.assertEquals;

public class ConfigTest {

    @Test
    public void testConfigPackage() {
        ConfigFile configFile = ConfigFile.load();
        assertEquals("io.yawp", configFile.getConfig().getPackages());
    }
}
