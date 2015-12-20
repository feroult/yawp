package io.yawp.commons.utils;

import io.yawp.servlet.RequestContext;

import java.util.Map;

public class RequestContextMock extends RequestContext {

    private String method;

    private String uri;

    private String json;

    private Map<String, String> params;

    public RequestContextMock() {
        super(null, null);
    }

    @Override
    public String getMethod() {
        return method;
    }

    @Override
    public String getUri() {
        return uri;
    }

    @Override
    public String getJson() {
        return json;
    }

    @Override
    public Map<String, String> getParams() {
        return params;
    }

    public static class Builder {

        private RequestContextMock mock = new RequestContextMock();

        public Builder() {
        }

        public Builder method(String method) {
            mock.method = method;
            return this;
        }

        public Builder uri(String uri) {
            mock.uri = uri;
            return this;
        }

        public Builder json(String json) {
            mock.json = json;
            return this;
        }

        public Builder params(Map<String, String> params) {
            mock.params = params;
            return this;
        }


        public RequestContext build() {
            return mock;
        }
    }
}
