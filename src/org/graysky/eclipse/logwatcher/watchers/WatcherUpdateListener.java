package org.graysky.eclipse.logwatcher.watchers;

import org.graysky.eclipse.util.BoundedList;

public interface WatcherUpdateListener 
{
	public void update(BoundedList list);
}
