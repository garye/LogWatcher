package org.graysky.eclipse.logwatcher.views;

import java.util.Iterator;
import java.util.Vector;

import org.eclipse.jface.text.TextViewer;
import org.eclipse.swt.custom.CTabItem;
import org.graysky.eclipse.logwatcher.LogwatcherPlugin;
import org.graysky.eclipse.logwatcher.filters.Filter;
import org.graysky.eclipse.logwatcher.watchers.TextFileWatcher;
import org.graysky.eclipse.util.XmlUtils;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;

/**
 * Manages the objects that make up the UI for a Watcher in the LogWatcher view.
 */
public class WatcherData
{
	private TextViewer m_viewer = null;
	private TextFileWatcher m_watcher = null;
	private CTabItem m_tab = null;
	private Vector m_filters = null;
	private boolean m_scroll = true;

	public WatcherData(TextViewer v, TextFileWatcher w, CTabItem t, Vector f) {
		setViewer(v);
		setWatcher(w);
		setTab(t);
		setFilters(f);
	}

	public void toXML(Document doc, Node node) {
		Element watcher = doc.createElement("watcher");
		watcher.appendChild(XmlUtils.createElementWithText(doc, "file", getWatcher().getFilename()));
		watcher.appendChild(XmlUtils.createElementWithText(doc, "numLines", Integer
				.toString(getWatcher().getNumLines())));
		watcher.appendChild(XmlUtils.createElementWithText(doc, "interval", Integer
				.toString(getWatcher().getInterval())));
		for (Iterator iter = getFilters().iterator(); iter.hasNext();) {
			Filter filter = (Filter) iter.next();
			filter.toXML(doc, watcher);
		}
		node.appendChild(watcher);
	}

	public void dispose() {
		try {
			getWatcher().halt();
			if (getViewer().getControl() != null) {
				getViewer().getControl().dispose();
			}
			getTab().dispose();
			for (Iterator iterator = getFilters().iterator(); iterator.hasNext();) {
				Filter element = (Filter) iterator.next();
				element.dispose();
			}
		}
		catch (Throwable t) {
			LogwatcherPlugin.getDefault().logError("Error disposing of the entry", null);
		}
	}

	public void setViewer(TextViewer v) {
		m_viewer = v;
	}

	public TextViewer getViewer() {
		return m_viewer;
	}

	public void setWatcher(TextFileWatcher watcher) {
		m_watcher = watcher;
	}

	public TextFileWatcher getWatcher() {
		return m_watcher;
	}

	public void setTab(CTabItem tab) {
		m_tab = tab;
	}

	public CTabItem getTab() {
		return m_tab;
	}

	public void setFilters(Vector filters) {
		m_filters = filters;
	}

	public Vector getFilters() {
		return m_filters;
	}

	public void setScroll(boolean scroll) {
		m_scroll = scroll;
	}

	public boolean isScroll() {
		return m_scroll;
	}
}