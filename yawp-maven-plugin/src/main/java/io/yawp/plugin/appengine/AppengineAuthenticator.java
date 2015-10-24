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

	private static final long serialVersionUID = 8961206693928060634L;

	private static final String LOGIN_URI = "/_ah/login";

	private static final String DEV_APPSERVER_LOGIN_COOKIE = "dev_appserver_login";

	private static final String CONTINUE_KEY = "continue";

	private static final Principal NOBODY = new Principal() {
		public String getName() {
			return "Nobody";
		}
	};

	@Override
	public Principal authenticate(UserRealm realm, String uri, Request request, Response response) throws IOException {
		String cookie = getAppengineCookie(request);
		if (isUserLoggedIn(cookie)) {
			return authenticateInRealm(realm, cookie, request);
		}

		if (isLoginUri(uri)) {
			return NOBODY;
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

	private boolean isLoginUri(String uri) {
		return uri.startsWith(LOGIN_URI);
	}

}
