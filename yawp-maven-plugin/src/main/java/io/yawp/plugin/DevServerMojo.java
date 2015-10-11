package io.yawp.plugin;

import java.io.File;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.ArrayList;
import java.util.EnumSet;
import java.util.List;

import javax.servlet.DispatcherType;

import org.apache.maven.artifact.DependencyResolutionRequiredException;
import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugins.annotations.Mojo;
import org.apache.maven.plugins.annotations.Parameter;
import org.apache.maven.plugins.annotations.ResolutionScope;
import org.apache.maven.project.MavenProject;
import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.webapp.WebAppContext;

@Mojo(name = "devserver", requiresDependencyResolution = ResolutionScope.COMPILE_PLUS_RUNTIME)
public class DevServerMojo extends AbstractMojo {

	@Parameter(defaultValue = "${project}")
	private MavenProject project;

	@Parameter(defaultValue = "${basedir}/src/main/webapp")
	private String appDir;

	public void execute() throws MojoExecutionException {
		start(getWebAppRoot(), getPort());
	}

	private void start(String rootPath, int port) {
		getLog().info("Starting webserver at: " + rootPath);

		Server server = new Server(port);
		server.setHandler(createWebApp(rootPath));

		try {
			server.start();
			server.join();
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	private WebAppContext createWebApp(String rootPath) {
		WebAppContext webapp = new WebAppContext(rootPath, "");

		webapp.setClassLoader(createClassLoader());

		webapp.setDefaultsDescriptor(null);
		addDevServerFilter(webapp);
		return webapp;
	}

	private URLClassLoader createClassLoader() {
		try {
			List<URL> runtimeUrls = new ArrayList<URL>();
			addURLs(project.getCompileClasspathElements(), runtimeUrls);
			addURLs(project.getRuntimeClasspathElements(), runtimeUrls);
			URLClassLoader newLoader = new URLClassLoader(runtimeUrls.toArray(new URL[] {}), Thread.currentThread().getContextClassLoader());
			return newLoader;
		} catch (DependencyResolutionRequiredException | MalformedURLException e) {
			throw new RuntimeException(e);
		}
	}

	private void addURLs(List<String> runtimeClasspathElements, List<URL> runtimeUrls) throws MalformedURLException {
		List<String> elements = runtimeClasspathElements;
		for (int i = 0; i < elements.size(); i++) {
			String element = (String) elements.get(i);
			runtimeUrls.add(new File(element).toURI().toURL());
			System.out.println("file: " + element);
		}
	}

	private void addDevServerFilter(WebAppContext webapp) {
		webapp.addFilter(DevServerFilterProxy.class, "/*", EnumSet.allOf(DispatcherType.class));
	}

	private int getPort() {
		return 8080;
	}

	private String getWebAppRoot() {
		return appDir;
	}
}