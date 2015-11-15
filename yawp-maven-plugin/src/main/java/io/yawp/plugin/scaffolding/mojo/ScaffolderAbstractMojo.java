package io.yawp.plugin.scaffolding.mojo;

import org.apache.maven.plugins.annotations.Parameter;

import io.yawp.plugin.PluginAbstractMojo;

public abstract class ScaffolderAbstractMojo extends PluginAbstractMojo {

	@Parameter(property = "yawp.package")
	protected String yawpPackage;
	@Parameter(property = "model")
	protected String model;

}
