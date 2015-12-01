package io.yawp.driver.mock;

import io.yawp.driver.api.HelpersDriver;

public class MockHelpersDriver implements HelpersDriver {

    @Override
    public void deleteAll() {
        MockStore.clear();
    }

    @Override
    public void sync() {
    }

}