package io.yawp.repository.driver.api;

import io.yawp.repository.Repository;
import io.yawp.repository.driver.appengine.AppengineRepositoryDriver;

public class RepositoryDriverFactory {

	public static RepositoryDriver getRepositoryDriver(Repository r) {
		AppengineRepositoryDriver driver = new AppengineRepositoryDriver();
		driver.init(r);
		return driver;
	}

}
