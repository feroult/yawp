package io.yawp.driver.api;

import io.yawp.repository.Repository;

import java.util.ServiceLoader;

public class TestHelperFactory {

	public static TestHelper getTestHelper(Repository r) {
		TestHelper helper = lookup(TestHelper.class);

		if (r != null) {
			helper.init(r);
		}
		return helper;
	}

	public static TestHelper getTestHelper() {
		return getTestHelper(null);
	}

	private static <T> T lookup(Class<T> clazz) {
		ServiceLoader<T> helpers = ServiceLoader.load(clazz);
		for (T helper : helpers) {
			return helper;
		}
		throw new RuntimeException(String.format("No yawp %s helper found!", TestHelper.class.getSimpleName()));
	}
}
