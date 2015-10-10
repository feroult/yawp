package io.yawp.plugin;

import io.yawp.driver.api.DevServerHelper;
import io.yawp.driver.api.TestHelperFactory;

import java.io.IOException;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;

public class DriverFilter implements Filter {

	private DevServerHelper helper;

	@Override
	public void destroy() {
	}

	@Override
	public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException {
		if (helper != null) {
			helper.setup();
		}
		chain.doFilter(request, response);
	}

	@Override
	public void init(FilterConfig config) throws ServletException {
		this.helper = TestHelperFactory.getDevServerHelper();
	}

}
