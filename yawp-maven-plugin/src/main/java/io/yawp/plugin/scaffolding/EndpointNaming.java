package io.yawp.plugin.scaffolding;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

import org.apache.commons.lang.WordUtils;
import org.atteo.evo.inflector.English;

public class EndpointNaming {

	private Properties customPlurals;

	private String input;

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

	public String getName() {
		return WordUtils.capitalize(input, new char[] { '_' }).replaceAll("_", "");
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

}
