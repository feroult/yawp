package io.yawp.driver.appengine;

import io.yawp.driver.api.EnvironmentDriver;
import io.yawp.driver.api.HelpersDriver;
import io.yawp.driver.api.TestHelperDriver;

public class AppengineHelpersDriver implements HelpersDriver {

	@Override
	public TestHelperDriver tests() {
		return new AppengineTestHelperDriver();
	}

	@Override
	public EnvironmentDriver environment() {
		return new AppengineEnvironmentDriver();
	}

}
