package io.yawp.driver.mock;

import io.yawp.driver.api.NamespaceDriver;

public class MockNamespaceDriver implements NamespaceDriver {

	@Override
	public String get() {
		return MockStore.getNamespace();
	}

	@Override
	public void set(String ns) {
		MockStore.setNamespace(ns);
	}

}
