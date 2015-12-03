package io.yawp.driver.postgresql.configuration;

import io.yawp.commons.utils.ResourceFinder;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathExpressionException;
import javax.xml.xpath.XPathFactory;
import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

public class XmlLoader {

    private String resourceUri;

    private Node item;

    public XmlLoader(String resourceUri) {
        this.resourceUri = resourceUri;
        load();
    }

    public XmlLoader(Node item) {
        this.item = item;
    }

    public void load() {
        try {
            URL url = new ResourceFinder().find(resourceUri);
            DocumentBuilderFactory dbf = createDocumentFactory();
            DocumentBuilder db = dbf.newDocumentBuilder();
            item = db.parse(url.getFile()).getDocumentElement();
        } catch (ParserConfigurationException | SAXException | IOException e) {
            throw new RuntimeException(e);
        }
    }

    private DocumentBuilderFactory createDocumentFactory() throws ParserConfigurationException {
        DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
        dbf.setValidating(false);
        dbf.setNamespaceAware(true);
        dbf.setFeature("http://xml.org/sax/features/namespaces", false);
        dbf.setFeature("http://xml.org/sax/features/validation", false);
        dbf.setFeature("http://apache.org/xml/features/nonvalidating/load-dtd-grammar", false);
        dbf.setFeature("http://apache.org/xml/features/nonvalidating/load-external-dtd", false);
        return dbf;
    }

    public List<XmlLoader> find(String xpath) {
        try {
            XPath xPath = XPathFactory.newInstance().newXPath();
            NodeList nodes = (NodeList) xPath.evaluate(xpath, item, XPathConstants.NODESET);
            List<XmlLoader> result = new ArrayList<XmlLoader>();
            for (int i = 0; i < nodes.getLength(); i++) {
                result.add(new XmlLoader(nodes.item(i)));
            }
            return result;
        } catch (XPathExpressionException e) {
            throw new RuntimeException(e);
        }
    }

    public String getTextContent() {
        return item.getTextContent();
    }

    public String getAttributeText(String attr) {
        return item.getAttributes().getNamedItem(attr).getNodeValue();
    }
}
