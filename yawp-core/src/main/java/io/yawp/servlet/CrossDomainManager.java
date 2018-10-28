package io.yawp.servlet;

import io.yawp.driver.api.DriverFactory;
import org.apache.commons.lang3.StringUtils;

import javax.servlet.ServletConfig;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.Map;

public class CrossDomainManager {

    public static final String ENABLE_CROSS_DOMAIN_PARAM = "enableCrossDomain";

    public static final String CROSS_DOMAIN_ORIGIN_PARAM = "crossDomainOrigin";

    public static final String CROSS_DOMAIN_METHODS_PARAM = "crossDomainMethods";

    public static final String CROSS_DOMAIN_HEADERS_PARAM = "crossDomainHeaders";

    public static final String CROSS_DOMAIN_ALLOW_CREDENTIALS_PARAM = "crossDomainAllowCredentials";

    public static final String CROSS_DOMAIN_EXPOSE_HEADERS_PARAM = "crossDomainExposeHeaders";

    public static final String DEFAULT_ORIGIN = "?";

    public static final String DEFAULT_METHODS = "GET, POST, PUT, PATCH, DELETE, OPTIONS, HEAD";

    public static final String DEFAULT_HEADERS = "Origin, X-Requested-With, Content-Type, Accept, Authorization, namespace";

    public static final String DEFAULT_ALLOW_CREDENTIALS = "true";

    public static final String DEFAULT_EXPOSE_HEADERS = null;

    private boolean enableCrossDomain;

    private String origin;

    private String methods;

    private String headers;

    private String allowCredentials;

    private String exposeHeaders;

    public CrossDomainManager() {}

    public CrossDomainManager(Map<String, String> params) {
        this.enableCrossDomain = "true".equalsIgnoreCase(params.get("enableCrossDomain"));
        this.origin = params.get("origin");
        this.methods = params.get("methods");
        this.headers = params.get("headers");
        this.allowCredentials = params.get("allowCredentials");
        this.exposeHeaders = params.get("exposeHeaders");
    }

    public void init(ServletConfig config) {
        this.enableCrossDomain = isCrossDomainEnabled(config);

        if (enableCrossDomain) {
            if (hasAnyValueSet(config)) {
                setOrigin(getOriginFromConfig(config));
                setMethods(getMethodsFromConfig(config));
                setHeaders(getHeadersFromConfig(config));
                setAllowCredentials(getAllowCredentialsFromConfig(config));
                setExposeHeaders(getExposeHeadersFromConfig(config));
            } else {
                setOrigin(DEFAULT_ORIGIN);
                setMethods(DEFAULT_METHODS);
                setHeaders(DEFAULT_HEADERS);
                setAllowCredentials(DEFAULT_ALLOW_CREDENTIALS);
                setExposeHeaders(DEFAULT_EXPOSE_HEADERS);
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

            if (!StringUtils.isEmpty(allowCredentials)) {
                response.setHeader("Access-Control-Allow-Credentials", allowCredentials);
            }

            if (!StringUtils.isEmpty(exposeHeaders)) {
                response.setHeader("Access-Control-Expose-Headers", exposeHeaders);
            }
        }
    }

    private String getOriginFromConfig(ServletConfig config) {
        return config.getInitParameter(CROSS_DOMAIN_ORIGIN_PARAM);
    }

    private String getMethodsFromConfig(ServletConfig config) {
        return config.getInitParameter(CROSS_DOMAIN_METHODS_PARAM);
    }

    private String getHeadersFromConfig(ServletConfig config) {
        return config.getInitParameter(CROSS_DOMAIN_HEADERS_PARAM);
    }

    private String getAllowCredentialsFromConfig(ServletConfig config) {
        return config.getInitParameter(CROSS_DOMAIN_ALLOW_CREDENTIALS_PARAM);
    }

    private String getExposeHeadersFromConfig(ServletConfig config) {
        return config.getInitParameter(CROSS_DOMAIN_EXPOSE_HEADERS_PARAM);
    }

    public boolean isEnableCrossDomain() {
        return enableCrossDomain;
    }

    public String getOrigin() {
        return origin;
    }

    public String getMethods() {
        return methods;
    }

    public String getHeaders() {
        return headers;
    }

    public String getAllowCredentials() {
        return allowCredentials;
    }

    public String getExposeHeaders() {
        return exposeHeaders;
    }

    public void setOrigin(String origin) {
        this.origin = origin;
    }

    public void setMethods(String methods) {
        this.methods = methods;
    }

    public void setHeaders(String headers) {
        this.headers = headers;
    }

    public void setAllowCredentials(String allowCredentials) {
        this.allowCredentials = allowCredentials;
    }

    public void setExposeHeaders(String exposeHeaders) {
        this.exposeHeaders = exposeHeaders;
    }

    private boolean isCrossDomainEnabled(ServletConfig config) {
        if (config.getInitParameter(ENABLE_CROSS_DOMAIN_PARAM) != null) {
            return Boolean.valueOf(config.getInitParameter(ENABLE_CROSS_DOMAIN_PARAM));
        } else {
            return !DriverFactory.getDriver().environment().isProduction();
        }
    }

    private boolean hasAnyValueSet(ServletConfig config) {
        return (config.getInitParameter(CROSS_DOMAIN_ORIGIN_PARAM) != null
                || config.getInitParameter(CROSS_DOMAIN_METHODS_PARAM) != null
                || config.getInitParameter(CROSS_DOMAIN_HEADERS_PARAM) != null);
    }
}
