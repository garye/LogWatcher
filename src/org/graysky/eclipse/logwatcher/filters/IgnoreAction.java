package org.graysky.eclipse.logwatcher.filters;

import org.eclipse.swt.custom.LineStyleEvent;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;

/**
 * Ignores (skips) a line in the LogWatcher editor view.
 */
public class IgnoreAction implements FilterAction
{
    public IgnoreAction()
    {
        super();
    }

    public void dispose()
    {
    }

    public void doViewerAction(LineStyleEvent event)
    {
    	return;
    }

    public String getDescription()
    {
        return "Don't show the line";
    }
    
    public String doWatcherAction(String line, boolean firstMatch)
    {
    	return null;	
    }
    
    public void toXML(Document doc, Node node)
    {
        Element action = doc.createElement("action");
        action.setAttribute("type", "ignore");
        node.appendChild(action);
    }
}
