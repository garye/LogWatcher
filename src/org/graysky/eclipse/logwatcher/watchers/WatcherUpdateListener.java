package org.graysky.eclipse.logwatcher.watchers;

import org.graysky.eclipse.util.BoundedList;

/**
 * Listens to a TextFileWatcher for an update to the file being watched.
 */
public interface WatcherUpdateListener 
{
	/**
	 * Notification that an update has occurred in the file being watched.
	 * 
	 * @param list The most recent lines that have been updated in the file.
	 */
	public void update(BoundedList list);
}
