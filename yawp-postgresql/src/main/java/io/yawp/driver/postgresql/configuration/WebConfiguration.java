package io.yawp.driver.postgresql.configuration;

import io.yawp.servlet.EndpointServlet;

import java.util.List;

public class WebConfiguration {

    private String path;

    private String packagePrefix;

    public WebConfiguration(String path) {
        this.path = path;
        load();
    }

    private void load() {
        XmlLoader xml = new XmlLoader(path);

        List<XmlLoader> servlets = xml.find("/web-app/servlet");

        for(XmlLoader servletXml : servlets) {
            if(!isYawpServlet(servletXml)) {
                continue;
            }

            List<XmlLoader> initParams = servletXml.find("init-param");

            for(XmlLoader initParamXml : initParams) {
                if(!isPackagePrefixParam(initParamXml)) {
                    continue;
                }

                XmlLoader paramValueXml = findFirst(initParamXml, "param-value");

                if(paramValueXml == null) {
                    continue;
                }

                this.packagePrefix = paramValueXml.getTextContent();
                return;
            }
        }
    }

    private boolean isPackagePrefixParam(XmlLoader initParamXml) {
        XmlLoader paramNameXml = findFirst(initParamXml, "param-name");

        if(paramNameXml == null) {
            return false;
        }

        if(!paramNameXml.getTextContent().equals("packagePrefix")) {
            return false;
        }

        return true;
    }

    private boolean isYawpServlet(XmlLoader servletXml) {
        XmlLoader servletClassXml = findFirst(servletXml, "servlet-class");

        if(servletClassXml == null) {
            return false;
        }

        if(!servletClassXml.getTextContent().equals(EndpointServlet.class.getName())) {
            return false;
        }
        return true;
    }

    private XmlLoader findFirst(XmlLoader servletXml, String xpath) {
        List<XmlLoader> servletClazzes = servletXml.find(xpath);

        XmlLoader servlet = null;

        if(servletClazzes.size() != 0) {
            servlet = servletClazzes.get(0);
        }
        return servlet;
    }

    public String getPackagePrefix() {
        return packagePrefix;
    }
}
