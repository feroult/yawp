package io.yawp.plugin;

import io.yawp.plugin.appengine.AppengineDevServerHelper;

import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugins.annotations.Mojo;
import org.apache.maven.plugins.annotations.Parameter;
import org.apache.maven.plugins.annotations.ResolutionScope;
import org.mortbay.jetty.Server;
import org.mortbay.jetty.webapp.WebAppContext;

@Mojo(name = "devserver", requiresDependencyResolution = ResolutionScope.COMPILE_PLUS_RUNTIME)
public class DevServerMojo extends PluginAbstractMojo {

	@Parameter(defaultValue = "${basedir}/src/main/webapp")
	private String appDir;

	@Parameter(defaultValue = "8080")
	private String port;

	private AppengineDevServerHelper appengine;

	public void execute() throws MojoExecutionException {
		init();
		start();
	}

	private void init() {
		// TODO: check if its appengine environment
		this.appengine = new AppengineDevServerHelper(this);
	}

	private void start() {
		getLog().info("Starting webserver at: " + appDir);

		Server server = new Server(getPort());
		server.setHandler(createWebApp(appDir));

		// TODO: configure hot deploy

		try {
			server.start();
			server.join();
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	private WebAppContext createWebApp(String rootPath) {
		WebAppContext webapp = new WebAppContext(rootPath, "");

		webapp.setDefaultsDescriptor("/webdefault-appengine.xml");
		webapp.setClassLoader(createClassLoader(appengine.getClassPathElements()));

		appengine.configure(webapp);

		return webapp;
	}

	private int getPort() {
		return Integer.valueOf(port);
	}

}