package org.graysky.eclipse.logwatcher.actions;

import org.eclipse.jface.action.Action;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.jface.window.Window;
import org.graysky.eclipse.logwatcher.dialogs.NewWatcherDialog;
import org.graysky.eclipse.logwatcher.views.LogWatcherView;
import org.graysky.eclipse.util.ImageUtils;

/**
 * Creates a new watcher in the view.
 */
public class NewWatcherAction extends Action
{
	private LogWatcherView	m_view = null;
	private static ImageDescriptor IMAGE_DESC = null; 
	
	public NewWatcherAction(LogWatcherView p)
	{
		m_view = p;
		
		setText("New Watcher");
		setToolTipText("Create a new watcher");
		setImageDescriptor(IMAGE_DESC);
	}
	
	public void run() {
		NewWatcherDialog d = new NewWatcherDialog(m_view.getFolder().getShell(), false);
		if (d.open() == Window.OK) {
			m_view.addWatcher(d.getFile(),d.getInterval(), d.getNumLines(), d.getFilters(), true);
		}
	}
	
	static {
		IMAGE_DESC = ImageUtils.createImageDescriptor("icons/new.gif");
	}
}
