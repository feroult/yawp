package io.yawp.driver.postgresql.configuration;

public class Configuration {

    private String resourceUri;
    private String env;
    private String DS;

    public Configuration(String resourceUri) {
        this.resourceUri = resourceUri;
        this.env = "test";
    }

    public String getEnv() {
        return env;
    }

    public String getDS() {
        return DS;
    }
}
