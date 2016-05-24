package io.yawp.plugin.devserver.appengine;

import com.google.appengine.tools.development.DevSocketImplFactory;
import io.yawp.plugin.devserver.WebAppContextHelper;
import io.yawp.plugin.devserver.MojoWrapper;
import org.mortbay.jetty.security.SecurityHandler;
import org.mortbay.jetty.webapp.WebAppContext;

public class AppengineWebAppContextHelper extends WebAppContextHelper {

    public AppengineWebAppContextHelper(MojoWrapper mojo) {
        super(mojo);
        installDevSocketImplFactory();
    }

    private void installDevSocketImplFactory() {
        DevSocketImplFactory.install();
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


}
