package org.graysky.eclipse.logwatcher.actions;

import org.eclipse.jface.action.Action;
import org.eclipse.jface.resource.ImageDescriptor;
import org.graysky.eclipse.logwatcher.views.LogWatcherView;
import org.graysky.eclipse.logwatcher.views.WatcherEntry;
import org.graysky.eclipse.util.ImageUtils;

/**
 * Copies current selection to clipboard.
 */
public class CopyAction extends Action
{
	private LogWatcherView	m_view = null;
	private static ImageDescriptor IMAGE_DESC = null; 
	
	public CopyAction(LogWatcherView p)
	{
		m_view = p;
		
		setText("Copy");
		setToolTipText("Copy selected text to the clipboard");
		setImageDescriptor(IMAGE_DESC);
	}
	
	public void run() {
		WatcherEntry entry = m_view.getSelectedEntry();
		if (entry != null) {
			entry.getViewer().getTextWidget().copy();
		}
	}
	
	static {
		IMAGE_DESC = ImageUtils.createImageDescriptor("icons/copy_edit.gif");
	}
}
