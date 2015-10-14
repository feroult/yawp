package io.yawp.plugin;

import io.yawp.plugin.appengine.AppengineWebAppContextHelper;

import java.io.IOException;

import org.apache.maven.model.Dependency;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugins.annotations.Mojo;
import org.apache.maven.plugins.annotations.Parameter;
import org.apache.maven.plugins.annotations.ResolutionScope;
import org.mortbay.jetty.Server;
import org.mortbay.jetty.nio.SelectChannelConnector;

@Mojo(name = "devserver", requiresDependencyResolution = ResolutionScope.COMPILE_PLUS_RUNTIME)
public class DevServerMojo extends PluginAbstractMojo {

	@Parameter(property = "yawp.appDir", defaultValue = "${basedir}/src/main/webapp")
	protected String appDir;

	@Parameter(property = "yawp.address", defaultValue = "0.0.0.0")
	private String address;

	@Parameter(property = "yawp.port", defaultValue = "8080")
	private String port;

	@Parameter(property = "yawp.fullScanSeconds", defaultValue = "3")
	private String fullScanSeconds;

	@Parameter(property = "yawp.hotDeployDir", defaultValue = "${basedir}/target/classes")
	protected String hotDeployDir;

	private WebAppContextHelper helper;

	public void execute() throws MojoExecutionException {
		initHelper();
		startServer();
	}

	private void initHelper() {
		if (isYawpAppengine()) {
			this.helper = new AppengineWebAppContextHelper(this);
		} else {
			this.helper = new WebAppContextHelper(this);
		}
	}

	private void startServer() {
		getLog().info("Starting webserver at: " + appDir);

		try {
			Server server = new Server();
			server.addConnector(createConnector());
			server.setHandler(helper.createWebApp());

			server.start();
			server.join();
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	private SelectChannelConnector createConnector() throws IOException {
		SelectChannelConnector connector = new SelectChannelConnector();
		connector.setHost(getAddress());
		connector.setPort(getPort());
		connector.setSoLingerTime(0);
		connector.open();
		return connector;
	}

	private boolean isYawpAppengine() {
		for (Dependency dependency : project.getDependencies()) {
			if (dependency.getGroupId().equals("io.yawp") && dependency.getArtifactId().equals("yawp")) {
				return true;
			}
		}
		return false;
	}

	public String getAppDir() {
		return appDir;
	}

	public String getHotDeployDir() {
		return hotDeployDir;
	}

	public String getAddress() {
		return address;
	}

	public int getPort() {
		return Integer.valueOf(port);
	}

	public int getFullScanSeconds() {
		return Integer.valueOf(fullScanSeconds);
	}
}