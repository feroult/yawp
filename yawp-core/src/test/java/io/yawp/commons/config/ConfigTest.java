package io.yawp.commons.config;

import org.junit.Test;

import static org.junit.Assert.assertTrue;

public class ConfigTest {

	@Test
	public void testConfigPackage() {
		ConfigFile configFile = ConfigFile.load();
		assertTrue(configFile.getConfig().getPackages().startsWith("io.yawp"));
	}
}
