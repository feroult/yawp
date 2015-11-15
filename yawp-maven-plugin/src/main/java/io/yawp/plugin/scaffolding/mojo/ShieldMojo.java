package io.yawp.plugin.scaffolding.mojo;

import io.yawp.plugin.scaffolding.ShieldScaffolder;

import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugins.annotations.Mojo;

@Mojo(name = "shield")
public class ShieldMojo extends ScaffolderAbstractMojo {

	public void execute() throws MojoExecutionException {
		getLog().info("Scaffolding to: " + baseDir);
		ShieldScaffolder scaffolder = new ShieldScaffolder(yawpPackage, model);
		scaffolder.createTo(baseDir);
	}

}