package io.yawp.driver.api;

import java.util.ServiceLoader;

public class TestHelperFactory {

	public static TestHelper getHelper() {
		return lookup();
	}

	private static TestHelper lookup() {
		ServiceLoader<TestHelper> helpers = ServiceLoader.load(TestHelper.class);
		for (TestHelper helper : helpers) {
			return helper;
		}
		throw new RuntimeException("No yawp test helper driver found!");
	}

}
