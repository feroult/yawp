package io.yawp.driver.api;

public interface EnvironmentDriver {

    public boolean isProduction();

    public boolean isDevelopment();

    public boolean isTest();

    public boolean isAdmin();
}
