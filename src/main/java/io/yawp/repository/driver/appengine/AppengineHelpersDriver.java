package io.yawp.repository.driver.appengine;

import io.yawp.repository.driver.api.HelpersDriver;
import io.yawp.repository.driver.api.TestHelperDriver;

public class AppengineHelpersDriver implements HelpersDriver {

	@Override
	public TestHelperDriver tests() {
		return new AppengineTestHelperDriver();
	}

}
