package org.graysky.eclipse.logwatcher.actions;

import org.eclipse.jface.action.Action;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.jface.text.Document;
import org.graysky.eclipse.logwatcher.views.LogWatcherView;
import org.graysky.eclipse.logwatcher.views.WatcherData;
import org.graysky.eclipse.util.ImageUtils;

/**
 * Clears the text area dislaying the log file.
 */
public class ClearDisplayAction extends Action
{
    private LogWatcherView m_view = null;
    private static ImageDescriptor IMAGE_DESC = null;

    public ClearDisplayAction(LogWatcherView p)
    {
        m_view = p;

        setText("Clear");
        setToolTipText("Clear logwatcher display");
        setImageDescriptor(IMAGE_DESC);
    }

    public void run()
    {
        WatcherData entry = m_view.getSelectedEntry();
        if (entry != null) {
            entry.getWatcher().clear();
            entry.getViewer().setDocument(new Document(""));
        }
    }

    static {
        IMAGE_DESC = ImageUtils.createImageDescriptor("icons/clear.gif");
    }
}
