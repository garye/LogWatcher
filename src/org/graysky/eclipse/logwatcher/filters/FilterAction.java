package org.graysky.eclipse.logwatcher.filters;

import org.eclipse.swt.custom.LineStyleEvent;

public interface FilterAction
{
	public void doAction(LineStyleEvent event);
	
	public String getDescription();
}
