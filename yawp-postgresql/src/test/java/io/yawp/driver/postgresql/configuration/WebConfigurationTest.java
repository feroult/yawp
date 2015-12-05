package io.yawp.driver.postgresql.configuration;

import io.yawp.commons.utils.ResourceFinder;
import org.junit.Test;

import java.io.IOException;
import java.net.URL;

import static org.junit.Assert.assertEquals;

public class WebConfigurationTest {

    @Test
    public void testLoad() throws IOException {
        WebConfiguration config = new WebConfiguration(getPath());
        assertEquals("yawpapp", config.getPackagePrefix());
    }

    private String getPath() throws IOException {
        URL url = new ResourceFinder().find("configuration/web-test.xml");
        return url.getFile();
    }

}
