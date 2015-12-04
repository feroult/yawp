package io.yawp.driver.postgresql.configuration;

import org.junit.Test;

import java.util.List;

import static org.junit.Assert.assertEquals;

public class XmlLoaderTest {

    @Test
    public void testLoad() {
        List<XmlLoader> envs = loadFromXml();
        assertEquals(3, envs.size());
    }

    @Test
    public void testLoadAndExtract() {
        List<XmlLoader> envs = loadFromXml();
        XmlLoader datasourceXml = envs.get(0);
        assertEquals("yawp_test", datasourceXml.getAttributeText("id"));
        assertEquals("java:comp/env/jdbc/yawp_test", datasourceXml.find("Arg").get(1).getTextContent());
        assertEquals("org.postgresql.Driver", datasourceXml.find("Arg/New/Set[@name='driverClassName']").get(0).getTextContent());
        assertEquals("jdbc:postgresql://localhost/yawp_test", datasourceXml.find("Arg/New/Set[@name='url']").get(0).getTextContent());
    }

    private List<XmlLoader> loadFromXml() {
        XmlLoader xml = new XmlLoader("configuration/jetty-env-test.xml");
        return xml.find("/Configure/New[starts-with(@id, 'yawp')]");
    }
}
