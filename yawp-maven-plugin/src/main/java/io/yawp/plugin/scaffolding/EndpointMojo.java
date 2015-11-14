package io.yawp.plugin.scaffolding;

import io.yawp.plugin.PluginAbstractMojo;

import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugins.annotations.Mojo;
import org.apache.maven.plugins.annotations.Parameter;

@Mojo(name = "endpoint")
public class EndpointMojo extends PluginAbstractMojo {

	@Parameter(property = "yawp.package")
	protected String yawpPackage;

	@Parameter(property = "model")
	protected String model;

	public void execute() throws MojoExecutionException {
		getLog().info("scaffolding to: " + baseDir);
		EndpointScaffolder scaffolder = new EndpointScaffolder(yawpPackage, model);
		scaffolder.createTo(baseDir);
	}

}