package io.yawp.plugin.devserver.appengine;

import com.google.appengine.tools.development.LocalServerEnvironment;
import com.google.appengine.tools.development.testing.LocalDatastoreServiceTestConfig;
import com.google.appengine.tools.development.testing.LocalServiceTestHelper;
import com.google.appengine.tools.development.testing.LocalTaskQueueTestConfig;
import com.google.appengine.tools.development.testing.LocalUserServiceTestConfig;
import com.google.apphosting.api.ApiProxy;
import com.google.apphosting.api.ApiProxy.Environment;
import com.google.apphosting.utils.config.AppEngineWebXml;
import com.google.apphosting.utils.config.AppEngineWebXmlReader;
import io.yawp.plugin.devserver.DevServerMojo;
import org.mortbay.jetty.webapp.WebAppContext;

import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

public class AppengineWebAppContext extends WebAppContext {

    private static final String API_PROXY_LOCAL = "com.google.appengine.devappserver.ApiProxyLocal";

    private static final String APPENGINE_WEB_XML = "com.google.appengine.tools.development.appEngineWebXml";

    private final DevServerMojo mojo;

    private LocalServiceTestHelper helper;

    private Environment environment;

    public AppengineWebAppContext(DevServerMojo mojo) {
        super(mojo.getAppDir(), "");
        this.mojo = mojo;
        this.helper = createHelper();
    }

    @Override
    protected void doStart() throws Exception {
        this.helper = createHelper();
        this.environment = ApiProxy.getCurrentEnvironment();
        getServletContext().setAttribute(API_PROXY_LOCAL, ApiProxy.getDelegate());
        getServletContext().setAttribute(APPENGINE_WEB_XML, readAppengineWebXml(getServletContext()));
        configureUserRealmAppengineHelper();
        super.doStart();
    }

    private void configureUserRealmAppengineHelper() {
        ((AppengineUserRealm) getSecurityHandler().getUserRealm()).setHelper(helper);
    }

    @Override
    public void handle(String target, HttpServletRequest request, HttpServletResponse response, int dispatch) throws IOException,
            ServletException {
        ApiProxy.setEnvironmentForCurrentThread(environment);
        super.handle(target, request, response, dispatch);
    }

    @Override
    protected void doStop() throws Exception {
        ApiProxy.setEnvironmentForCurrentThread(environment);
        helper.tearDown();
        environment = null;
        super.doStop();
    }

    private AppEngineWebXml readAppengineWebXml(ServletContext servletContext) {
        AppEngineWebXmlReader reader = new AppEngineWebXmlReader(mojo.getAppDir());
        return reader.readAppEngineWebXml();
    }

    private LocalServiceTestHelper createHelper() {
        LocalServiceTestHelper helper = new LocalServiceTestHelper(new LocalUserServiceTestConfig(), createDatastoreServiceTestConfig(), createTaskQueueTestConfig()) {
            @Override
            protected LocalServerEnvironment newLocalServerEnvironment() {
                return new TestLocalServerEnvironment(mojo, super.newLocalServerEnvironment());
            }
        };
        helper.setUp();
        return helper;
    }

    private LocalTaskQueueTestConfig createTaskQueueTestConfig() {
        LocalTaskQueueTestConfig config = new LocalTaskQueueTestConfig();
        config.setShouldCopyApiProxyEnvironment(true);
        config.setDisableAutoTaskExecution(false);
        return config;
    }

    private LocalDatastoreServiceTestConfig createDatastoreServiceTestConfig() {
        LocalDatastoreServiceTestConfig config = new LocalDatastoreServiceTestConfig();
        config.setNoStorage(false);
        config.setBackingStoreLocation("target/appengine-generated/local_db.bin");
        return config;
    }

    public LocalServiceTestHelper getHelper() {
        return helper;
    }

}
