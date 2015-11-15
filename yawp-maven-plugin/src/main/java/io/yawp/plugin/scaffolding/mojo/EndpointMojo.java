package io.yawp.plugin.scaffolding.mojo;

import io.yawp.plugin.scaffolding.EndpointScaffolder;

import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugins.annotations.Mojo;

@Mojo(name = "endpoint")
public class EndpointMojo extends ScaffolderAbstractMojo {

	public void execute() throws MojoExecutionException {
		getLog().info("Scaffolding to: " + baseDir);
		EndpointScaffolder scaffolder = new EndpointScaffolder(yawpPackage, model);
		scaffolder.createTo(baseDir);
	}

}