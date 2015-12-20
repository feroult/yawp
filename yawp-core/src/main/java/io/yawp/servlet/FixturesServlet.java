package io.yawp.servlet;

import io.yawp.commons.http.ExceptionResponse;
import io.yawp.driver.api.Driver;
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
        RequestContext ctx = new RequestContext(req, resp);

        if (!enableFixtures(ctx)) {
            response(resp, new ExceptionResponse(403));
            return;
        }
        
        super.service(req, resp);
    }

    private boolean enableFixtures(RequestContext ctx) {
        Repository r = getRepository(ctx.getParams());
        Driver driver = r.getDriver();

        if (!driver.environment().isProduction()) {
            return true;
        }

        return driver.environment().isAdmin();
    }

}
