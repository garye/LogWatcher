package org.graysky.eclipse.logwatcher.views;

import java.io.IOException;
import java.io.Writer;
import java.util.Iterator;
import java.util.Vector;

import org.eclipse.jface.text.TextViewer;
import org.eclipse.swt.custom.CTabItem;
import org.graysky.eclipse.logwatcher.LogwatcherPlugin;
import org.graysky.eclipse.logwatcher.filters.Filter;
import org.graysky.eclipse.logwatcher.watchers.TextFileWatcher;

/**
 * Tracks the objects that make up a Watcher.
 */	
public class WatcherEntry
{
	private TextViewer 		m_viewer 	= null;
	private TextFileWatcher 	m_watcher 	= null;
	private CTabItem 			m_tab 		= null;
	private Vector				m_filters	= null;
	private boolean 			m_scroll	= true;
	
	public WatcherEntry(TextViewer v, TextFileWatcher w, CTabItem t, Vector f)
	{
		setViewer(v);	
		setWatcher(w);
		setTab(t);
		setFilters(f);
	}
	
	/**
	 * Serialize the Watcher to XML
	 * TODO: Use the DOM API to write the XML.
	 */
	public void toXML(Writer w) throws IOException
	{
		w.write("<watcher>\n");
		w.write("<file>" + getWatcher().getFilename() + "</file>");
		w.write("<numLines>" + getWatcher().getNumLines() + "</numLines>");
		w.write("<interval>" + getWatcher().getInterval() + "</interval>");
		for (Iterator iter = getFilters().iterator(); iter.hasNext();) {
            Filter filter = (Filter) iter.next();
            filter.toXML(w);
        }
		w.write("\n</watcher>");
		
	}
	
	public void dispose()
	{
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

	public void setViewer(TextViewer v)
	{
		m_viewer = v;
	}

	public TextViewer getViewer()
	{
		return m_viewer;
	}

	public void setWatcher(TextFileWatcher watcher)
	{
		m_watcher = watcher;
	}

	public TextFileWatcher getWatcher()
	{
		return m_watcher;
	}

	public void setTab(CTabItem tab)
	{
		m_tab = tab;
	}

	public CTabItem getTab()
	{
		return m_tab;
	}

	public void setFilters(Vector filters)
	{
		m_filters = filters;
	}

	public Vector getFilters()
	{
		return m_filters;
	}

	public void setScroll(boolean scroll)
	{
		m_scroll = scroll;
	}

	public boolean isScroll()
	{
		return m_scroll;
	}
}