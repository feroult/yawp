package io.yawp.commons.utils;

import io.yawp.driver.api.DriverFactory;
import io.yawp.driver.api.EnvironmentDriver;

public class Environment {

    private static final String YAWP_ENV = "yawp.env";

    private static final String YAWP_BASE_DIR = "yawp.dir";


    public static boolean isProduction() {
        if (get() != null) {
            return get().equals("production");
        }
        return environment().isProduction();
    }

    public static boolean isDevelopment() {
        if (get() != null) {
            return get().equals("development");
        }
        return environment().isDevelopment();
    }

    public static boolean isTest() {
        if (get() != null) {
            return get().equals("test");
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

    public static void setBaseDir(String appDir) {
        System.setProperty(YAWP_BASE_DIR, appDir);
    }

    public static String getBaseDir() {
        return System.getProperty(YAWP_BASE_DIR);
    }

}