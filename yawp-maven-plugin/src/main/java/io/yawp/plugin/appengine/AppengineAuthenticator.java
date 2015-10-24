package io.yawp.plugin.appengine;

import java.io.IOException;
import java.security.Principal;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;

import org.mortbay.jetty.Request;
import org.mortbay.jetty.Response;
import org.mortbay.jetty.security.Authenticator;
import org.mortbay.jetty.security.UserRealm;

public class AppengineAuthenticator implements Authenticator {

	private static final String CONTINUE_KEY = "continue";

	private static final long serialVersionUID = 8961206693928060634L;

	private static String LOGIN_URI = "/_ah/login";

	private static String DEV_APPSERVER_LOGIN_COOKIE = "dev_appserver_login";

	@Override
	public Principal authenticate(UserRealm realm, String uri, Request request, Response response) throws IOException {
		String cookie = getAppengineCookie(request);
		if (isUserLoggedIn(cookie)) {
			return authenticateInRealm(realm, cookie, request);
		}

		if (isLoginForm(request, uri)) {
			return NOBODY;
		}

		if (isLoginPost(request, uri)) {
			return redirectToContinueUri(response, request.getParameter(CONTINUE_KEY));
		}

		return redirectToLogin(response, uri);
	}

	@Override
	public String getAuthMethod() {
		return HttpServletRequest.FORM_AUTH;
	}

	private boolean isUserLoggedIn(String cookie) {
		return cookie != null;
	}

	private Principal authenticateInRealm(UserRealm realm, String cookie, Request request) {
		AppengineUser user = new AppengineUser(cookie);
		return realm.authenticate(user.getUsername(), user, request);
	}

	private Principal redirectToLogin(Response response, String uri) throws IOException {
		String redirectUri = String.format("%s?%s=%s", LOGIN_URI, CONTINUE_KEY, uri);
		response.sendRedirect(response.encodeRedirectURL(redirectUri));
		return null;
	}

	private Principal redirectToContinueUri(Response response, String uri) throws IOException {
		//response.sendRedirect(response.encodeRedirectURL(uri));
		return NOBODY;
	}

	private String getAppengineCookie(Request request) {
		Cookie[] cookies = request.getCookies();
		if (cookies == null) {
			return null;
		}

		for (int i = 0; i < cookies.length; i++) {
			Cookie cookie = cookies[i];
			if (cookie.getName().equals(DEV_APPSERVER_LOGIN_COOKIE)) {
				return cookie.getValue();
			}
		}
		return null;
	}

	private boolean isLoginForm(Request request, String uri) {
		return request.getMethod().equals("GET") && uri.startsWith(LOGIN_URI);
	}

	private boolean isLoginPost(Request request, String uri) {
		return request.getMethod().equals("POST") && uri.startsWith(LOGIN_URI);
	}

	public static Principal NOBODY = new Principal() {
		public String getName() {
			return "Nobody";
		}

		public String toString() {
			return getName();
		}
	};
}
