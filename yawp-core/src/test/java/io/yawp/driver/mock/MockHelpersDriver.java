package io.yawp.driver.mock;

import io.yawp.driver.api.HelpersDriver;

import javax.servlet.Filter;

public class MockHelpersDriver implements HelpersDriver {

	@Override
	public void deleteAll() {
		MockStore.clear();
	}

	@Override
	public Filter getDevServerFilter() {
		return null;
	}

}
