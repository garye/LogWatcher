package org.graysky.eclipse.util;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.w3c.dom.Document;
import org.w3c.dom.Element;

/**
 * Helper functions for dealing with XML documents
 */
public class XmlUtils
{
    public static Element createElementWithText(Document doc, String tagName, String text)
	{
	    Element elem = doc.createElement(tagName);
	    elem.appendChild(doc.createTextNode(text));
	    return elem;
	}
    
    public static Document createDocument() throws ParserConfigurationException
    {
        DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
  
        DocumentBuilder builder = factory.newDocumentBuilder();
        return builder.newDocument();
    }
}