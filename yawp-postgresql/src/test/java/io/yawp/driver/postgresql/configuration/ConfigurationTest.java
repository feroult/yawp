package io.yawp.driver.postgresql.configuration;

import org.junit.Test;

import static org.junit.Assert.assertEquals;

public class ConfigurationTest {

    @Test
    public void testLoad() {
        Configuration config = new Configuration("jetty-env-test.xml");
        assertEquals("test", config.getEnv());
    }

}
