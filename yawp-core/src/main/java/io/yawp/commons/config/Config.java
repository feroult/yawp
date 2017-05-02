package io.yawp.commons.config;

import org.yaml.snakeyaml.Yaml;
import org.yaml.snakeyaml.constructor.Constructor;

import java.io.InputStream;

public class Config {

    private static final String DEFAULT_CONFIG = "yawp.yml";

    private FeaturesConfig config;

    public static Config load() {
        return loadYamlFrom(stream(DEFAULT_CONFIG));
    }

    private static InputStream stream(String uri) {
        return Thread.currentThread().getContextClassLoader().getResourceAsStream(uri);
    }

    private static Config loadYamlFrom(InputStream stream) {
        Constructor constructor = new Constructor(Config.class);
        Yaml yaml = new Yaml(constructor);
        return (Config) yaml.load(stream);
    }

    public FeaturesConfig getConfig() {
        return config;
    }

    public void setConfig(FeaturesConfig config) {
        this.config = config;
    }

}
