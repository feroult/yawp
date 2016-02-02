package io.yawp.commons.config;

import org.yaml.snakeyaml.Yaml;
import org.yaml.snakeyaml.constructor.Constructor;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.util.Map;

public class Config {

    private static final String DEFAULT_CONFIG = "/yawp.yml";

    private static final String DEFAULT_KEY = "default";

    private Map<String, FeatureConfig> features;

    private Map<String, RepositoryConfig> repositories;

    public static Config load() {
        return loadYamlFrom(file(DEFAULT_CONFIG));
    }

    private static String file(String uri) {
        return Config.class.getResource(uri).getFile();
    }

    private static Config loadYamlFrom(String file) {
        try {
            Constructor constructor = new Constructor(Config.class);
            Yaml yaml = new Yaml(constructor);
            return (Config) yaml.load(new FileInputStream(file));
        } catch (FileNotFoundException e) {
            throw new RuntimeException(e);
        }
    }

    public Map<String, FeatureConfig> getFeatures() {
        return features;
    }

    public void setFeatures(Map<String, FeatureConfig> features) {
        this.features = features;
    }

    public Map<String, RepositoryConfig> getRepositories() {
        return repositories;
    }

    public void setRepositories(Map<String, RepositoryConfig> repositories) {
        this.repositories = repositories;
    }

    public FeatureConfig getDefaultFeatures() {
        return features.get(DEFAULT_KEY);
    }

    public RepositoryConfig getDefaultRepository() {
        return repositories.get(DEFAULT_KEY);
    }

    public FeatureConfig getDefaultRepositoryFeatures() {
        return features.get(repositories.get(DEFAULT_KEY).getFeatures());
    }
}
