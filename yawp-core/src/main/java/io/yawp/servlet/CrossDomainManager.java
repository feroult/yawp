package io.yawp.servlet;

import io.yawp.driver.api.DriverFactory;

import javax.servlet.ServletConfig;
import javax.servlet.http.HttpServletResponse;
import java.util.List;

/**
 * Created by walidsabihi on 25/08/2016.
 */
public class CrossDomainManager {

    String defaultOrigins = "*";
    String defaultHeaders = "Origin, X-Requested-With, Content-Type, Accept, Authorization";
    String defaultMethods = "GET, POST, PUT, PATCH, DELETE, OPTIONS, HEAD";

    String origins = null;
    String headers = null;
    String methods = null;

    public String getOrigins() {
        return origins;
    }
    public String getOrigins(ServletConfig config) {
        return config.getInitParameter("crossDomainOrigins");
    }

    public void setOrigins(String origins) {
        this.origins = origins;
    }

    public String getHeaders() {
        return headers;
    }
    public String getHeaders(ServletConfig config) {
        return config.getInitParameter("crossDomainHeaders");
    }

    public void setHeaders(String headers) {
        this.headers = headers;
    }

    public String getMethods() {
        return methods;
    }
    public String getMethods(ServletConfig config) {
        return config.getInitParameter("crossDomainMethods");
    }

    public void setMethods(String methods) {
        this.methods = methods;
    }

    public boolean hasAnyValueSet() {
        return (origins != null && headers != null && methods != null);
    }

    public boolean hasAnyValueSet(ServletConfig config) {
        return (config.getInitParameter("crossDomainOrigin") != null
                && config.getInitParameter("crossDomainHeaders") != null
                && config.getInitParameter("crossDomainMethods") != null);
    }

    public void setCustomValues(HttpServletResponse response) {
        response.setHeader("Access-Control-Allow-Origin", origins);
        response.setHeader("Access-Control-Allow-Headers", headers);
        response.setHeader("Access-Control-Allow-Methods", methods);
    }

    public void setDefaultValues(HttpServletResponse response) {
        response.setHeader("Access-Control-Allow-Origin", defaultOrigins);
        response.setHeader("Access-Control-Allow-Headers", defaultHeaders);
        response.setHeader("Access-Control-Allow-Methods", defaultMethods);
    }

    public void setResponseHeaders(HttpServletResponse response) {
        if (hasAnyValueSet()) {
            setCustomValues(response);
        } else {
            setDefaultValues(response);
        }
    }

    public boolean isCrossDomainEnabled(ServletConfig config) {
        if (config.getInitParameter("enableCrossDomain") != null) {
            return Boolean.valueOf(config.getInitParameter("enableCrossDomain"));
        } else {
            return !DriverFactory.getDriver().environment().isProduction();
        }
    }
}
