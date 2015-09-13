package io.yawp.driver.api;

import io.yawp.repository.Repository;

import java.util.ServiceLoader;

public class DriverFactory {

	public static Driver getDriver(Repository r) {
		Driver driver = lookup();
		driver.init(r);
		return driver;
	}

	public static Driver getDriver() {
		Driver driver = lookup();
		return driver;
	}

	public static Driver lookup() {
		ServiceLoader<Driver> drivers = ServiceLoader.load(Driver.class);
		for (Driver driver : drivers) {
			return driver;
		}
		throw new RuntimeException("No yawp driver found!");
	}

}
