package org.graysky.eclipse.logwatcher.filters;

import java.util.Iterator;
import java.util.Vector;

import org.eclipse.swt.custom.LineStyleEvent;

public class Filter 
{
	private String	m_pattern	= null;
	private Vector	m_actions	= new Vector();
	private	boolean	m_contains	= true;
	
	/**
	 * Test if the given string is matched by this filter.
	 */
	public boolean matches(String str)
	{
		return (str.matches(m_pattern));	
	}

	/**
	 * Take the specified actions for the given string, which is assumed to have
	 * matched the filter.
	 */
	public void handleMatch(LineStyleEvent event)
	{
		for (Iterator iter = m_actions.iterator(); iter.hasNext();) {
            FilterAction action = (FilterAction) iter.next();
            action.doAction(event);
        }
	}
	
	public void addAction(FilterAction action)
	{
		m_actions.add(action);	
	}

	public String getDescription()
	{
		return "";
	}	
}
