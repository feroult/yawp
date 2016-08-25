package io.yawp.servlet;

import io.yawp.driver.api.DriverFactory;
import org.apache.commons.lang3.StringUtils;

import javax.servlet.ServletConfig;
import javax.servlet.http.HttpServletResponse;

public class CrossDomainManager {

    private static final String ENABLE_CROSS_DOMAIN_PARAM = "enableCrossDomain";

    private static final String CROSS_DOMAIN_ORIGINS_PARAM = "crossDomainOrigins";

    private static final String CROSS_DOMAIN_METHODS_PARAM = "crossDomainMethods";

    private static final String CROSS_DOMAIN_HEADERS_PARAM = "crossDomainHeaders";

    public static final String DEFAULT_ORIGINS = "*";

    public static final String DEFAULT_METHODS = "GET, POST, PUT, PATCH, DELETE, OPTIONS, HEAD";

    public static final String DEFAULT_HEADERS = "Origin, X-Requested-With, Content-Type, Accept, Authorization";

    private boolean enableCrossDomain;

    private String origins;

    private String methods;

    private String headers;

    public void init(ServletConfig config) {
        this.enableCrossDomain = isCrossDomainEnabled(config);

        if (enableCrossDomain) {
            setOrigins(getOrigins(config));
            setMethods(getMethods(config));
            setHeaders(getHeaders(config));
        }
    }

    public void setResponseHeaders(HttpServletResponse response) {
        if (enableCrossDomain) {
            response.setHeader("Access-Control-Allow-Origin", origins);
            response.setHeader("Access-Control-Allow-Headers", headers);
            response.setHeader("Access-Control-Allow-Methods", methods);
        }
    }

    private String getOrigins(ServletConfig config) {
        return defaultTo(config.getInitParameter(CROSS_DOMAIN_ORIGINS_PARAM), DEFAULT_ORIGINS);
    }

    private String getMethods(ServletConfig config) {
        return defaultTo(config.getInitParameter(CROSS_DOMAIN_METHODS_PARAM), DEFAULT_METHODS);
    }

    private String getHeaders(ServletConfig config) {
        return defaultTo(config.getInitParameter(CROSS_DOMAIN_HEADERS_PARAM), DEFAULT_HEADERS);
    }

    private void setOrigins(String origins) {
        this.origins = origins;
    }

    private void setHeaders(String headers) {
        this.headers = headers;
    }

    private void setMethods(String methods) {
        this.methods = methods;
    }

    private String defaultTo(String customValue, String defaultValue) {
        return StringUtils.isEmpty(customValue) ? defaultValue : customValue;
    }

    private boolean isCrossDomainEnabled(ServletConfig config) {
        if (config.getInitParameter(ENABLE_CROSS_DOMAIN_PARAM) != null) {
            return Boolean.valueOf(config.getInitParameter(ENABLE_CROSS_DOMAIN_PARAM));
        } else {
            return !DriverFactory.getDriver().environment().isProduction();
        }
    }
}
