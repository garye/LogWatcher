package org.graysky.eclipse.logwatcher;

import org.eclipse.jface.wizard.Wizard;
import org.eclipse.swt.graphics.Color;
import org.graysky.eclipse.logwatcher.filters.AddTaskAction;
import org.graysky.eclipse.logwatcher.filters.Filter;
import org.graysky.eclipse.logwatcher.filters.FilterAction;
import org.graysky.eclipse.logwatcher.filters.HighlightAction;
import org.graysky.eclipse.logwatcher.filters.IgnoreAction;

public class NewFilterWizard extends Wizard
{
	private Filter					m_filter;
	private boolean				m_canFinish 	= false;
	private NewFilterWizardStart	m_startPage		= new NewFilterWizardStart("start");
	private HighlightOptionsPage	m_highlightPage	= new HighlightOptionsPage("highlight_options");
	private IgnoreOptionsPage		m_ignorePage	= new IgnoreOptionsPage("ignore_options");
	private AddTaskOptionsPage		m_taskPage		= new AddTaskOptionsPage("addTask_options");

	/**
	 * Constructor for NewFilterWizard.
	 */
	public NewFilterWizard()
	{
		super();
		setWindowTitle("New Filter Wizard");
		initPages();
	}

	public boolean performFinish()
	{
		m_filter = m_startPage.getFilter();
		switch (m_startPage.getActionType()) {
			case 0:
				HighlightAction a = new HighlightAction(m_highlightPage.getColor());
				addFilterAction(a);
				break;
				
			case 1:
				addFilterAction(new IgnoreAction());
				break;
				
			case 2:
				AddTaskAction ta = new AddTaskAction(m_taskPage.getDescription(), 
													 m_taskPage.getPriority(), 
													 LogwatcherPlugin.getWorkspace().getRoot());
				addFilterAction(ta);
				break;
					
		}
		return true;
	}
	
	public boolean performCancel()
	{
		Color c = m_highlightPage.getColor();
		if (c != null) {
			c.dispose();
		}
			
		return super.performCancel();
	}
		
	protected void initPages()
	{
		addPage(m_startPage);
		addPage(m_highlightPage);
		addPage(m_ignorePage);
		addPage(m_taskPage);
	}

    public void setFilter(Filter f)
    {
    	m_filter = f;
    }
	
	public void addFilterAction(FilterAction a)
	{
		m_filter.addAction(a);	
	}

    public Filter getFilter()
    {
        return m_filter;
    }

    public boolean canFinish()
    {
        if (m_startPage.isPageComplete()) {
			switch (m_startPage.getActionType()) {
				case 0:
					if (m_highlightPage.isPageComplete()) {
						return true;
					}
					break;
		
				case 1:
					return true;
				
				case 2:
					if (m_taskPage.isPageComplete()) {
						return true;
					}
					break;
			}
        }
        
        return false;
    }
    
    public void setCanFinish(boolean canFinish)
    {
        m_canFinish = canFinish;
    }

}
