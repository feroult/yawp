package io.yawp.servlet;

import io.yawp.commons.http.ExceptionResponse;
import io.yawp.driver.api.Driver;
import io.yawp.driver.api.DriverFactory;
import io.yawp.repository.Yawp;

import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

public class FixturesServlet extends EndpointServlet {

	private static final long serialVersionUID = -7833278558858095857L;

	@Override
	public void init(ServletConfig config) throws ServletException {
		super.init(config);
		setWithShields(false);
		setWithHooks(false);
	}

	@Override
	protected void service(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		if (!enableFixtures()) {
			response(resp, new ExceptionResponse(403));
			return;
		}

		Yawp.yawp.namespace(req.getHeader("namespace"));
		super.service(req, resp);
	}

	private boolean enableFixtures() {
		Driver driver = DriverFactory.getDriver();

		if (!driver.environment().isProduction()) {
			return true;
		}

		return driver.environment().isAdmin();
	}

}
