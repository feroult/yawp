package io.yawp.commons.utils;

import io.yawp.driver.api.DriverFactory;
import io.yawp.driver.api.EnvironmentDriver;

public class Environment {

    public static final String DEFAULT_DEVELOPMENT_ENVIRONMENT = "development";

    public static final String DEFAULT_TEST_ENVIRONMENT = "test";

    private static final String YAWP_ENV = "yawp.env";

    private static final String YAWP_BASE_DIR = "yawp.dir";

    public static boolean isProduction() {
        if (get() != null) {
            return getOrDefault().equals("production");
        }
        return environment().isProduction();
    }

    public static boolean isDevelopment() {
        if (get() != null) {
            return getOrDefault().equals("development");
        }
        return environment().isDevelopment();
    }

    public static boolean isTest() {
        if (get() != null) {
            return getOrDefault().equals("test");
        }
        return environment().isTest();
    }

    private static EnvironmentDriver environment() {
        return DriverFactory.getDriver().environment();
    }

    public static void set(String env) {
        System.setProperty(YAWP_ENV, env);
    }

    public static String get() {
        return System.getProperty(YAWP_ENV);
    }

    public static String getOrDefault() {
        String env = System.getProperty(YAWP_ENV);
        if (env != null) {
            return env;
        }
        return DEFAULT_DEVELOPMENT_ENVIRONMENT;
    }

    public static void setBaseDir(String baseDir) {
        System.setProperty(YAWP_BASE_DIR, baseDir);
    }

    public static String getBaseDir() {
        return System.getProperty(YAWP_BASE_DIR);
    }

}