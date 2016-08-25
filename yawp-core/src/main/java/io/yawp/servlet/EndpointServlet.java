package io.yawp.servlet;

import io.yawp.commons.http.HttpException;
import io.yawp.commons.http.HttpResponse;
import io.yawp.commons.http.JsonResponse;
import io.yawp.commons.http.RequestContext;
import io.yawp.driver.api.DriverFactory;
import io.yawp.repository.Repository;
import io.yawp.repository.Yawp;
import org.apache.commons.lang3.StringUtils;

import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Arrays;
import java.util.logging.Logger;

public class EndpointServlet extends HttpServlet {

    private static final long serialVersionUID = 8155293897299089610L;

    private final static Logger logger = Logger.getLogger(EndpointServlet.class.getName());

    private boolean enableHooks = true;

    private boolean enableCrossDomain = false;

    private CrossDomainParams crossDomainParams = new CrossDomainParams();

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

        boolean enableCrossDomain = false;
        if (config.getInitParameter("enableCrossDomain") != null) {
            enableCrossDomain = Boolean.valueOf(config.getInitParameter("enableCrossDomain"));
        } else {
            enableCrossDomain = !DriverFactory.getDriver().environment().isProduction();
        }

        setCrossDomain(String.valueOf(enableCrossDomain),
                config.getInitParameter("crossDomainOrigin"),
                config.getInitParameter("crossDomainHeaders"),
                config.getInitParameter("crossDomainMethods"));
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

    private void setCrossDomain(String enableCrossDomainParameter,
                                String crossDomainOrigin,
                                String crossDomainHeaders,
                                String crossDomainMethods) {
        if (enableCrossDomainParameter != null) {
            this.enableCrossDomain = Boolean.valueOf(enableCrossDomainParameter);
        } else {
            this.enableCrossDomain = !DriverFactory.getDriver().environment().isProduction();
        }

        if (this.enableCrossDomain && crossDomainOrigin != null && crossDomainHeaders != null && crossDomainMethods != null) {
            this.crossDomainParams.setOrigins(Arrays.asList(crossDomainOrigin.split(", ")));
            this.crossDomainParams.setHeaders(Arrays.asList(crossDomainHeaders.split(", ")));
            this.crossDomainParams.setMethods(Arrays.asList(crossDomainMethods.split(", ")));
        }
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

        if (enableCrossDomain
                && this.crossDomainParams.getHeaders() != null
                && this.crossDomainParams.getMethods() != null
                && this.crossDomainParams.getOrigins() != null) {

            resp.setHeader("Access-Control-Allow-Origin", StringUtils.join(this.crossDomainParams.getOrigins(), ", "));
            resp.setHeader("Access-Control-Allow-Headers", StringUtils.join(this.crossDomainParams.getHeaders(), ", "));
            resp.setHeader("Access-Control-Allow-Methods", StringUtils.join(this.crossDomainParams.getMethods(), ", "));
        }
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
