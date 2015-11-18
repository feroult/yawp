package io.yawp.driver.api;

public interface EnvironmentDriver {

    boolean isProduction();

    boolean isDevelopment();

    boolean isTest();

    boolean isAdmin();
}
