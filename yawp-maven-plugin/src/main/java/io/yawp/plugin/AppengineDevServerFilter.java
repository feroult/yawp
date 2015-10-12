package io.yawp.plugin;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;

import com.google.appengine.tools.development.testing.LocalDatastoreServiceTestConfig;
import com.google.appengine.tools.development.testing.LocalServiceTestHelper;
import com.google.appengine.tools.development.testing.LocalUserServiceTestConfig;
import com.google.apphosting.api.ApiProxy;
import com.google.apphosting.api.ApiProxy.Environment;

public class AppengineDevServerFilter implements Filter {

	private LocalServiceTestHelper helper;

	private Environment environment;

	@Override
	public void init(FilterConfig filterConfig) throws ServletException {
		this.helper = createHelper();
		if (environment == null) {
			environment = ApiProxy.getCurrentEnvironment();
		}
		filterConfig.getServletContext().setAttribute("com.google.appengine.devappserver.ApiProxyLocal", ApiProxy.getDelegate());
	}

	@Override
	public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException {
		ApiProxy.setEnvironmentForCurrentThread(environment);
		chain.doFilter(request, response);
	}

	@Override
	public void destroy() {
		helper.tearDown();
	}

	private LocalServiceTestHelper createHelper() {
		LocalServiceTestHelper helper = new LocalServiceTestHelper(new LocalUserServiceTestConfig(), createDatastoreServiceTestConfig());
		helper.setEnvIsLoggedIn(true);
		Map<String, Object> envs = new HashMap<String, Object>();
		helper.setEnvAttributes(envs);
		helper.setEnvAuthDomain("localhost");
		helper.setEnvEmail("test@localhost");
		helper.setUp();
		return helper;
	}

	private LocalDatastoreServiceTestConfig createDatastoreServiceTestConfig() {
		LocalDatastoreServiceTestConfig config = new LocalDatastoreServiceTestConfig();
		config.setNoStorage(false);
		config.setBackingStoreLocation("target/appengine-generated/local_db.bin");
		return config;
	}

}
