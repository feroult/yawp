package endpoint.servlet;

import java.io.IOException;
import java.util.logging.Logger;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;

import com.google.appengine.api.NamespaceManager;
import com.google.appengine.api.users.User;
import com.google.appengine.api.users.UserServiceFactory;

public class NamespaceFilter implements Filter {

	private static final String NS_PARAMETER = "ns";

	Logger logger = Logger.getLogger(NamespaceFilter.class.getSimpleName());

	@Override
	public void init(FilterConfig filterConfig) throws ServletException {
	}

	@Override
	public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException {
		if (NamespaceManager.get() == null) {
			User currentUser = UserServiceFactory.getUserService().getCurrentUser();
			if (currentUser != null) {
				namespaceByUser(currentUser);
			} else {
				String ns = request.getParameter(NS_PARAMETER);
				if (ns != null) {
					namespaceByParameter(ns);
				}
			}
		}
		chain.doFilter(request, response);
	}

	private void namespaceByParameter(String ns) {
		NamespaceManager.set(ns);
		logger.info("namespace by ns: " + ns);
	}

	private void namespaceByUser(User currentUser) {
		String userId = currentUser.getUserId();
		NamespaceManager.set(userId);
		logger.info("namespace by user: " + userId);
	}

	@Override
	public void destroy() {
	}
}
