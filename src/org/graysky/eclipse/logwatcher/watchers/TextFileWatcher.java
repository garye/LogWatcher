package org.graysky.eclipse.logwatcher.watchers;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.BufferedInputStream;
import java.io.IOException;
import java.util.Iterator;
import java.util.Vector;

import org.graysky.eclipse.logwatcher.filters.Filter;
import org.graysky.eclipse.util.BoundedList;

/**
 * Watches a text file for any changes, and keeps a list of the most recent
 * lines to have been added to the file. Notifies WatcherListeners when
 * a change to the file being watched is detected.
 */
public class TextFileWatcher extends Thread 
{

	private File			m_file		= null;
	private BufferedReader	m_reader	= null;
	private int				m_interval	= 1; // Seconds
	private int				m_numLines	= 10;
	private boolean			m_active	= false;
	private Vector			m_listeners	= new Vector();
	private boolean			m_console	= false;
	private Vector			m_filters	= new Vector();

	
	public TextFileWatcher(String filename, int interval, int numLines) 
			throws FileNotFoundException
	{
		this(new File(filename), interval, numLines);	
	}
	
	public TextFileWatcher(File file, int interval, int numLines) 
		throws FileNotFoundException
	{
		m_file = file;
		m_interval = interval;
		m_numLines = numLines;
		
		m_reader = new BufferedReader(new FileReader(m_file));
	}
	
	public void halt()
	{
		m_active = false;
		interrupt();
	}
	
	/**
	 * Determines if the watcher should output each updated line to the console.
	 */
	public void setConsole(boolean b)
	{
		m_console = b;
	}
	
	public void addListener(WatcherUpdateListener listener)
	{
		m_listeners.add(listener);
	}
	
	public void run()
	{
		m_active = true;
		
		BoundedList list = new BoundedList(m_numLines);
		String line = null;
		
		while (m_active) {
			// Keep checking for new lines in the file
			boolean updated = false;
			try {
				while ((line = m_reader.readLine()) != null) {
					if (line.length() > 0) {
						
						for (Iterator iter = m_filters.iterator(); iter.hasNext();) {
                            Filter f = (Filter) iter.next();
                            if (f.matches(line)) {
                    			line = f.handleWatcherMatch(line);	
                    		}   
                        }
						
						// Make sure the filter didn't set the line to null...
						if (line != null) {
							updated = true;
							list.put(line);
							
							if (m_console) {
								// Dump the latest line to the console
								System.out.println(line);
							}
						}
					}
				}
				
				if (updated) {
					// Notify listeners
					for (Iterator i = m_listeners.iterator(); i.hasNext();) {
						WatcherUpdateListener l = (WatcherUpdateListener) i.next();
						l.update(list);
					}
				}
				
				sleep(m_interval * 1000);
			}
			catch (IOException e) {
				e.printStackTrace();
			}
			catch (InterruptedException e) {
				// Ignore. If we have been stopped, the while condition will fail.	
			}
		}
	}

    public int getNumLines()
    {
        return m_numLines;
    }

    public void setFilters(Vector filters)
    {
        m_filters = filters;
    }

}
