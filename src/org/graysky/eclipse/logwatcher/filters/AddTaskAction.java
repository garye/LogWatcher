package org.graysky.eclipse.logwatcher.filters;

import java.io.IOException;
import java.io.Writer;

import org.eclipse.core.resources.IMarker;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.swt.custom.LineStyleEvent;
import org.eclipse.swt.custom.StyledText;

public class AddTaskAction implements FilterAction
{
	private String 		m_taskDescription 	= "";
	private int			m_priority			= -1;
	private IResource	m_resource			= null;
	
	public static final int LOW 	= 0;
	public static final int NORMAL	= 1;
	public static final int HIGH	= 2;	
	
    public AddTaskAction()
    {
        super();
    }

	public AddTaskAction(String desc, int pri, IResource res)
	{
		m_taskDescription = desc;
		m_priority = pri;	
		m_resource = res;
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
        return "Add a Todo Task";
    }
    
    public String doWatcherAction(String line, boolean firstMatch)
    {
    	
    	if (firstMatch) {
	    	try {
	            IMarker marker = m_resource.createMarker(IMarker.TASK);
	            marker.setAttribute(IMarker.SEVERITY, IMarker.PRIORITY_LOW);
	            marker.setAttribute(IMarker.MESSAGE, m_taskDescription);
	        }
	        catch (CoreException e) {
	        	e.printStackTrace();
	        }
    	}
       
    	return line;	
    }
    
    public void toXML(Writer writer) throws IOException
    {
    	writer.write("<action type=\"task\"/>");
    }
}
