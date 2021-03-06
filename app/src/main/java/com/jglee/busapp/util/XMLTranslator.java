package com.jglee.busapp.util;

import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

import java.io.IOException;
import java.io.StringReader;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

public class XMLTranslator {

    public static Document getXMLDocument(String xml) throws ParserConfigurationException, IOException, SAXException {
        DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
        factory.setNamespaceAware(true);
        DocumentBuilder builder = factory.newDocumentBuilder();

        Document document = builder.parse(new InputSource(new StringReader(xml)));

        return document;
    }

    public static Document nodeToDocument(Node node) throws ParserConfigurationException {
        DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
        factory.setNamespaceAware(true);
        DocumentBuilder builder = factory.newDocumentBuilder();

        Document document = builder.newDocument();
        Node importedDocument = document.importNode(node, true);
        document.appendChild(importedDocument);

        return document;
    }

    public static String getStringValue(Document document, String tagName) {

        NodeList itemList = document.getElementsByTagName(tagName);
        Node item = itemList.item(0);
        String string = item.getFirstChild().getNodeValue();

        return string;
    }
}
