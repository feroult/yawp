package io.yawp.driver.mock;

import io.yawp.driver.api.TestHelper;

public class MockTestHelper implements TestHelper {

	@Override
	public void setUp() {
		MockStore.clear();
	}

	@Override
	public void tearDown() {
	}

}
