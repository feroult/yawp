package io.yawp.repository.driver.appengine;

import io.yawp.repository.driver.api.NamespaceDriver;

import com.google.appengine.api.NamespaceManager;

public class AppengineNamespaceDriver implements NamespaceDriver {

	@Override
	public String get() {
		return NamespaceManager.get();
	}

	@Override
	public void set(String ns) {
		NamespaceManager.set(ns);
	}

}
