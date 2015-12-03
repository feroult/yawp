package io.yawp.driver.postgresql.configuration;

import org.junit.Test;

import java.util.List;

import static org.junit.Assert.assertEquals;

public class XmlLoaderTest {

    @Test
    public void testLoadAndFind() {
        XmlLoader xml = new XmlLoader("configuration/jetty-env-test.xml");
        List<XmlLoader> result = xml.find("/Configure/New[starts-with(@id, 'yawp')]");
        assertEquals(3, result.size());
    }

    @Test
    public void testFindAfterFind() {
        XmlLoader xml = new XmlLoader("configuration/jetty-env-test.xml");
        List<XmlLoader> result = xml.find("/Configure/New[starts-with(@id, 'yawp')]");
        XmlLoader datasourceXml = result.get(0);
        assertEquals("java:comp/env/jdbc/yawp_test", datasourceXml.find("Arg").get(1).getTextContent());
        assertEquals("org.postgresql.Driver", datasourceXml.find("Arg/New/Set[@name='driverClassName']").get(0).getTextContent());
        assertEquals("jdbc:postgresql://localhost/yawp_test", datasourceXml.find("Arg/New/Set[@name='url']").get(0).getTextContent());
    }
}
