package io.yawp.plugin;

import io.yawp.driver.api.DriverFactory;

import java.io.IOException;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;

public class DevServerFilterProxy implements Filter {

	private Filter proxy;

	@Override
	public void init(FilterConfig filterConfig) throws ServletException {
		if (proxy == null) {
			proxy = DriverFactory.getDriver().helpers().getDevServerFilter();
		}
		proxy.init(filterConfig);
	}

	@Override
	public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException {
		proxy.doFilter(request, response, chain);
	}

	@Override
	public void destroy() {
		proxy.destroy();
	}

}
