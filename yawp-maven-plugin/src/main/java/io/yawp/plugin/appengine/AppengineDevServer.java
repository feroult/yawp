package io.yawp.plugin.appengine;

import io.yawp.plugin.DevServerMojo;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.EnumSet;
import java.util.List;

import javax.servlet.Servlet;

import org.apache.maven.plugin.MojoExecutionException;
import org.eclipse.jetty.server.DispatcherType;
import org.eclipse.jetty.webapp.WebAppContext;

public class AppengineDevServer {

	private DevServerMojo mojo;

	private String sdkRoot;

	public AppengineDevServer(DevServerMojo mojo) {
		this.mojo = mojo;
		resolveSdkRoot();
	}

	public void configure(WebAppContext webapp) {
		addDevServerFilter(webapp);
		addAppengineServlets(webapp);
	}

	private void resolveSdkRoot() {
		try {
			@SuppressWarnings("unchecked")
			File sdkBaseDir = SdkResolver.getSdk(mojo.getProject(), mojo.getRepoSystem(), mojo.getRepoSession(), mojo.getPluginRepos(),
					mojo.getProjectRepos());

			System.setProperty("appengine.sdk.root", sdkBaseDir.getCanonicalPath());

			this.sdkRoot = sdkBaseDir.getAbsolutePath();
		} catch (MojoExecutionException | IOException e) {
			throw new RuntimeException(e);
		}
	}

	private void addDevServerFilter(WebAppContext webapp) {
		// TODO: add support do admin/login appengine servlets
		webapp.addFilter(AppengineDevServerFilter.class, "/*", EnumSet.allOf(DispatcherType.class));
		// webapp.addFilter(AppengineDevServerFilter.class, "/*", 1);
	}

	private void addAppengineServlets(WebAppContext webapp) {
		//webapp.addServlet(servletClass("com.google.apphosting.utils.servlet.DatastoreViewerServlet"), "/_ah/admin");
	}

	@SuppressWarnings("unchecked")
	private Class<? extends Servlet> servletClass(String className) {
		try {
			return (Class<? extends Servlet>) Class.forName(className);
		} catch (ClassNotFoundException e) {
			throw new RuntimeException(e);
		}
	}

	public List<String> getClassPathElements() {
		List<String> elements = new ArrayList<String>();
		// elements.add("/Users/fernando/.m2/repository/javax/servlet/servlet-api/2.5/servlet-api-2.5.jar");
		// elements.add("/Users/fernando/.m2/repository/org/eclipse/jetty/orbit/javax.servlet/2.5.0.v201103041518/javax.servlet-2.5.0.v201103041518.jar");
		// elements.add("/Users/fernando/.m2/repository/org/eclipse/jetty/orbit/javax.servlet/3.0.0.v201112011016/javax.servlet-3.0.0.v201112011016.jar");
		//elements.add(sdkRoot + "/lib/shared/servlet-api.jar");
		//elements.add(sdkRoot + "/lib/shared/appengine-local-runtime-shared.jar");
		return elements;
	}
}
