package io.yawp.servlet;

import io.yawp.commons.utils.JsonUtils;

import io.yawp.commons.http.HttpException;
import io.yawp.commons.http.HttpResponse;
import io.yawp.commons.http.JsonResponse;
import io.yawp.commons.http.RequestContext;
import io.yawp.repository.Repository;
import io.yawp.repository.Yawp;

import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.logging.Logger;

public class GraphQLServlet extends HttpServlet {

    private static final long serialVersionUID = 8155293897299089610L;

    private final static Logger logger = Logger.getLogger(EndpointServlet.class.getName());

    public GraphQLServlet() {
    }

    @Override
    public void init(ServletConfig config) throws ServletException {
        super.init(config);
    }

    @Override
    protected void service(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
	    String body = JsonUtils.readJson(req.getReader());
	    System.out.println("=============================================" + body);
	    resp.setStatus(200);
    }

}
