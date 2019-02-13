package com.github.jacobcn.antkits;

import org.w3c.dom.DOMImplementation;
import org.w3c.dom.Document;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.ls.DOMImplementationLS;
import org.w3c.dom.ls.LSOutput;
import org.w3c.dom.ls.LSSerializer;
import org.xml.sax.SAXException;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathExpressionException;
import javax.xml.xpath.XPathFactory;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.Map;

public class SimpleXmlUpdater {

    public static void main(String[] args)
            throws XPathExpressionException, ParserConfigurationException, IOException, SAXException {

        //input
        String srcFilename = "/Users/jacobzhao/active/camps-session-management-service/config/cxf.xml";
        String outFilename = "/Users/jacobzhao/Desktop/test/cxf.xml";

        String attrNodeXpath = "/beans/conduit/client";
        Map<String, String> attrMap = new HashMap<>();
        attrMap.put("ConnectionTimeout", "1000002");
        attrMap.put("ReceiveTimeout", "3000000");
        Map<String, Map<String, String>> attrs = new HashMap<>();
        attrs.put(attrNodeXpath, attrMap);
        updateAttrs(srcFilename, outFilename, attrs);
    }

    public static void updateAttrs(String srcFilename, String outFilename, Map<String, Map<String, String>> attrs) throws ParserConfigurationException, SAXException, IOException, XPathExpressionException {
        // create xml doc
        Document doc = parseXmlDoc(srcFilename);

        //update xml node attr
        updateAttrs(doc, attrs);

        // create new file is not exists
        createNewFile(outFilename);

        //write xml file
        writeXmlFile(doc, outFilename);
    }

    private static void updateAttrs(Document doc, Map<String, Map<String, String>> attrs) throws XPathExpressionException {
        for (Map.Entry<String, Map<String, String>> attr : attrs.entrySet()) {
            String attrNodeXpath = attr.getKey();
            Map<String, String> attrMap = attr.getValue();
            updateNodeAttr(doc, attrNodeXpath, attrMap);
        }
    }

    private static void writeXmlFile(Document doc, String outfilename) throws IOException {
        File outfile = new File(outfilename);

        String xmlEncoding = doc.getXmlEncoding();
        DOMImplementation impl = doc.getImplementation();
        DOMImplementationLS implLS = (DOMImplementationLS) impl.getFeature("LS", "3.0");
        LSSerializer ser = implLS.createLSSerializer();
        ser.getDomConfig().setParameter("format-pretty-print", true);

        LSOutput out = implLS.createLSOutput();
        out.setEncoding(xmlEncoding);
        out.setByteStream(Files.newOutputStream(Paths.get(outfile.getAbsolutePath())));
        ser.write(doc, out);
    }

    private static File createNewFile(String outfilename) throws IOException {
        File outFile = new File(outfilename);
        File outDir = outFile.getParentFile();
        if (!outDir.exists()) {
            outDir.mkdir();
        }
        if (!outFile.exists()) {
            outFile.createNewFile();
        }
        return outFile;
    }

    private static void updateNodeAttr(Document doc, String nodeXpath, Map<String, String> nodeAttrMap) throws XPathExpressionException {
        XPathFactory xPathFactory = XPathFactory.newInstance();
        XPath xpath = xPathFactory.newXPath();
        Node node = (Node) xpath.evaluate(nodeXpath, doc, XPathConstants.NODE);
        NamedNodeMap attrs = node.getAttributes();

        for (Map.Entry<String, String> attr : nodeAttrMap.entrySet()) {
            attrs.getNamedItem(attr.getKey()).setNodeValue(attr.getValue());
        }
    }

    private static Document parseXmlDoc(String pathname) throws ParserConfigurationException, SAXException, IOException {
        DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
        DocumentBuilder builder = factory.newDocumentBuilder();
        File file = new File(pathname);
        return builder.parse(file);
    }
}
