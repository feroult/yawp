package io.yawp.plugin.devserver.appengine;

import com.google.appengine.api.utils.SystemProperty;
import com.google.appengine.tools.development.LocalServerEnvironment;
import com.google.appengine.tools.development.testing.*;
import com.google.apphosting.api.ApiProxy;
import com.google.apphosting.api.ApiProxy.Environment;
import com.google.apphosting.utils.config.AppEngineWebXml;
import com.google.apphosting.utils.config.AppEngineWebXmlReader;
import com.google.apphosting.utils.config.WebXml;
import io.yawp.plugin.devserver.MojoWrapper;
import org.apache.commons.lang3.text.StrSubstitutor;
import org.mortbay.jetty.webapp.WebAppContext;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Map;

public class AppengineWebAppContext extends WebAppContext {

    private static final String API_PROXY_LOCAL = "com.google.appengine.devappserver.ApiProxyLocal";

    private static final String APPENGINE_WEB_XML = "com.google.appengine.tools.development.appEngineWebXml";

    private static final String WEB_XML = "com.google.appengine.tools.development.webXml";

    private final MojoWrapper mojo;

    private LocalServiceTestHelper helper;

    private Environment environment;

    public AppengineWebAppContext(MojoWrapper mojo) {
        super(mojo.getAppDir(), "");
        this.mojo = mojo;
        this.helper = createHelper();
    }

    @Override
    protected void doStart() throws Exception {
        this.helper = createHelper();
        this.environment = ApiProxy.getCurrentEnvironment();
        getServletContext().setAttribute(API_PROXY_LOCAL, ApiProxy.getDelegate());
        getServletContext().setAttribute(APPENGINE_WEB_XML, readAppengineWebXml());
        getServletContext().setAttribute(WEB_XML, readWebXml());
        SystemProperty.environment.set(SystemProperty.Environment.Value.Development);
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

    private AppEngineWebXml readAppengineWebXml() {
        AppEngineWebXmlReader reader = new AppEngineWebXmlReader(mojo.getAppDir());
        AppEngineWebXml appEngineWebXml = reader.readAppEngineWebXml();
        applySystemProperties(appEngineWebXml);
        return appEngineWebXml;
    }

    private void applySystemProperties(AppEngineWebXml appEngineWebXml) {
        Map<String, String> props = appEngineWebXml.getSystemProperties();

        for (String key : props.keySet()) {
            System.setProperty(key, interpolate(props.get(key)));
        }
    }

    private String interpolate(String s) {
        StrSubstitutor sub = new StrSubstitutor((Map) System.getProperties(), "${", "}");
        return sub.replace(s);
    }

    private WebXml readWebXml() {
        return new WebXml() {
            @Override
            public boolean matches(String url) {
                return true;
            }
        };
    }

    private LocalServiceTestHelper createHelper() {
        LocalServiceTestHelper helper =
                new LocalServiceTestHelper(new LocalUserServiceTestConfig(),
                        createDatastoreServiceTestConfig(),
                        createTaskQueueTestConfig(),
                        createSearchTestConfig(),
                        new LocalModulesServiceTestConfig(),
                        new LocalMemcacheServiceTestConfig()) {
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

    private LocalSearchServiceTestConfig createSearchTestConfig() {
        return new LocalSearchServiceTestConfig()
                .setPersistent(true)
                .setStorageDirectory("target/appengine-generated");
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
