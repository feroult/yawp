package io.yawp.plugin.mojos.scaffolding.mojo;

import io.yawp.commons.config.ConfigFile;
import io.yawp.plugin.mojos.base.PluginAbstractMojo;
import org.apache.maven.plugins.annotations.Parameter;

public abstract class ScaffolderAbstractMojo extends PluginAbstractMojo {

    @Parameter(property = "lang", defaultValue = "java")
    protected String lang;

    @Parameter(property = "model", required = true)
    protected String model;


    protected String getYawpPackage() {
        return loadYawpPackageFromConfig();
    }

    private String loadYawpPackageFromConfig() {
        configureRuntimeClassLoader();
        ConfigFile configFile = ConfigFile.load();
        return configFile.getConfig().getPackages();
    }

}

