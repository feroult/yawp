package io.yawp.driver.api;

import io.yawp.repository.Repository;

import java.util.ServiceLoader;

public class TestHelperFactory {

	public static TestHelper getHelper(Repository r) {
		TestHelper helper = lookup();
		helper.init(r);
		return helper;
	}

	private static TestHelper lookup() {
		ServiceLoader<TestHelper> helpers = ServiceLoader.load(TestHelper.class);
		for (TestHelper helper : helpers) {
			return helper;
		}
		throw new RuntimeException("No yawp test helper driver found!");
	}

}
