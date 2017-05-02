package io.yawp.commons.config;

import org.yaml.snakeyaml.Yaml;
import org.yaml.snakeyaml.constructor.Constructor;

import java.io.InputStream;

public class YamlFile {

    public static <T> T load(String uri, Class<T> clazz) {
        return loadYamlFrom(stream(uri), clazz);
    }

    private static InputStream stream(String uri) {
        return Thread.currentThread().getContextClassLoader().getResourceAsStream(uri);
    }

    private static <T> T loadYamlFrom(InputStream stream, Class<T> clazz) {
        Constructor constructor = new Constructor(ConfigFile.class);
        Yaml yaml = new Yaml(constructor);
        return (T) yaml.load(stream);
    }

}
