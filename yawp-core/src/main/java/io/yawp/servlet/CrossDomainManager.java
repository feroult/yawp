package io.yawp.servlet;

import io.yawp.driver.api.DriverFactory;
import org.apache.commons.lang3.StringUtils;

import javax.servlet.ServletConfig;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public class CrossDomainManager {

    private static final String ENABLE_CROSS_DOMAIN_PARAM = "enableCrossDomain";

    private static final String CROSS_DOMAIN_ORIGIN_PARAM = "crossDomainOrigin";

    private static final String CROSS_DOMAIN_METHODS_PARAM = "crossDomainMethods";

    private static final String CROSS_DOMAIN_HEADERS_PARAM = "crossDomainHeaders";

    public static final String DEFAULT_ORIGIN = "*";

    public static final String DEFAULT_METHODS = "GET, POST, PUT, PATCH, DELETE, OPTIONS, HEAD";

    public static final String DEFAULT_HEADERS = "Origin, X-Requested-With, Content-Type, Accept, Authorization";

    private boolean enableCrossDomain;

    private String origin;

    private String methods;

    private String headers;

    public void init(ServletConfig config) {
        this.enableCrossDomain = isCrossDomainEnabled(config);

        if (enableCrossDomain) {
            if (hasAnyValueSet(config)) {
                setOrigin(getOrigin(config));
                setMethods(getMethods(config));
                setHeaders(getHeaders(config));
            } else {
                setOrigin(DEFAULT_ORIGIN);
                setMethods(DEFAULT_METHODS);
                setHeaders(DEFAULT_HEADERS);
            }
        }
    }

    public void setResponseHeaders(HttpServletRequest request, HttpServletResponse response) {
        if (enableCrossDomain) {
            if (!StringUtils.isEmpty(origin)) {
                if (origin.equals("?")) {
                    response.setHeader("Access-Control-Allow-Origin", request.getHeader("Origin"));
                } else {
                    response.setHeader("Access-Control-Allow-Origin", origin);
                }
            }

            if (!StringUtils.isEmpty(methods)) {
                response.setHeader("Access-Control-Allow-Methods", methods);
            }

            if (!StringUtils.isEmpty(headers)) {
                response.setHeader("Access-Control-Allow-Headers", headers);
            }
        }
    }

    private String getOrigin(ServletConfig config) {
        return config.getInitParameter(CROSS_DOMAIN_ORIGIN_PARAM);
    }

    private String getMethods(ServletConfig config) {
        return config.getInitParameter(CROSS_DOMAIN_METHODS_PARAM);
    }

    private String getHeaders(ServletConfig config) {
        return config.getInitParameter(CROSS_DOMAIN_HEADERS_PARAM);
    }

    private void setOrigin(String origin) {
        this.origin = origin;
    }

    private void setMethods(String methods) {
        this.methods = methods;
    }

    private void setHeaders(String headers) {
        this.headers = headers;
    }

    private boolean isCrossDomainEnabled(ServletConfig config) {
        if (config.getInitParameter(ENABLE_CROSS_DOMAIN_PARAM) != null) {
            return Boolean.valueOf(config.getInitParameter(ENABLE_CROSS_DOMAIN_PARAM));
        } else {
            return !DriverFactory.getDriver().environment().isProduction();
        }
    }

    public boolean hasAnyValueSet(ServletConfig config) {
        return (config.getInitParameter(CROSS_DOMAIN_ORIGIN_PARAM) != null
                || config.getInitParameter(CROSS_DOMAIN_METHODS_PARAM) != null
                || config.getInitParameter(CROSS_DOMAIN_HEADERS_PARAM) != null);
    }
}
