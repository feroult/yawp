package io.yawp.plugin.mojos.scaffolding.mojo;

import io.yawp.commons.config.ConfigFile;
import io.yawp.plugin.mojos.base.PluginAbstractMojo;
import org.apache.maven.plugins.annotations.Parameter;

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
        if (yawpPackage == null) {
            loadYawpPackageFromConfig();
        }
        return yawpPackage;
    }

    private void loadYawpPackageFromConfig() {
        configureRuntimeClassLoader();
        ConfigFile configFile = ConfigFile.load();
        this.yawpPackage = configFile.getConfig().getPackages();
    }

}

