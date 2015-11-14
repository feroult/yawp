package io.yawp.plugin.scaffolding;

import org.apache.commons.lang.WordUtils;

public class EndpointNaming {

	private String input;

	private String name;

	private String packageName;

	public EndpointNaming(String input) {
		this.input = input;
		this.name = endpointName();
		this.packageName = endpointPackageName();
	}

	private String endpointName() {
		return WordUtils.capitalize(input, new char[] { '_' }).replaceAll("_", "");
	}

	private String endpointPackageName() {
		return name.toLowerCase();
	}

	public String getName() {
		return name;
	}

	public String getPackageName() {
		return packageName;
	}
}
