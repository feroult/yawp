package io.yawp.plugin.appengine;

import io.yawp.plugin.DevServerMojo;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.apache.maven.plugin.MojoExecutionException;
import org.mortbay.jetty.webapp.WebAppContext;

import com.google.appengine.tools.development.DevSocketImplFactory;

public class AppengineDevServerHelper {

	private DevServerMojo mojo;

	private String sdkRoot;

	public AppengineDevServerHelper(DevServerMojo mojo) {
		this.mojo = mojo;
		resolveSdkRoot();
	}

	public void configure(WebAppContext webapp) {
		installDevSocketImplFactory();
		addDevServerFilter(webapp);
	}

	private void installDevSocketImplFactory() {
		DevSocketImplFactory.install();
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
		// webapp.addFilter(AppengineDevServerFilter.class, "/*", 1);
	}

	public List<String> getClassPathElements() {
		List<String> elements = new ArrayList<String>();
		//elements.add(sdkRoot + "/lib/shared/servlet-api.jar");
		elements.add(sdkRoot + "/lib/shared/el-api.jar");
		elements.add(sdkRoot + "/lib/shared/jsp-api.jar");
		elements.add(sdkRoot + "/lib/impl/appengine-local-runtime.jar");
		elements.add(sdkRoot + "/lib/shared/appengine-local-runtime-shared.jar");
		elements.add(sdkRoot + "/lib/java-managed-vm/appengine-java-vmruntime/lib/ext/appengine-vm-runtime.jar");
		return elements;
	}

	public String getSdkRoot() {
		return sdkRoot;
	}
}
