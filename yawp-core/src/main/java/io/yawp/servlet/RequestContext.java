package io.yawp.servlet;

import io.yawp.commons.http.HttpVerb;
import io.yawp.commons.utils.JsonUtils;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Map;

public class RequestContext {

    private HttpServletResponse resp;

    private HttpServletRequest req;

    protected String method;

    protected String uri;

    protected String json;

    protected Map<String, String> params;

    public RequestContext() {
    }

    public RequestContext(HttpServletRequest req, HttpServletResponse resp) {
        this.req = req;
        this.resp = resp;

        this.method = parseMethod();
        this.uri = parseUri();
        this.json = parseJson();
        this.params = parseParams();
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

    private String parseMethod() {
        return req.getMethod();
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
}
