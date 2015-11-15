package io.yawp.plugin.scaffolding.mojo;

import io.yawp.plugin.scaffolding.HookScaffolder;

import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugins.annotations.Mojo;
import org.apache.maven.plugins.annotations.Parameter;

@Mojo(name = "hook")
public class HookMojo extends ScaffolderAbstractMojo {

	@Parameter(property = "name", required = true)
	protected String name;

	public void execute() throws MojoExecutionException {
		HookScaffolder scaffolder = new HookScaffolder(getLog(), yawpPackage, model, name);
		scaffolder.createTo(baseDir);
	}

}