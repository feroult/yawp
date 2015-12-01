package io.yawp.driver.api.testing;

import io.yawp.commons.utils.ServiceLookup;
import io.yawp.repository.Repository;

public class TestHelperFactory {

    public static TestHelper getTestHelper(Repository r) {
        TestHelper helper = lookup(TestHelper.class);

        if (r != null) {
            helper.init(r);
        }
        return helper;
    }

    public static TestHelper getTestHelper() {
        return getTestHelper(null);
    }

    private static <T> T lookup(Class<T> clazz) {
        return ServiceLookup.lookup(clazz);
    }
}
