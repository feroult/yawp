package io.yawp.plugin;

import java.util.EnumSet;

import javax.servlet.DispatcherType;

import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugins.annotations.Mojo;
import org.apache.maven.plugins.annotations.Parameter;
import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.servlet.FilterHolder;
import org.eclipse.jetty.servlet.Holder;
import org.eclipse.jetty.webapp.WebAppContext;

@Mojo(name = "devserver")
public class DevServerMojo extends AbstractMojo {

	@Parameter(defaultValue = "${basedir}/src/main/webapp")
	private String appDir;

	public void execute() throws MojoExecutionException {
		start(getWebAppRoot(), getPort());
		getLog().info("YAWP! devserver");
	}

	private void start(String rootPath, int port) {
		getLog().info("Starting webserver at: " + rootPath);

		Server server = new Server(port);
		WebAppContext webapp = new WebAppContext(rootPath, "");
		webapp.setDefaultsDescriptor(null);
		server.setHandler(webapp);

		webapp.addFilter(DriverFilter.class, "/*", EnumSet.allOf(DispatcherType.class));

		try {
			server.start();
			server.join();
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	private int getPort() {
		return 8080;
	}

	private String getWebAppRoot() {
		return appDir;
	}
}