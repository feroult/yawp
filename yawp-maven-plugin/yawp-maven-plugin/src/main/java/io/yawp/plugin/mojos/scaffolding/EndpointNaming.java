package io.yawp.plugin.mojos.scaffolding;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.text.WordUtils;
import org.atteo.evo.inflector.English;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

public class EndpointNaming {

    private Properties customPlurals;

    private String lang;

    private String input;

    private String action;

    private String transformer;

    private String hook;

    private String pipe;

    private String pipeSink;

    public EndpointNaming(String lang, String input) {
        this.lang = lang;
        this.input = input;
        loadCustomPlurals();
    }

    public EndpointNaming(String input) {
        this("java", input);
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

    private String getExtension() {
        if (lang.equalsIgnoreCase("kotlin")) {
            return "kt";
        }
        return lang;
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

    public EndpointNaming pipe(String pipe) {
        this.pipe = pipe;
        return this;
    }

    public EndpointNaming pipeSink(String sink) {
        this.pipeSink = sink;
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
        return String.format("%s/%s.%s", getPackageName(), getName(), getExtension());
    }

    public String getTestName() {
        return String.format("%sTest", getName());
    }

    public String getTestFilename() {
        return String.format("%s/%s.%s", getPackageName(), getTestName(), getExtension());
    }

    public String getInstance() {
        return WordUtils.uncapitalize(getName());
    }

    public String getShieldName() {
        return String.format("%sShield", getName());
    }

    public String getShieldFilename() {
        return String.format("%s/%s.%s", getPackageName(), getShieldName(), getExtension());
    }

    public String getActionName() {
        return String.format("%s%sAction", getName(), capitalize(action));
    }

    public String getActionFilename() {
        return String.format("%s/%s.%s", getPackageName(), getActionName(), getExtension());
    }

    public String getTransformerName() {
        return String.format("%s%sTransformer", getName(), capitalize(transformer));
    }

    public String getTransformerFilename() {
        return String.format("%s/%s.%s", getPackageName(), getTransformerName(), getExtension());
    }

    public String getHookName() {
        return String.format("%s%sHook", getName(), capitalize(hook));
    }

    public String getHookFilename() {
        return String.format("%s/%s.%s", getPackageName(), getHookName(), getExtension());
    }

    public String getPipeName() {
        return String.format("%s%sPipe", getName(), capitalize(pipe));
    }

    public String getPipeSinkName() {
        return capitalize(pipeSink);
    }

    public String getPipeSinkInstance() {
        return WordUtils.uncapitalize(getPipeSinkName());
    }

    public String getPipeFilename() {
        return String.format("%s/%s.%s", getPackageName(), getPipeName(), getExtension());
    }

    public String parsePath(String sourceMain) {
        return sourceMain.replaceAll("\\$\\{lang\\}", lang).replaceAll("\\$\\{ext\\}", getExtension());
    }

    public String whitespaces(String value) {
        int length = Integer.valueOf(value);
        return StringUtils.repeat(' ', length);
    }
}
