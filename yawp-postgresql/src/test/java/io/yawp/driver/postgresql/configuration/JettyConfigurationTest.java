package io.yawp.driver.postgresql.configuration;

import io.yawp.commons.utils.ResourceFinder;
import org.junit.Test;

import java.io.IOException;
import java.net.URL;

import static org.junit.Assert.assertEquals;

public class JettyConfigurationTest {

    @Test
    public void testLoad() throws IOException {
        JettyConfiguration config = new JettyConfiguration(getPath());
        DataSourceInfo ds = config.getDatasourceInfo("test");
        assertEquals("jdbc/yawp_test", ds.getName());
        assertEquals("org.postgresql.Driver", ds.getDriverClassName());
        assertEquals("jdbc:postgresql://localhost/yawp_test", ds.getUrl());
    }

    private String getPath() throws IOException {
        URL url = new ResourceFinder().find("configuration/jetty-env-test.xml");
        return url.getFile();
    }

}
