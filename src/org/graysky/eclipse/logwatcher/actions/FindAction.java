package org.graysky.eclipse.logwatcher.actions;

import org.eclipse.jface.action.Action;
import org.eclipse.jface.resource.ImageDescriptor;
import org.graysky.eclipse.logwatcher.dialogs.FindDialog;
import org.graysky.eclipse.logwatcher.views.LogWatcherView;
import org.graysky.eclipse.logwatcher.views.WatcherData;
import org.graysky.eclipse.util.ImageUtils;

/**
 * Displays a Find dialog box, similar to the default Eclipse Find dialog.
 */
public class FindAction extends Action
{
	private LogWatcherView	m_view = null;
	private static ImageDescriptor IMAGE_DESC = null; 
	
	public FindAction(LogWatcherView p)
	{
		m_view = p;
		
		setText("Find...");
		setToolTipText("Find in log file");
		setImageDescriptor(IMAGE_DESC);
	}
	
	public void run() {
		WatcherData entry = m_view.getSelectedEntry();
		if (entry != null) {

			FindDialog d = new FindDialog(m_view.getFolder().getShell(), entry.getViewer().getFindReplaceTarget());
			d.open();
		}
	}
	
	static {
		IMAGE_DESC = ImageUtils.createImageDescriptor("icons/search.gif");
	}
}
