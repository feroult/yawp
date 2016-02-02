package io.yawp.commons.config;

public class RepositoryConfig {

    private String features;

    private boolean enableHooks;

    public String getFeatures() {
        return features;
    }

    public void setFeatures(String features) {
        this.features = features;
    }

    public boolean isEnableHooks() {
        return enableHooks;
    }

    public void setEnableHooks(boolean enableHooks) {
        this.enableHooks = enableHooks;
    }
}
