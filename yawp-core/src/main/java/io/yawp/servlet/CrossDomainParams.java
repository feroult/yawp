package io.yawp.servlet;

import java.util.List;

/**
 * Created by walidsabihi on 25/08/2016.
 */
public class CrossDomainParams {
    List<String> origins = null;
    List<String> headers = null;
    List<String> methods = null;

    public List<String> getOrigins() {
        return origins;
    }

    public void setOrigins(List<String> origins) {
        this.origins = origins;
    }

    public List<String> getHeaders() {
        return headers;
    }

    public void setHeaders(List<String> headers) {
        this.headers = headers;
    }

    public List<String> getMethods() {
        return methods;
    }

    public void setMethods(List<String> methods) {
        this.methods = methods;
    }
}
