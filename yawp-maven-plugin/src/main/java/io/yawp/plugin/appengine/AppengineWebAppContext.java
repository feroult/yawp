package io.yawp.plugin.appengine;

import java.io.IOException;

import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.mortbay.jetty.webapp.WebAppContext;

import com.google.appengine.tools.development.testing.LocalDatastoreServiceTestConfig;
import com.google.appengine.tools.development.testing.LocalServiceTestHelper;
import com.google.appengine.tools.development.testing.LocalUserServiceTestConfig;
import com.google.apphosting.api.ApiProxy;
import com.google.apphosting.api.ApiProxy.Environment;
import com.google.apphosting.utils.config.AppEngineWebXml;
import com.google.apphosting.utils.config.AppEngineWebXmlReader;

public class AppengineWebAppContext extends WebAppContext {

	private static final String API_PROXY_LOCAL = "com.google.appengine.devappserver.ApiProxyLocal";

	private static final String APPENGINE_WEB_XML = "com.google.appengine.tools.development.appEngineWebXml";

	private LocalServiceTestHelper helper;

	private Environment environment;

	private String appDir;

	public AppengineWebAppContext(String appDir, String contextPath) {
		super(appDir, contextPath);
		this.appDir = appDir;
		this.helper = createHelper();
	}

	@Override
	protected void doStart() throws Exception {
		this.environment = ApiProxy.getCurrentEnvironment();
		getServletContext().setAttribute(API_PROXY_LOCAL, ApiProxy.getDelegate());
		getServletContext().setAttribute(APPENGINE_WEB_XML, readAppengineWebXml(getServletContext()));
		super.doStart();
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
		AppEngineWebXmlReader reader = new AppEngineWebXmlReader(appDir);
		return reader.readAppEngineWebXml();
	}

	private LocalServiceTestHelper createHelper() {
		LocalServiceTestHelper helper = new LocalServiceTestHelper(new LocalUserServiceTestConfig(), createDatastoreServiceTestConfig());
		helper.setUp();
		return helper;
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
