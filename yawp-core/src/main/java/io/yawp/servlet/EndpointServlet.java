package io.yawp.servlet;

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

public class EndpointServlet extends HttpServlet {

    private static final long serialVersionUID = 8155293897299089610L;

    private final static Logger logger = Logger.getLogger(EndpointServlet.class.getName());

    private boolean enableHooks = true;

    private boolean enableCrossDomain = false;

    private CrossDomainManager crossDomainManager = new CrossDomainManager();

    public EndpointServlet() {
    }

    protected EndpointServlet(String packagePrefix) {
        initYawp(packagePrefix);
    }

    @Override
    public void init(ServletConfig config) throws ServletException {
        super.init(config);
        setWithHooks(config.getInitParameter("enableHooks"));
        initYawp(config.getInitParameter("packagePrefix"));

        crossDomainManager.init(config);
    }

    @Override
    public void destroy() {
        super.destroy();
        Yawp.destroyFeatures();
    }

    private void setWithHooks(String enableHooksParameter) {
        if (!enableHooks) {
            return;
        }

        boolean enableHooks = enableHooksParameter == null || Boolean.valueOf(enableHooksParameter);
        setWithHooks(enableHooks);
    }

    protected void setWithHooks(boolean enableHooks) {
        this.enableHooks = enableHooks;
    }

    /**
     * @deprecated in 2.0, yawp will be configured only by yawp.yml
     */
    @Deprecated
    private void initYawp(String packagePrefix) {
        if (packagePrefix == null) {
            return;
        }
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
        logger.finer("begin");

        HttpResponse httpResponse;
        try {
            httpResponse = execute(new RequestContext(req, resp));
        } catch (HttpException e) {
            httpResponse = e.createResponse();
        }

        crossDomainManager.setResponseHeaders(resp);
        response(resp, httpResponse);

        logger.finer("done");
    }

    public HttpResponse execute(RequestContext ctx) {
        try {
            Repository r = getRepository(ctx);
            EndpointRouter router = EndpointRouter.parse(r, ctx);

            if (!router.isValid()) {
                throw new HttpException(400, "Invalid route. Please check uri, json format, object ids and parent structure, etc.");
            }

            return router.executeRestAction(enableHooks);

        } finally {
            Yawp.dispose();
        }
    }

    protected Repository getRepository(RequestContext ctx) {
        return Yawp.yawp().setRequestContext(ctx);
    }

}
