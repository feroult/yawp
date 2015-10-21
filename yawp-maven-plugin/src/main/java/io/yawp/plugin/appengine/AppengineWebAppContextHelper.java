package io.yawp.plugin.appengine;

import io.yawp.plugin.DevServerMojo;
import io.yawp.plugin.WebAppContextHelper;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.apache.maven.plugin.MojoExecutionException;
import org.mortbay.jetty.security.Constraint;
import org.mortbay.jetty.security.ConstraintMapping;
import org.mortbay.jetty.security.HashUserRealm;
import org.mortbay.jetty.security.SecurityHandler;
import org.mortbay.jetty.security.UserRealm;

import com.google.appengine.tools.development.DevSocketImplFactory;

public class AppengineWebAppContextHelper extends WebAppContextHelper {

	private String sdkRoot;

	public AppengineWebAppContextHelper(DevServerMojo mojo) {
		super(mojo);
		resolveSdkRoot();
		installDevSocketImplFactory();
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

	@Override
	protected void configureSecurity() {
		SecurityHandler handler = new SecurityHandler();

		Constraint constraint = new Constraint();
		constraint.setName(Constraint.__FORM_AUTH);
		//constraint.setRoles(new String[] { USER_ROLE, ADMIN_ROLE });
		constraint.setAuthenticate(true);

		ConstraintMapping constraintMapping = new ConstraintMapping();
		constraintMapping.setConstraint(constraint);
		constraintMapping.setPathSpec("/*");

		handler.setConstraintMappings(new ConstraintMapping[] { constraintMapping });
		handler.setAuthenticator(new AppengineAuthenticator());
		handler.setUserRealm(new AppengineUserRealm());

		webapp.setSecurityHandler(handler);
	}

	@Override
	protected String getWebDefaultXml() {
		return "/webdefault-appengine.xml";
	}

	@Override
	protected List<String> getCustomClassPathElements() {
		List<String> elements = new ArrayList<String>();
		// elements.add(sdkRoot + "/lib/shared/servlet-api.jar");
		elements.add(sdkRoot + "/lib/shared/el-api.jar");
		elements.add(sdkRoot + "/lib/shared/jsp-api.jar");
		elements.add(sdkRoot + "/lib/impl/appengine-local-runtime.jar");
		elements.add(sdkRoot + "/lib/shared/appengine-local-runtime-shared.jar");
		elements.add(sdkRoot + "/lib/java-managed-vm/appengine-java-vmruntime/lib/ext/appengine-vm-runtime.jar");
		return elements;
	}

}
