package io.yawp.plugin.devserver.appengine;

import com.google.appengine.tools.development.DevSocketImplFactory;
import io.yawp.plugin.devserver.WebAppContextHelper;
import io.yawp.plugin.devserver.base.MojoWrapper;
import org.apache.maven.plugin.MojoExecutionException;
import org.mortbay.jetty.security.SecurityHandler;
import org.mortbay.jetty.webapp.WebAppContext;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class AppengineWebAppContextHelper extends WebAppContextHelper {

    private String sdkRoot;

    public AppengineWebAppContextHelper(MojoWrapper mojo) {
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
    protected WebAppContext createWebAppContext() {
        return new AppengineWebAppContext(mojo);
    }

    @Override
    protected void configureCustom() {
        configureSecurity();
    }

    private void configureSecurity() {
        SecurityHandler handler = new SecurityHandler();

        handler.setAuthenticator(new AppengineAuthenticator());
        handler.setUserRealm(createAppengineUserRealm());

        webapp.setSecurityHandler(handler);
    }

    private AppengineUserRealm createAppengineUserRealm() {
        return new AppengineUserRealm();
    }

    @Override
    protected String getWebDefaultXml() {
        return "/webdefault-appengine.xml";
    }

    @Override
    protected List<String> getCustomClasspathElements() {
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
