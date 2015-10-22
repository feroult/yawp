package io.yawp.plugin.appengine;

import java.io.IOException;
import java.security.Principal;

import javax.servlet.http.HttpServletRequest;

import org.mortbay.jetty.Request;
import org.mortbay.jetty.Response;
import org.mortbay.jetty.security.Authenticator;
import org.mortbay.jetty.security.UserRealm;

import com.google.appengine.api.users.User;
import com.google.appengine.api.users.UserService;
import com.google.appengine.api.users.UserServiceFactory;

public class AppengineAuthenticator implements Authenticator {

	private static final long serialVersionUID = 8961206693928060634L;

	private static String LOGIN_URI = "/_ah/login";

	@Override
	public Principal authenticate(UserRealm realm, String uri, Request request, Response response) throws IOException {
		System.out.println("here");
		if (isLoginPage(uri) || true) {
			return NOBODY;
		}

		UserService userService = UserServiceFactory.getUserService();
		if (userService.isUserLoggedIn()) {
			return authenticate(realm, userService, request);
		}

		return redirectToLogin(response);
	}

	private Principal authenticate(UserRealm realm, UserService userService, Request request) {
		AppengineUserRealm appengineRealm = (AppengineUserRealm) realm;
		User user = userService.getCurrentUser();
		return appengineRealm.authenticate(user.getUserId(), new AppengineUser(user, userService.isUserAdmin()), request);
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

	public static Principal NOBODY = new Principal() {
		public String getName() {
			return "Nobody";
		}

		public String toString() {
			return getName();
		}
	};
}
