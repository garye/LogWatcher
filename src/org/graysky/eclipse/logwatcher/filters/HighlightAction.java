package org.graysky.eclipse.logwatcher.filters;

import org.eclipse.swt.custom.LineStyleEvent;
import org.eclipse.swt.custom.StyleRange;
import org.eclipse.swt.graphics.Color;
import org.graysky.eclipse.util.XmlUtils;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;

/**
 * Highlights a line in the LogWatcher editor view. 
 */
public class HighlightAction implements FilterAction
{
	private Color m_fgcolor = null;
	private Color m_bgcolor = null;

	public HighlightAction(Color fgcolor, Color bgcolor)
	{
		m_fgcolor = fgcolor;
		m_bgcolor = bgcolor;
	}
	
	public void doViewerAction(LineStyleEvent event)
	{
		StyleRange range = new StyleRange(event.lineOffset, event.lineText.length(), m_fgcolor, m_bgcolor);
		event.styles = new StyleRange[1];
		event.styles[0] = range;
	}

	public String getDescription()
	{
		return "Highlight line";
	}

	public Color getFgColor()
	{
		return m_fgcolor;
	}

	public void setFgColor(Color fgcolor)
	{
		m_fgcolor = fgcolor;
	}

	public Color getBgColor() {
		return m_bgcolor;
	}

	public void setBgColor(Color bgcolor) {
		this.m_bgcolor = bgcolor;
	}

	public void dispose()
	{
		m_fgcolor.dispose();
		m_bgcolor.dispose();
	}

	public String doWatcherAction(String line, boolean firstMatch)
	{
		return line;
	}

	public void toXML(Document doc, Node node)
    {
        Element action = doc.createElement("action");
        action.setAttribute("type", "highlight");
        
        // Foreground colour
        Element fgcolor = doc.createElement("fgColor");
        fgcolor.appendChild(XmlUtils.createElementWithText(doc, "red", Integer.toString(getFgColor().getRGB().red)));
        fgcolor.appendChild(XmlUtils.createElementWithText(doc, "green", Integer.toString(getFgColor().getRGB().green)));
        fgcolor.appendChild(XmlUtils.createElementWithText(doc, "blue", Integer.toString(getFgColor().getRGB().blue)));
        action.appendChild(fgcolor);
        
        // Background colour
        Element bgcolor = doc.createElement("bgColor");
        bgcolor.appendChild(XmlUtils.createElementWithText(doc, "red", Integer.toString(getBgColor().getRGB().red)));
        bgcolor.appendChild(XmlUtils.createElementWithText(doc, "green", Integer.toString(getBgColor().getRGB().green)));
        bgcolor.appendChild(XmlUtils.createElementWithText(doc, "blue", Integer.toString(getBgColor().getRGB().blue)));
        action.appendChild(bgcolor);

        node.appendChild(action);
    }
}
