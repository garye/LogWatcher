package org.graysky.eclipse.logwatcher.filters;

import org.eclipse.swt.custom.LineStyleEvent;
import org.eclipse.swt.custom.StyleRange;
import org.eclipse.swt.custom.StyledText;
import org.eclipse.swt.graphics.Color;

public class HighlightAction implements FilterAction
{
	private Color	m_color	= null;
	
	public HighlightAction(Color c)
	{
		m_color = c;
	}
	
    public void doAction(LineStyleEvent event)
    {
		StyleRange range = new StyleRange(event.lineOffset, event.lineText.length(), m_color, null);
		event.styles = new StyleRange[1];
		event.styles[0] = range;
    }

	
	public Color getColor()
	{
		return m_color;
	}

	public void setColor(Color color)
	{
		m_color = color;
	}
}
