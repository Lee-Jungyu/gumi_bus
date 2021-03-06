package com.jglee.busapp.util;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.IOException;
import java.net.URL;

public class HTMLTranslator {

    public static Document getDocumentByUrl(URL arrivalDataUrl) throws IOException {
        Document doc = Jsoup.connect(String.valueOf(arrivalDataUrl)).get();
        return doc;
    }

    public static Elements getElementsByDocsQuery(Document doc, String s) {
        org.jsoup.select.Elements elements = doc.select(s);
        return elements;
    }

    public static Element getElementByElementSelect(Element element, String select) {
        org.jsoup.nodes.Element selectedElement = element.select(select).get(0);
        return selectedElement;
    }
}
