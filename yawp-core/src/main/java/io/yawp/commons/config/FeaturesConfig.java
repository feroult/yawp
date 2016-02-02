package io.yawp.commons.config;

public class FeaturesConfig {

    private String packagePrefix;

    private boolean enableHooks;

    public String getPackagePrefix() {
        return packagePrefix;
    }

    public void setPackagePrefix(String packagePrefix) {
        this.packagePrefix = packagePrefix;
    }

    public boolean isEnableHooks() {
        return enableHooks;
    }

    public void setEnableHooks(boolean enableHooks) {
        this.enableHooks = enableHooks;
    }
}
