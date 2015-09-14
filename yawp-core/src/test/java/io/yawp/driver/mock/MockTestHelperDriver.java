package io.yawp.driver.mock;

import io.yawp.driver.api.TestHelperDriver;

public class MockTestHelperDriver implements TestHelperDriver {

	@Override
	public void setUp() {
		MockStore.clear();
	}

	@Override
	public void tearDown() {
	}

}
