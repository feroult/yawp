package io.yawp.commons.utils;

import io.yawp.driver.api.DriverFactory;
import io.yawp.driver.api.EnvironmentDriver;

public class Environment {

	public static boolean isProduction() {
		return environment().isProduction();
	}

	public static boolean isDevelopment() {
		return environment().isDevelopment();
	}

	public static boolean isTest() {
		return environment().isTest();
	}

	private static EnvironmentDriver environment() {
		return DriverFactory.getDriver().helpers().environment();
	}
}