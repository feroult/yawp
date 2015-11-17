package io.yawp.driver.mock;

import io.yawp.driver.api.EnvironmentDriver;

public class MockEnvironmentDriver implements EnvironmentDriver {

    @Override
    public boolean isProduction() {
        return false;
    }

    @Override
    public boolean isDevelopment() {
        return true;
    }

    @Override
    public boolean isTest() {
        return true;
    }

    @Override
    public boolean isAdmin() {
        return true;
    }

}
