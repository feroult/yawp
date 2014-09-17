package io.yawp.repository;

import io.yawp.repository.annotations.Global;

import com.google.appengine.api.NamespaceManager;

public class Namespace {

	public static String GLOBAL = "";

	protected String ns;

	protected String previousNs;

	public Namespace() {
	}

	public Namespace(String ns) {
		this.ns = ns;
	}

	public void set(Class<?> clazz) {
		previousNs = NamespaceManager.get();
		configureNs(clazz.isAnnotationPresent(Global.class) ? GLOBAL : ns);
	}

	private void configureNs(String ns) {
		if (ns != null) {
			NamespaceManager.set(ns);
		}
	}

	public void reset() {
		NamespaceManager.set(previousNs);
	}

	public void setNs(String ns) {
		this.ns = ns;
	}

	public String getNs() {
		return ns;
	}

}
