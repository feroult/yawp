package io.yawp.commons.http;

import io.yawp.commons.utils.JsonUtils;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Logger;

public class RequestContext {

    private final static Logger logger = Logger.getLogger(RequestContext.class.getName());

    private HttpServletResponse resp;

    private HttpServletRequest req;

    protected String method;

    protected String optionsRequestMethod;

    protected String uri;

    protected String json;

    protected Map<String, String> params;

    protected Class<?> endpointClazz;

    public RequestContext() {
    }

    public RequestContext(HttpServletRequest req, HttpServletResponse resp) {
        logger.finer("parsing request context");

        this.req = req;
        this.resp = resp;

        this.method = parseMethod();
        this.optionsRequestMethod = parseOptionsRequestMethod();
        this.uri = parseUri();
        this.json = parseJson();
        this.params = parseParams();

        logger.finer("done");
    }

    public HttpServletRequest req() {
        return req;
    }

    public HttpServletResponse resp() {
        return resp;
    }

    public String getMethod() {
        return method;
    }

    public String getOptionsRequestMethod() {
        return optionsRequestMethod;
    }

    public HttpVerb getHttpVerb() {
        return HttpVerb.fromString(method);
    }

    public String getUri() {
        return uri;
    }

    public String getJson() {
        return json;
    }

    public Map<String, String> getParams() {
        return params;
    }

    public boolean hasParam(String key) {
        return params.containsKey(key);
    }

    public String getParam(String key) {
        return params.get(key);
    }

    private String parseMethod() {
        return req.getMethod();
    }

    private String parseOptionsRequestMethod() {
        return !this.method.equalsIgnoreCase("OPTIONS") ? null : req.getHeader("Access-Control-Request-Method");
    }

    private String parseUri() {
        return req.getRequestURI().substring(req.getServletPath().length());
    }

    private String parseJson() {
        try {
            return JsonUtils.readJson(req.getReader());
        } catch (IOException e) {
            throw new RuntimeException();
        }
    }

    private Map<String, String> parseParams() {
        Map<String, String> map = new HashMap<>();

        Enumeration<String> e = req.getParameterNames();
        while (e.hasMoreElements()) {
            String name = e.nextElement();
            map.put(name, req.getParameter(name));
        }

        return map;
    }

    public void setEndpointClazz(Class<?> endpointClazz) {
        this.endpointClazz = endpointClazz;
    }
}
