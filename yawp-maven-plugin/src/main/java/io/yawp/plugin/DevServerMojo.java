package io.yawp.plugin;

import io.yawp.plugin.appengine.AppengineWebAppContextHelper;

import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugins.annotations.Mojo;
import org.apache.maven.plugins.annotations.Parameter;
import org.apache.maven.plugins.annotations.ResolutionScope;
import org.mortbay.jetty.Server;

@Mojo(name = "devserver", requiresDependencyResolution = ResolutionScope.COMPILE_PLUS_RUNTIME)
public class DevServerMojo extends PluginAbstractMojo {

	@Parameter(defaultValue = "${basedir}/src/main/webapp")
	protected String appDir;

	@Parameter(defaultValue = "8080")
	private String port;

	@Parameter(defaultValue = "3")
	private String fullScanSeconds;

	@Parameter(defaultValue = "${basedir}/target/classes")
	protected String hotDeployDir;

	private WebAppContextHelper helper;

	public void execute() throws MojoExecutionException {
		initHelper();
		startServer();
	}

	private void initHelper() {
		// TODO: check if its helper environment
		this.helper = new AppengineWebAppContextHelper(this);
	}

	private void startServer() {
		getLog().info("Starting webserver at: " + appDir);

		Server server = new Server(getPort());
		server.setHandler(helper.createWebApp());

		try {
			server.start();
			server.join();
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	public String getAppDir() {
		return appDir;
	}

	public String getHotDeployDir() {
		return hotDeployDir;
	}

	public int getPort() {
		return Integer.valueOf(port);
	}

	public int getFullScanSeconds() {
		return Integer.valueOf(fullScanSeconds);
	}
}