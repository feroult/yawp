package io.yawp.servlet;

import io.yawp.commons.http.ErrorResponse;
import io.yawp.commons.utils.Environment;

import java.io.IOException;

import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.google.appengine.api.users.UserService;
import com.google.appengine.api.users.UserServiceFactory;

public class FixturesServlet extends EndpointServlet {

	private static final long serialVersionUID = -7833278558858095857L;

	@Override
	public void init(ServletConfig config) throws ServletException {
		super.init(config);
		setWithHooks(false);
	}

	@Override
	protected void service(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		if (!enableFixtures()) {
			response(resp, new ErrorResponse(403));
			return;
		}
		super.service(req, resp);
	}

	private boolean enableFixtures() {
		if (!Environment.isProduction()) {
			return true;
		}
		UserService userService = UserServiceFactory.getUserService();
		return userService.isUserLoggedIn() && userService.isUserAdmin();
	}

}
