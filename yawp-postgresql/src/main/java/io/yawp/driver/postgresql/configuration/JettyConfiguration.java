package io.yawp.driver.postgresql.configuration;

import io.yawp.commons.utils.Environment;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class JettyConfiguration {

    private static final String XPATH_ENVS = "/Configure/New[starts-with(@id, 'yawp')]";

    private static final String XPATH_NAME = "Arg";

    private static final String XPATH_DRIVER_CLASS_NAME = "Arg/New/Set[@name='driverClassName']";

    private static final String XPATH_URL = "Arg/New/Set[@name='url']";

    private static final int ENV_PREFIX_LENGTH = "yawp_".length();

    private static final int NAME_PREFIX_LENGTH = "java:comp/env/".length();

    private String path;

    private Map<String, DataSourceInfo> envs;

    protected static JettyConfiguration instance;

    protected JettyConfiguration(String path) {
        this.path = path;
        load();
    }

    public static JettyConfiguration load(String path) {
        instance = new JettyConfiguration(path);
        return instance;
    }

    public static JettyConfiguration get() {
        return instance;
    }

    private void load() {
        envs = new HashMap<String, DataSourceInfo>();

        XmlLoader xml = new XmlLoader(path);
        List<XmlLoader> allEnvsXml = xml.find(XPATH_ENVS);
        for (XmlLoader envXml : allEnvsXml) {
            DataSourceInfo ds = new DataSourceInfo();

            ds.setName(parseName(envXml));
            ds.setDriverClassName(parseDriverClassName(envXml));
            ds.setUrl(parseUrl(envXml));

            envs.put(parseEnv(envXml), ds);
        }
    }

    private String parseEnv(XmlLoader envXml) {
        return envXml.getAttributeText("id").substring(ENV_PREFIX_LENGTH);
    }

    private String parseUrl(XmlLoader envXml) {
        return envXml.find(XPATH_URL).get(0).getTextContent();
    }

    private String parseDriverClassName(XmlLoader envXml) {
        return envXml.find(XPATH_DRIVER_CLASS_NAME).get(0).getTextContent();
    }

    private String parseName(XmlLoader envXml) {
        String name = envXml.find(XPATH_NAME).get(1).getTextContent();
        return name.substring(NAME_PREFIX_LENGTH);
    }

    public DataSourceInfo getDatasourceInfo(String env) {
        return envs.get(env);
    }

    public DataSourceInfo getDatasourceInfo() {
        return envs.get(Environment.getOrDefault());
    }
}
