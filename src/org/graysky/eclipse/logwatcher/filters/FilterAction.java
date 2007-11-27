package org.graysky.eclipse.logwatcher.filters;

import org.eclipse.swt.custom.LineStyleEvent;
import org.w3c.dom.Document;
import org.w3c.dom.Node;

/**
 * A Filter specifies how to deal with a line that has been "matched" by one of
 * the regular expressions attached to a Watcher.
 */
public interface FilterAction
{
	public void doViewerAction(LineStyleEvent event);

	public String getDescription();

	public void dispose();

	public String doWatcherAction(String line, boolean firstMatch);

	public void toXML(Document doc, Node node);
}
