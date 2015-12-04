package io.yawp.driver.postgresql.configuration;

import org.junit.Test;

public class InitialContextSetupTest {

    @Test
    public void testSetup() {

        InitialContextSetup.configure("configuration/jetty-test.xml");

    }

}
