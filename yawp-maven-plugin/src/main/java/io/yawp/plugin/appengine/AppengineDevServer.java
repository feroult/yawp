package io.yawp.plugin.appengine;

import io.yawp.plugin.DevServerMojo;

import java.io.File;
import java.io.IOException;
import java.util.EnumSet;

import javax.servlet.DispatcherType;

import org.apache.maven.plugin.MojoExecutionException;
import org.eclipse.jetty.webapp.WebAppContext;

public class AppengineDevServer {

	private DevServerMojo mojo;

	private String sdkRoot;

	public AppengineDevServer(DevServerMojo mojo) {
		this.mojo = mojo;
		resolveSdkRoot();
	}

	public void configure(WebAppContext webapp) {
		configureClassLoader(webapp);
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

	private void configureClassLoader(WebAppContext webapp) {
		ClassLoaderPatch.addFile(sdkRoot + "/lib/shared/appengine-local-runtime-shared.jar");
	}

	private void addDevServerFilter(WebAppContext webapp) {
		// TODO: add support do admin/login appengine servlets
		webapp.addFilter(AppengineDevServerFilter.class, "/*", EnumSet.allOf(DispatcherType.class));
	}

	private void addAppengineServlets(WebAppContext webapp) {
		// TODO Auto-generated method stub

	}

}
