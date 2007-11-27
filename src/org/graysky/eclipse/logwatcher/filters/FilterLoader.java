package org.graysky.eclipse.logwatcher.filters;

import java.io.Reader;
import java.util.Vector;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.apache.oro.text.regex.MalformedPatternException;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.widgets.Display;
import org.graysky.eclipse.logwatcher.LogwatcherPlugin;
import org.w3c.dom.Document;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;

/**
 * Loads a filter definition from XML.
 */
public class FilterLoader
{
    public Vector loadFilters(Reader r) throws Exception
    {
        Document doc = getDocument(r);
        Vector filters = loadFilters(doc);
        return filters;
    }

    public Vector loadFilters(Document doc)
    {
        Vector filters = new Vector();
        NodeList filterNodes = doc.getElementsByTagName("filter");
        for (int i = 0; i < filterNodes.getLength(); i++) {
            Node node = filterNodes.item(i);
            filters.add(loadFilter(node));
        }
        return filters;
    }

    public Filter loadFilter(Node filterNode)
    {
        Filter f = new Filter();
        NodeList children = filterNode.getChildNodes();
        String pattern = "";
        boolean caseSensitive = false;
        for (int i = 0; i < children.getLength(); i++) {
            Node node = children.item(i);
            String name = node.getNodeName();
            if (name.equals("pattern")) {
                pattern = node.getFirstChild().getNodeValue();
            }
            else if (name.equals("caseSensitive")) {
                caseSensitive = new Boolean(node.getFirstChild().getNodeValue()).booleanValue();
            }
            else if (name.equals("description")) {
                f.setDescription(node.getFirstChild().getNodeValue());
            }
            else if (name.equals("contains")) {
                f.setContains(new Boolean(node.getFirstChild().getNodeValue()).booleanValue());
            }
            else if (name.equals("action")) {
                f.addAction(loadAction(node));
            }
        }
        try {
            f.setPattern(pattern, caseSensitive);
        }
        catch (MalformedPatternException ignore) {
        }
        return f;
    }

    protected FilterAction loadAction(Node node)
    {
        NamedNodeMap attrs = node.getAttributes();
        Node type = attrs.getNamedItem("type");
        if (type.getNodeValue().equals("highlight")) {
            NodeList children = node.getChildNodes();
            int red = 0;
            int green = 0;
            int blue = 0;
            for (int i = 0; i < children.getLength(); i++) {
                Node colorNode = children.item(i);
                String name = colorNode.getNodeName();
                if (name.equals("red")) {
                    red = Integer.parseInt(colorNode.getFirstChild().getNodeValue());
                }
                else if (name.equals("green")) {
                    green = Integer.parseInt(colorNode.getFirstChild().getNodeValue());
                }
                else if (name.equals("blue")) {
                    blue = Integer.parseInt(colorNode.getFirstChild().getNodeValue());
                }
            }

            // Is this the best way to get a Display?
            Display d = LogwatcherPlugin.getDefault().getWorkbench().getActiveWorkbenchWindow().getShell().getDisplay();
            Color c = new Color(d, red, green, blue);
            return new HighlightAction(c);
        }
        else if (type.getNodeValue().equals("ignore")) {
            return new IgnoreAction();
        }
        else if (type.getNodeValue().equals("task")) {
            NodeList children = node.getChildNodes();
            String desc = "";
            int priority = 0;
            for (int i = 0; i < children.getLength(); i++) {
                Node n = children.item(i);
                String name = n.getNodeName();
                if (name.equals("taskDescription")) {
                    desc = n.getFirstChild().getNodeValue();
                }
                else if (name.equals("priority")) {
                    priority = Integer.parseInt(n.getFirstChild().getNodeValue());
                }
            }
            return new AddTaskAction(desc, priority, LogwatcherPlugin.getWorkspace().getRoot());
        }
        else {
            LogwatcherPlugin.getDefault().logError("Invalid action type: " + type, null);
            return null;
        }
    }

    protected Document getDocument(Reader r) throws Exception
    {
        Document document;
        try {
            DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
            DocumentBuilder parser = factory.newDocumentBuilder();
            document = parser.parse(new InputSource(r));
            return document;
        }
        catch (Exception e) {
            throw e;
        }
    }
}
