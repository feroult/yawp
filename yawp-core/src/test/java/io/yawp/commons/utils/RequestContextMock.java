package io.yawp.commons.utils;

import io.yawp.commons.http.RequestContext;

import java.util.Map;

public class RequestContextMock extends RequestContext {

    public static class Builder {

        private RequestContextMock mock = new RequestContextMock();

        public Builder() {
        }

        public Builder method(String method) {
            mock.method = method;
            return this;
        }

        public Builder optionsRequestMethod(String optionsRequestMethod) {
            mock.optionsRequestMethod = optionsRequestMethod;
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
