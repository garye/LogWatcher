package org.graysky.eclipse.logwatcher.filters;

import org.eclipse.swt.custom.LineStyleEvent;
import org.eclipse.swt.custom.StyledText;

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
    
    public String doWatcherAction(String line)
    {
    	return null;	
    }
}
