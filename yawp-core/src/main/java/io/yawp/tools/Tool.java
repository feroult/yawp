package io.yawp.tools;

import io.yawp.repository.features.Feature;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.Map;

public abstract class Tool extends Feature {

    private static final String NS_PARAM = "ns";

    protected Map<String, String> params;

    protected PrintWriter pw;

    public abstract void execute();

    public void prepareAndExecute() throws IOException {
        init();
        setNamespace();
        execute();
    }

    private void init() throws IOException {
        this.params = requestContext.getParams();
        this.pw = requestContext.resp().getWriter();
    }

    private void setNamespace() {
        String ns = params.get(NS_PARAM);
        if (ns != null) {
            yawp.namespace(ns);
        }
    }

}
