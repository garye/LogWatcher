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
	private int				m_numLines	= 100;
	private boolean			m_active	= false;
	private Vector			m_listeners	= new Vector();
	private boolean			m_console	= false;
	private Vector			m_filters	= new Vector();
	private BoundedList		m_list		= null;

	
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
		
		m_list = new BoundedList(m_numLines);
		String line = null;
		long size = 0;
		boolean firstUpdate = false;
		
		while (m_active) {
			// Keep checking for new lines in the file
			boolean updated = false;
			boolean truncated = false;
			
			// See if the file was truncated...
			truncated = false;
			if (m_file.length() < size) {
				truncated = true;
			}
			size = m_file.length();
			
			try {
				if (truncated) {
					m_list.put("*** File truncated ***");
					updated = true;
					m_reader.close();
					m_reader = new BufferedReader(new FileReader(m_file));
					
					// Reset the stream
					while ((line = m_reader.readLine()) != null) {}	
				}
				else if (!m_file.exists()) {
					m_list.put("*** File deleted ***");
					updated = true;
					m_active = false;
				}
				else {
					while ((line = m_reader.readLine()) != null) {
						
						if (line.length() > 0) {
						    synchronized (m_filters) {
						        // Apply each filter
								for (Iterator iter = m_filters.iterator(); iter.hasNext();) {
		                            Filter f = (Filter) iter.next();
		                            if (f.matches(line)) {
		                    			line = f.handleWatcherMatch(line, firstUpdate);	
		                    		}   
		                        }
						    }
						    
							// Make sure the filter didn't set the line to null...
							if (line != null) {
								updated = true;
								m_list.put(line);
								
								if (m_console) {
									// Dump the latest line to the console
									System.out.println(line);
								}
							}
						}
					}
					
					firstUpdate = true;
				}
				
				if (updated) {
					notifyListeners();
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
		
		try {
			m_reader.close();
		}
		catch (Exception e) {
			// ignore	
		}
	}

	protected synchronized void notifyListeners()
	{
		for (Iterator i = m_listeners.iterator(); i.hasNext();) {
			WatcherUpdateListener l = (WatcherUpdateListener) i.next();
			l.update(m_list);
		}
	}

	public String getFilename()
	{
		return m_file.getAbsolutePath();	
	}

	public int getInterval()
	{
		return m_interval;	
	}
	
	public void setInterval(int interval)
	{
	    m_interval = interval;
	}

	public void clear()
	{
		m_list.clear();
		notifyListeners();
	}

	public void setNumLines(int numLines)
	{
	    m_numLines = numLines;
	    m_list.setMaxItems(numLines);
	}

    public int getNumLines()
    {
        return m_numLines;
    }

    public void setFilters(Vector filters)
    {
        synchronized (m_filters) {
        	m_filters = filters;
        }
    }

}
