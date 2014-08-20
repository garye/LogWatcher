package org.graysky.eclipse.logwatcher.filters;

import org.eclipse.core.resources.IMarker;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.swt.custom.LineStyleEvent;
import org.graysky.eclipse.util.XmlUtils;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;

public class AddTaskAction implements FilterAction
{
	private String 		m_taskDescription 	= "";
	private int			m_priority			= -1;
	private IResource	m_resource			= null;
	
	public static final int LOW 	= IMarker.PRIORITY_LOW;
	public static final int NORMAL	= IMarker.PRIORITY_NORMAL;
	public static final int HIGH	= IMarker.PRIORITY_HIGH;	
	
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
    
    public int getPriority()
    {
        return m_priority;
    }

    public void setPriority(int priority)
    {
        m_priority = priority;
    }
    
    public String getTaskDescription()
    {
        return m_taskDescription;
    }

    public void setTaskDescription(String taskDescription)
    {
        m_taskDescription = taskDescription;
    }

    public String doWatcherAction(String line, boolean firstMatch)
    {
    	
    	if (firstMatch) {
	    	try {
	            IMarker marker = m_resource.createMarker(IMarker.TASK);
	            marker.setAttribute(IMarker.PRIORITY, m_priority);
	            marker.setAttribute(IMarker.MESSAGE, m_taskDescription);
	        }
	        catch (CoreException e) {
	        	e.printStackTrace();
	        }
    	}
       
    	return line;	
    }
    
    public void toXML(Document doc, Node node)
    {
        Element action = doc.createElement("action");
        action.setAttribute("type", "task");
        action.appendChild(XmlUtils.createElementWithText(doc, "taskDescription", getTaskDescription()));
        action.appendChild(XmlUtils.createElementWithText(doc, "priority", Integer.toString(getPriority())));
        node.appendChild(action);
    }
}
