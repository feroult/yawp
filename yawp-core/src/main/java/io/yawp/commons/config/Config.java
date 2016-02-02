package io.yawp.commons.config;

import org.yaml.snakeyaml.Yaml;
import org.yaml.snakeyaml.constructor.Constructor;

import java.io.InputStream;
import java.util.Map;

public class Config {

    private static final String DEFAULT_CONFIG = "/yawp.yml";

    private static final String DEFAULT_KEY = "default";

    private Map<String, FeaturesConfig> features;

    public static Config load() {
        return loadYamlFrom(stream(DEFAULT_CONFIG));
    }

    private static InputStream stream(String uri) {
        return Config.class.getResourceAsStream(uri);
    }

    private static Config loadYamlFrom(InputStream stream) {
        Constructor constructor = new Constructor(Config.class);
        Yaml yaml = new Yaml(constructor);
        return (Config) yaml.load(stream);
    }

    public Map<String, FeaturesConfig> getFeatures() {
        return features;
    }

    public void setFeatures(Map<String, FeaturesConfig> features) {
        this.features = features;
    }

    public FeaturesConfig getDefaultFeatures() {
        return features.get(DEFAULT_KEY);
    }

}
