package org.graysky.eclipse.logwatcher.filters;

import java.io.IOException;
import java.io.Writer;

import org.eclipse.swt.custom.LineStyleEvent;

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
    
    public void toXML(Writer writer) throws IOException
    {
    	writer.write("<action type=\"ignore\"/>");
    }
}
