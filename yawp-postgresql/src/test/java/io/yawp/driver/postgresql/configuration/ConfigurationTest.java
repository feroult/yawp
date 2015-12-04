package io.yawp.driver.postgresql.configuration;

import org.junit.Test;

import static org.junit.Assert.assertEquals;

public class ConfigurationTest {

    @Test
    public void testLoad() {
        Configuration config = new Configuration("configuration/jetty-env-test.xml");
        assertEquals("test", config.getEnv());
        DataSourceInfo ds = config.getDatasourceInfo("test");
        assertEquals("jdbc/yawp_test", ds.getName());
        assertEquals("org.postgresql.Driver", ds.getDriverClassName());
        assertEquals("jdbc:postgresql://localhost/yawp_test", ds.getUrl());
    }

}
