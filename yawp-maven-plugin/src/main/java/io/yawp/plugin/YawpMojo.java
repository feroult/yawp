package io.yawp.plugin;

import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugins.annotations.Mojo;

/**
 * @goal yawp
 */
@Mojo(name = "xpto")
public class YawpMojo extends AbstractMojo {

	public void execute() throws MojoExecutionException {

		getLog().info("Hi there!!!");
	}
}