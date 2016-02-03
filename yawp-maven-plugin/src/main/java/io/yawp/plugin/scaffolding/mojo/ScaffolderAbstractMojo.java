package io.yawp.plugin.scaffolding.mojo;

import io.yawp.commons.config.Config;
import org.apache.maven.plugins.annotations.Parameter;

import io.yawp.plugin.base.PluginAbstractMojo;

public abstract class ScaffolderAbstractMojo extends PluginAbstractMojo {

    /**
     * @deprecated it will be removed in 2.0
     */
    @Deprecated
    @Parameter(property = "yawp.package")
    private String yawpPackage;

    @Parameter(property = "model", required = true)
    protected String model;

    protected String getYawpPackage() {
        if(yawpPackage == null) {
            loadYawpPackageFromConfig();
        }
        return yawpPackage;
    }

    private void loadYawpPackageFromConfig() {
        configureRuntimeClassLoader();
        Config config = Config.load();
        this.yawpPackage = config.getDefaultFeatures().getPackagePrefix();
    }

}

