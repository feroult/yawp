package io.yawp.driver.postgresql.configuration;

import io.yawp.commons.utils.ResourceFinder;
import org.junit.Before;
import org.junit.Test;

import java.io.IOException;
import java.net.URL;

import static org.junit.Assert.assertEquals;

public class JettyConfigurationTest {

    private JettyConfiguration config;

    @Before
    public void setUp() throws IOException {
        config = new JettyConfiguration(getPath());
    }

    @Test
    public void testLoad()  {
        DataSourceInfo dsTest = config.getDatasourceInfo("test");
        assertEquals("jdbc/yawp_test", dsTest.getName());
        assertEquals("org.postgresql.Driver", dsTest.getDriverClassName());
        assertEquals("jdbc:postgresql://localhost/yawp_pg_driver_test", dsTest.getUrl());
    }

    @Test
    public void testInitDatasource() throws IOException {
        DataSourceInfo dsTest = config.getDatasourceInfo("test");
        DataSourceInfo dsInitForTest = dsTest.getInitDatasource();
        assertEquals("jdbc/_yawp_init", dsInitForTest.getName());
        assertEquals("org.postgresql.Driver", dsInitForTest.getDriverClassName());
        assertEquals("jdbc:postgresql://localhost/template1", dsInitForTest.getUrl());

        DataSourceInfo dsDevelopment = config.getDatasourceInfo("development");
        DataSourceInfo dsInitForDevelopment = dsDevelopment.getInitDatasource();
        assertEquals("jdbc/_yawp_init", dsInitForDevelopment.getName());
        assertEquals("org.postgresql.Driver", dsInitForDevelopment.getDriverClassName());
        assertEquals("jdbc:postgresql://127.0.0.1:5432/template1", dsInitForDevelopment.getUrl());
    }

    private String getPath() throws IOException {
        URL url = new ResourceFinder().find("configuration/jetty-env-test.xml");
        return url.getFile();
    }

}
