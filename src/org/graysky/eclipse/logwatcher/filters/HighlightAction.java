package org.graysky.eclipse.logwatcher.filters;

import java.io.IOException;
import java.io.Writer;

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
	
    public void doViewerAction(LineStyleEvent event)
    {
		StyleRange range = new StyleRange(event.lineOffset, event.lineText.length(), m_color, null);
		event.styles = new StyleRange[1];
		event.styles[0] = range;
    }

	public String getDescription()
	{
		return "Highlight line";	
	}
	
	public Color getColor()
	{
		return m_color;
	}

	public void setColor(Color color)
	{
		m_color = color;
	}
	
	public void dispose()
	{
		m_color.dispose();	
	}

    public String doWatcherAction(String line)
    {
        return line;
    }

    public void toXML(Writer writer) throws IOException
    {
    	writer.write("<action type=\"highlight\">");
    	writer.write("<red>" + getColor().getRGB().red + "</red>");
    	writer.write("<green>" + getColor().getRGB().green + "</green>");
    	writer.write("<blue>" + getColor().getRGB().blue + "</blue>");
    	writer.write("</action>");
    }

}
