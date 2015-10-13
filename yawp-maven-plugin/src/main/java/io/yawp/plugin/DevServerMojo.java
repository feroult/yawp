package io.yawp.plugin;

import io.yawp.plugin.appengine.AppengineDevServer;
import io.yawp.plugin.appengine.ClassLoaderPatch;

import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugins.annotations.Mojo;
import org.apache.maven.plugins.annotations.Parameter;
import org.apache.maven.plugins.annotations.ResolutionScope;
import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.webapp.WebAppContext;

@Mojo(name = "devserver", requiresDependencyResolution = ResolutionScope.COMPILE_PLUS_RUNTIME)
public class DevServerMojo extends PluginAbstractMojo {

	@Parameter(defaultValue = "${basedir}/src/main/webapp")
	private String appDir;

	@Parameter(defaultValue = "8080")
	private String port;

	private AppengineDevServer appengine;

	public void execute() throws MojoExecutionException {
		init();
		start();
	}

	private void init() {
		this.appengine = new AppengineDevServer(this);

		ClassLoaderPatch.addFile(appengine.getSdkRoot() + "/lib/shared/servlet-api.jar");

		// ClassLoaderPatch.addFiles(appengine.getClassPathElements());
		// ClassLoaderPatch
		// .addFile("/Users/fernando/.m2/repository/com/google/appengine/appengine-api-1.0-sdk/1.9.25/appengine-api-1.0-sdk-1.9.25.jar");
		// ClassLoaderPatch
		// .addFile("/Users/fernando/.m2/repository/com/google/appengine/appengine-api-labs/1.9.25/appengine-api-labs-1.9.25.jar");
		// ClassLoaderPatch
		// .addFile("/Users/fernando/.m2/repository/com/google/appengine/appengine-testing/1.9.25/appengine-testing-1.9.25.jar");
		//
		// ClassLoaderPatch
		// .addFile("/Users/fernando/.m2/repository/com/google/appengine/appengine-api-stubs/1.9.25/appengine-api-stubs-1.9.25.jar");
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

		webapp.setDefaultsDescriptor(null);

		webapp.setParentLoaderPriority(true);
		webapp.setClassLoader(createClassLoader(appengine.getClassPathElements()));
		// createClassLoader(appengine.getClassPathElements());

		// TODO: check if its appengine environment
		appengine.configure(webapp);

		return webapp;
	}

	private int getPort() {
		return Integer.valueOf(port);
	}

}