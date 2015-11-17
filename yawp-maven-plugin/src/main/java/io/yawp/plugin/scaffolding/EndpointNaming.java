package io.yawp.plugin.scaffolding;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

import org.apache.commons.lang.WordUtils;
import org.atteo.evo.inflector.English;

public class EndpointNaming {

    private Properties customPlurals;

    private String input;

    private String action;

    private String transformer;

    private String hook;

    public EndpointNaming(String input) {
        loadCustomPlurals();
        this.input = input;
    }

    private void loadCustomPlurals() {
        try {
            customPlurals = new Properties();
            InputStream in = getClass().getResourceAsStream("/custom_plurals.properties");
            customPlurals.load(in);
            in.close();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private String plural(String word) {
        String key = word.toLowerCase();
        if (customPlurals.containsKey(key)) {
            return customPlurals.getProperty(key);
        }
        return English.plural(word);
    }

    private String capitalize(String word) {
        return WordUtils.capitalize(word, new char[]{'_'}).replaceAll("_", "");
    }

    public EndpointNaming action(String action) {
        this.action = action;
        return this;
    }

    public EndpointNaming transformer(String transformer) {
        this.transformer = transformer;
        return this;
    }

    public EndpointNaming hook(String hook) {
        this.hook = hook;
        return this;
    }

    public String getName() {
        return capitalize(input);
    }

    public String getPackageName() {
        return getName().toLowerCase();
    }

    public String getPath() {
        String endpointPath = getName().replaceAll("(.)(\\p{Lu})", "$1-$2").toLowerCase();
        return plural(endpointPath);
    }

    public String getFilename() {
        return String.format("%s/%s.java", getPackageName(), getName());
    }

    public String getTestName() {
        return String.format("%sTest", getName());
    }

    public String getTestFilename() {
        return String.format("%s/%s.java", getPackageName(), getTestName());
    }

    public String getInstance() {
        return WordUtils.uncapitalize(getName());
    }

    public String getShieldName() {
        return String.format("%sShield", getName());
    }

    public String getShieldFilename() {
        return String.format("%s/%s.java", getPackageName(), getShieldName());
    }

    public String getActionName() {
        return String.format("%s%sAction", getName(), capitalize(action));
    }

    public String getActionFilename() {
        return String.format("%s/%s.java", getPackageName(), getActionName());
    }

    public String getTransformerName() {
        return String.format("%s%sTransformer", getName(), capitalize(transformer));
    }

    public String getTransformerFilename() {
        return String.format("%s/%s.java", getPackageName(), getTransformerName());
    }

    public String getHookName() {
        return String.format("%s%sHook", getName(), capitalize(hook));
    }

    public String getHookFilename() {
        return String.format("%s/%s.java", getPackageName(), getHookName());
    }
}
