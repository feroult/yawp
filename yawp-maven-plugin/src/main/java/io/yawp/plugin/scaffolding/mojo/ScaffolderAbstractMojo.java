package io.yawp.plugin.scaffolding.mojo;

import org.apache.maven.plugins.annotations.Parameter;

import io.yawp.plugin.base.PluginAbstractMojo;

public abstract class ScaffolderAbstractMojo extends PluginAbstractMojo {

    @Parameter(property = "yawp.package", required = true)
    protected String yawpPackage;

    @Parameter(property = "model", required = true)
    protected String model;

}
