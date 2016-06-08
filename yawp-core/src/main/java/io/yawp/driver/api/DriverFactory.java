package io.yawp.driver.api;

import io.yawp.commons.utils.ServiceLookup;
import io.yawp.repository.Repository;

public class DriverFactory {

    private DriverFactory() {}

    public static Driver getDriver(Repository r) {
        Driver driver = lookup();
        driver.init(r);
        return driver;
    }

    public static Driver getDriver() {
        Driver driver = lookup();
        return driver;
    }

    private static Driver lookup() {
        return ServiceLookup.lookup(Driver.class);
    }

}
