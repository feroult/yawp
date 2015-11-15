package io.yawp.plugin.scaffolding.mojo;

import io.yawp.plugin.scaffolding.ActionScaffolder;

import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugins.annotations.Mojo;
import org.apache.maven.plugins.annotations.Parameter;

@Mojo(name = "action")
public class ActionMojo extends ScaffolderAbstractMojo {

	@Parameter(property = "name", required = true)
	protected String name;

	public void execute() throws MojoExecutionException {
		ActionScaffolder scaffolder = new ActionScaffolder(getLog(), yawpPackage, model, name);
		scaffolder.createTo(baseDir);
	}

}