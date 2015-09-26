package io.yawp.driver.postgresql;

import io.yawp.driver.api.EnvironmentDriver;

public class PGEnvironmentDriver implements EnvironmentDriver {

	@Override
	public boolean isProduction() {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean isDevelopment() {
		// TODO Auto-generated method stub
		return true;
	}

	@Override
	public boolean isTest() {
		// TODO Auto-generated method stub
		return true;
	}

	@Override
	public boolean isAdmin() {
		// TODO Auto-generated method stub
		return true;
	}

}
