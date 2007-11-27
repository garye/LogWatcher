package org.graysky.eclipse.logwatcher.actions;

import java.io.File;
import java.util.Vector;

import org.eclipse.jface.action.Action;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.jface.window.Window;
import org.graysky.eclipse.logwatcher.dialogs.NewWatcherDialog;
import org.graysky.eclipse.logwatcher.views.LogWatcherView;
import org.graysky.eclipse.logwatcher.views.WatcherData;
import org.graysky.eclipse.util.ImageUtils;

/**
 * Edits the currently active Watcher.
 */
public class EditWatcherAction extends Action
{
	private LogWatcherView m_view = null;
	private static ImageDescriptor IMAGE_DESC = null;

	public EditWatcherAction(LogWatcherView p) {
		m_view = p;

		setText("Edit Watcher");
		setToolTipText("Edit this watcher");
		setImageDescriptor(IMAGE_DESC);
	}

	public void run() {
		WatcherData entry = m_view.getSelectedEntry();
		if (entry != null) {
			int topIndex = entry.getViewer().getTopIndex();
			int caret = entry.getViewer().getTextWidget().getCaretOffset();
			NewWatcherDialog d = new NewWatcherDialog(m_view.getFolder().getShell(), true);

			Vector tempFilters = new Vector();
			tempFilters.addAll(entry.getFilters());

			d.setFilters(tempFilters);
			d.setInterval(entry.getWatcher().getInterval());
			d.setNumLines(entry.getWatcher().getNumLines());
			d.setFile(new File(entry.getWatcher().getFilename()));
			if (d.open() == Window.OK) {
				m_view.editWatcher(entry, d.getInterval(), d.getNumLines(), d.getFilters());
				entry.getViewer().refresh();
				entry.getViewer().setTopIndex(topIndex);
				entry.getViewer().getTextWidget().setCaretOffset(caret);
			}
		}
	}

	static {
		IMAGE_DESC = ImageUtils.createImageDescriptor("icons/edit.gif");
	}
}
