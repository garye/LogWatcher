package org.graysky.eclipse.logwatcher.filters;

import java.io.IOException;
import java.io.Writer;

import org.eclipse.swt.custom.LineStyleEvent;

public interface FilterAction
{
	public void doViewerAction(LineStyleEvent event);
	
	public String getDescription();
	
	public void dispose();
	
	public String doWatcherAction(String line);
	
	public void toXML(Writer writer) throws IOException;
}
