package io.yawp.repository.cache;

import java.io.IOException;

import javax.servlet.*;


public class ClearCacheFilter implements Filter {

	@Override
	public void init(FilterConfig filterConfig) throws ServletException { }

	@Override
	public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException {
		Cache.clearAll();
		chain.doFilter(request, response);
	}

	@Override
	public void destroy() { }
}
