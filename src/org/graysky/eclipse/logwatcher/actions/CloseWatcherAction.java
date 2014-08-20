package org.graysky.eclipse.logwatcher.actions;

import org.eclipse.jface.action.Action;
import org.eclipse.jface.resource.ImageDescriptor;
import org.graysky.eclipse.logwatcher.views.LogWatcherView;
import org.graysky.eclipse.util.ImageUtils;

/**
 * Closes the currently selected Watcher.
 */
public class CloseWatcherAction extends Action
{
	private LogWatcherView	m_view = null;
	private static ImageDescriptor IMAGE_DESC = null; 
	
	public CloseWatcherAction(LogWatcherView p)
	{
		m_view = p;
		
		setText("Close");
		setToolTipText("Close this watcher");
		setImageDescriptor(IMAGE_DESC);
	}
	
	public void run() {
		m_view.closeSelectedWatcher();
	}
	
	static {
		IMAGE_DESC = ImageUtils.createImageDescriptor("icons/close.gif");
	}
}
