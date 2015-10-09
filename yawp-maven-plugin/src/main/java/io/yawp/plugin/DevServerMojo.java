package io.yawp.plugin;

import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugins.annotations.Mojo;
import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.webapp.WebAppContext;

@Mojo(name = "devserver")
public class DevServerMojo extends AbstractMojo {

	public void execute() throws MojoExecutionException {
		start(getWebAppRoot(), getPort());
		getLog().info("YAWP! devserver");
	}

	private void start(String rootPath, int port) {
		Server server = new Server(port);
		WebAppContext webapp = new WebAppContext(rootPath, "");
		webapp.setDefaultsDescriptor(null);
		server.setHandler(webapp);

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
		return "/Users/fernando/dev/yawp/yawp-appengine/src/test/webapp";
	}
}