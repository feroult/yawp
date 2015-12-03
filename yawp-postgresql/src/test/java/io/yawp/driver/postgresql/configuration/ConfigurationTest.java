package io.yawp.driver.postgresql.configuration;

import org.junit.Test;

import static org.junit.Assert.assertEquals;

public class ConfigurationTest {

    @Test
    public void testConfiguration() {
        Configuration config = new Configuration("configuration/jetty-env-test.xml");
        assertEquals("test", config.getEnv());
        DataSourceInfo dsInfo = config.getDatasourceInfo("test");
        assertEquals("jdbc/yawp_test", dsInfo.getName());
        assertEquals("org.postgresql.Driver", dsInfo.getDriverClassName());
        assertEquals("jdbc:postgresql://localhost/yawp_test", dsInfo.getUrl());
    }

}
