package io.yawp.driver.api;

import io.yawp.driver.appengine.AppengineDriver;
import io.yawp.repository.Repository;

public class DriverFactory {

	public static Driver getRepositoryDriver(Repository r) {
		AppengineDriver driver = new AppengineDriver();
		driver.init(r);
		return driver;
	}

	public static Driver getDriver() {
		AppengineDriver driver = new AppengineDriver();
		return driver;
	}

}
