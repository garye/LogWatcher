/*
 * Created on Mar 6, 2004
 *
 * To change the template for this generated file go to
 * Window - Preferences - Java - Code Generation - Code and Comments
 */
package org.graysky.eclipse.logwatcher.watchers;

import java.io.File;
import java.io.Reader;
import java.util.Vector;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.graysky.eclipse.logwatcher.filters.FilterLoader;
import org.graysky.eclipse.logwatcher.views.LogWatcherView;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;


/**
 * Loader for watchers stored in XML. Also starts each watcher in the view.
 */
public class WatcherLoader
{
	FilterLoader filterLoader = new FilterLoader();
	private LogWatcherView m_view;
	
	public WatcherLoader(LogWatcherView view)
	{
	    m_view = view;
	}
	
	public void loadWatchers(Reader r) throws Exception
	{
		org.w3c.dom.Document doc = createDocument(r);
		loadWatchers(doc);
	}

	public void loadWatchers(org.w3c.dom.Document doc)
	{
		NodeList watcherNodes = doc.getElementsByTagName("watcher");
		for (int i = 0; i < watcherNodes.getLength(); i++) {
			Node node = watcherNodes.item(i);
			loadWatcher(node);
		}
	}

	protected void loadWatcher(Node watcherNode)
	{
		File file = null;
		int interval = 0;
		int numLines = 0;
		
		Vector filters = new Vector();
		NodeList children = watcherNode.getChildNodes();
		for (int i = 0; i < children.getLength(); i++) {
			Node node = children.item(i);
			String name = node.getNodeName();
			if (name.equals("file")) {
				file = new File(node.getFirstChild().getNodeValue());
			}
			else if (name.equals("numLines")) {
				numLines = Integer.parseInt(node.getFirstChild().getNodeValue());
			}
			else if (name.equals("interval")) {
				interval = Integer.parseInt(node.getFirstChild().getNodeValue());
			}
			else if (name.equals("filter")) {
				filters.add(filterLoader.loadFilter(node));
			}
		}
		
		m_view.addWatcher(file, interval, numLines, filters, false);
	}

	/**
	 * Create a Document with content based on the content of the given Reader.
	 */
	protected org.w3c.dom.Document createDocument(Reader r) throws Exception
	{
		org.w3c.dom.Document document;
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