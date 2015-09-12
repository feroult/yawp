package io.yawp.repository.tools;

import io.yawp.driver.api.DriverFactory;
import io.yawp.driver.api.HelpersDriver;
import io.yawp.driver.api.NamespaceDriver;

public class DeleteAll {

	public static void now(String ns) {
		namespace().set(ns);
		now();
	}

	public static void now() {
		helpers().deleteAll();
	}

	private static NamespaceDriver namespace() {
		return DriverFactory.getDriver().namespace();
	}

	private static HelpersDriver helpers() {
		return DriverFactory.getDriver().helpers();
	}

}
