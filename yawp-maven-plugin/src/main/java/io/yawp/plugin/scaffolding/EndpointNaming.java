package io.yawp.plugin.scaffolding;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

import org.apache.commons.lang.WordUtils;
import org.atteo.evo.inflector.English;

public class EndpointNaming {

	private String name;

	private String packageName;

	private String path;

	private Properties customPlurals;

	public EndpointNaming(String input) {
		loadCustomPlurals();
		this.name = endpointName(input);
		this.packageName = endpointPackageName();
		this.path = endpointPath();
	}

	private String endpointName(String input) {
		return WordUtils.capitalize(input, new char[] { '_' }).replaceAll("_", "");
	}

	private String endpointPackageName() {
		return name.toLowerCase();
	}

	private String endpointPath() {
		String endpointPath = name.replaceAll("(.)(\\p{Lu})", "$1-$2").toLowerCase();
		return plural(endpointPath);
	}

	public String getName() {
		return name;
	}

	public String getPackageName() {
		return packageName;
	}

	public String getPath() {
		return path;
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
}
