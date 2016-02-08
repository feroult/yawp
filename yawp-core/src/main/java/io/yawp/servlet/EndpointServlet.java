package io.yawp.servlet;

import io.yawp.commons.http.HttpException;
import io.yawp.commons.http.HttpResponse;
import io.yawp.commons.http.JsonResponse;
import io.yawp.commons.http.RequestContext;
import io.yawp.driver.api.DriverFactory;
import io.yawp.repository.Repository;
import io.yawp.repository.Yawp;

import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

public class EndpointServlet extends HttpServlet {

    private static final long serialVersionUID = 8155293897299089610L;

    private boolean enableHooks = true;

    private boolean enableCrossDomain = false;

    public EndpointServlet() {
    }

    @Override
    public void init(ServletConfig config) throws ServletException {
        super.init(config);
        setWithHooks(config.getInitParameter("enableHooks"));
        setCrossDomain(config.getInitParameter("enableCrossDomain"));
        initYawp(config.getInitParameter("packagePrefix"));
    }

    private void setWithHooks(String enableHooksParameter) {
        if (!enableHooks) {
            return;
        }

        boolean enableHooks = enableHooksParameter == null || Boolean.valueOf(enableHooksParameter);
        setWithHooks(enableHooks);
    }

    private void setCrossDomain(String enableCrossDomainParameter) {
        if (enableCrossDomainParameter != null) {
            this.enableCrossDomain = Boolean.valueOf(enableCrossDomainParameter);
        } else {
            this.enableCrossDomain = !DriverFactory.getDriver().environment().isProduction();
        }
    }

    protected void setWithHooks(boolean enableHooks) {
        this.enableHooks = enableHooks;
    }

    protected EndpointServlet(String packagePrefix) {
        initYawp(packagePrefix);
    }

    /**
     * @deprecated in 2.0, yawp will be configured only by yawp.yml
     */
    @Deprecated
    private void initYawp(String packagePrefix) {
        Yawp.init(packagePrefix);
    }

    protected void response(HttpServletResponse resp, HttpResponse httpResponse) throws IOException {
        if (httpResponse == null) {
            new JsonResponse().execute(resp);
        } else {
            httpResponse.execute(resp);
        }
    }

    @Override
    protected void service(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        HttpResponse httpResponse;
        try {
            httpResponse = execute(new RequestContext(req, resp));
        } catch (HttpException e) {
            httpResponse = e.createResponse();
        }

        if (enableCrossDomain) {
            resp.setHeader("Access-Control-Allow-Origin", "*");
            resp.setHeader("Access-Control-Allow-Headers", "Origin, X-Requested-With, Content-Type, Accept");
            resp.setHeader("Access-Control-Allow-Methods", "GET, POST, PUT, OPTIONS, DELETE");
        }
        response(resp, httpResponse);
    }

    public HttpResponse execute(RequestContext ctx) {
        Repository r = getRepository(ctx);

        EndpointRouter router = EndpointRouter.parse(r, ctx);

        if (!router.isValid()) {
            throw new HttpException(400, "Invalid route. Please check uri, json format, object ids and parent structure, etc.");
        }

        return router.executeRestAction(enableHooks);
    }

    protected Repository getRepository(RequestContext ctx) {
        return Yawp.yawp().setRequestContext(ctx);
    }

}
