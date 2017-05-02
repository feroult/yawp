package io.yawp.commons.config;

public class ConfigFile extends YamlFile {

    private Config config;

    public static ConfigFile load() {
        return load("yawp.yml", ConfigFile.class);
    }

    public Config getConfig() {
        return config;
    }

    public void setConfig(Config config) {
        this.config = config;
    }

    public static class Config {
        private String packages;

        public String getPackages() {
            return packages;
        }

        public void setPackages(String packages) {
            this.packages = packages;
        }
    }

}
