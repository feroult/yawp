package io.yawp.servlet;

import io.yawp.commons.http.ErrorResponse;
import io.yawp.driver.api.EnvironmentDriver;
import io.yawp.driver.api.Driver;
import io.yawp.driver.api.DriverFactory;
import io.yawp.repository.Repository;

import java.io.IOException;

import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public class FixturesServlet extends EndpointServlet {

	private static final long serialVersionUID = -7833278558858095857L;

	@Override
	public void init(ServletConfig config) throws ServletException {
		super.init(config);
		setWithHooks(false);
	}

	@Override
	protected void service(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		if (!enableFixtures(req)) {
			response(resp, new ErrorResponse(403));
			return;
		}
		super.service(req, resp);
	}

	private boolean enableFixtures(HttpServletRequest req) {
		Repository r = getRepository(makeParams(req));
		Driver driver = DriverFactory.getRepositoryDriver(r);

		EnvironmentDriver environment = driver.helpers().environment();

		if (!environment.isProduction()) {
			return true;
		}

		return environment.isAdmin();
	}

}
