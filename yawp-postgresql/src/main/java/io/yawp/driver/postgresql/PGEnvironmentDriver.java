package io.yawp.driver.postgresql;

import io.yawp.commons.utils.Environment;
import io.yawp.driver.api.EnvironmentDriver;

public class PGEnvironmentDriver implements EnvironmentDriver {

    @Override
    public boolean isProduction() {
        return envContains("production");
    }

    @Override
    public boolean isDevelopment() {
        return envContains("development");
    }

    @Override
    public boolean isTest() {
        return envContains("test");
    }

    @Override
    public boolean isAdmin() {
        return false;
    }

    private boolean envContains(String env) {
        return Environment.getOrDefault().toLowerCase().contains(env);
    }

}
