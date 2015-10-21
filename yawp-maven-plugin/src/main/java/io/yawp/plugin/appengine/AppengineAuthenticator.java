package io.yawp.plugin.appengine;

import java.io.IOException;
import java.security.Principal;

import javax.servlet.http.HttpServletRequest;

import org.mortbay.jetty.Request;
import org.mortbay.jetty.Response;
import org.mortbay.jetty.security.Authenticator;
import org.mortbay.jetty.security.SecurityHandler;
import org.mortbay.jetty.security.UserRealm;

import com.google.appengine.api.users.User;
import com.google.appengine.api.users.UserService;
import com.google.appengine.api.users.UserServiceFactory;

public class AppengineAuthenticator implements Authenticator {

	private static final long serialVersionUID = 8961206693928060634L;

	private static String LOGIN_URI = "/_ah/login";

	@Override
	public Principal authenticate(UserRealm realm, String uri, Request request, Response response) throws IOException {
		if (isLoginPage(uri)) {
			return SecurityHandler.__NOBODY;
		}

		UserService userService = UserServiceFactory.getUserService();
		if (userService.isUserLoggedIn()) {
			return getPrincipal(userService.getCurrentUser());
		}

		return redirectToLogin(response);
	}

	private Principal getPrincipal(User user) {
		return null;
	}

	private Principal redirectToLogin(Response response) throws IOException {
		response.sendRedirect(response.encodeRedirectURL(LOGIN_URI));
		return null;
	}

	private boolean isLoginPage(String uri) {
		return uri.startsWith(LOGIN_URI);
	}

	@Override
	public String getAuthMethod() {
		return HttpServletRequest.FORM_AUTH;
	}

}
