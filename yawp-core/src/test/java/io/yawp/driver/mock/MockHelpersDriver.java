package io.yawp.driver.mock;

import io.yawp.driver.api.HelpersDriver;

import java.util.List;

public class MockHelpersDriver implements HelpersDriver {

    @Override
    public void deleteAll() {
        MockStore.clear();
    }

    @Override
    public void deleteAll(String namespace) {
        MockStore.clear(namespace);
    }

    @Override
    public List<String> listNamespaces() {
        return MockStore.namespaces();
    }

    @Override
    public void sync() {
    }

}