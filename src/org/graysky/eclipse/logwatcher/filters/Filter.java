package org.graysky.eclipse.logwatcher.filters;

import java.util.Iterator;
import java.util.Vector;

public class Filter 
{
	private String	m_pattern	= null;
	private Vector	m_actions	= new Vector();
	
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
	public void handleMatch(String str, int offset)
	{
		for (Iterator iter = m_actions.iterator(); iter.hasNext();) {
            FilterAction action = (FilterAction) iter.next();
            action.doAction(str, offset);
        }
	}
	
	public void addAction(FilterAction action)
	{
		m_actions.add(action);	
	}
	
}
