package io.yawp.plugin.mojos.scaffolding.mojo;

import io.yawp.commons.config.ConfigFile;
import io.yawp.plugin.mojos.base.PluginAbstractMojo;
import org.apache.commons.lang3.StringUtils;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;
import org.apache.maven.plugins.annotations.Parameter;

public abstract class ScaffolderAbstractMojo extends PluginAbstractMojo {

    @Parameter(property = "lang")
    protected String lang;

    @Parameter(property = "model", required = true)
    protected String model;

    @Override
    public void execute() throws MojoExecutionException, MojoFailureException {
        defineLanguage();
        super.execute();
    }

    private void defineLanguage() {
        if (project == null) {
            return;
        }

        if (!StringUtils.isEmpty(lang)) {
            return;
        }

        Object lang = project.getProperties().get("yawp.lang");
        if (lang != null) {
            this.lang = lang.toString();
        } else {
            this.lang = "java";
        }
    }

    protected String getYawpPackage() {
        return loadYawpPackageFromConfig();
    }

    private String loadYawpPackageFromConfig() {
        configureRuntimeClassLoader();
        ConfigFile configFile = ConfigFile.load();
        return configFile.getConfig().getPackages();
    }

}

