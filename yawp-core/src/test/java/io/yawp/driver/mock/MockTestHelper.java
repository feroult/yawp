package io.yawp.driver.mock;

import io.yawp.driver.api.testing.TestHelper;
import io.yawp.repository.Repository;

public class MockTestHelper implements TestHelper {

	@Override
	public void init(Repository r) {
	}

	@Override
	public void setUp() {
		MockStore.clear();
	}

	@Override
	public void tearDown() {
	}

}
