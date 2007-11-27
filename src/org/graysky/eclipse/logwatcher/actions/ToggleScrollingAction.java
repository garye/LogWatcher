package org.graysky.eclipse.logwatcher.actions;

import org.eclipse.jface.action.Action;
import org.eclipse.jface.resource.ImageDescriptor;
import org.graysky.eclipse.logwatcher.views.LogWatcherView;
import org.graysky.eclipse.logwatcher.views.WatcherData;
import org.graysky.eclipse.util.ImageUtils;

/**
 * Toggles automatic scrolling.
 */
public class ToggleScrollingAction extends Action
{
	private LogWatcherView m_view = null;
	private static ImageDescriptor IMAGE_DESC = null;

	public ToggleScrollingAction(LogWatcherView p) {
		m_view = p;

		setText("Scroll Lock");
		setToolTipText("Scroll Lock");
		setImageDescriptor(IMAGE_DESC);
	}

	public void run() {
		WatcherData entry = m_view.getSelectedEntry();
		if (entry != null) {
			entry.setScroll(!isChecked());
		}
	}

	static {
		IMAGE_DESC = ImageUtils.createImageDescriptor("icons/toggle_scroll.gif");
	}
}
