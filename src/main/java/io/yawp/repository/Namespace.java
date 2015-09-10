package io.yawp.repository;

import io.yawp.repository.annotations.Global;
import io.yawp.repository.driver.api.NamespaceDriver;

public class Namespace {

	public static String GLOBAL = "";

	private NamespaceDriver driver;

	private String ns;

	private String previousNs;

	public Namespace(NamespaceDriver driver) {
		this.driver = driver;
	}

	public Namespace(String ns, NamespaceDriver driver) {
		this.ns = ns;
		this.driver = driver;
	}

	public void set(Class<?> clazz) {
		previousNs = driver.get();
		configureNs(clazz.isAnnotationPresent(Global.class) ? GLOBAL : ns);
	}

	private void configureNs(String ns) {
		if (ns != null) {
			driver.set(ns);
		}
	}

	public void reset() {
		driver.set(previousNs);
	}

	public void setNs(String ns) {
		this.ns = ns;
	}

	public String getNs() {
		return ns;
	}

}
