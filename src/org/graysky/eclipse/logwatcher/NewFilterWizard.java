package org.graysky.eclipse.logwatcher;

import org.eclipse.jface.wizard.Wizard;
import org.graysky.eclipse.logwatcher.filters.Filter;
import org.graysky.eclipse.logwatcher.filters.FilterAction;
import org.graysky.eclipse.logwatcher.filters.HighlightAction;
import org.graysky.eclipse.logwatcher.filters.IgnoreAction;

public class NewFilterWizard extends Wizard {
	
	private Filter					m_filter;
	private boolean					m_canFinish 	= false;
	private NewFilterWizardStart	m_startPage		= new NewFilterWizardStart("start");
	private HighlightOptionsPage	m_highlightPage	= new HighlightOptionsPage("highlight_options");
	private IgnoreOptionsPage		m_ignorePage	= new IgnoreOptionsPage("ignore_options");

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
		switch (m_startPage.getActionType()) {
			case 0:
				HighlightAction a = new HighlightAction(m_highlightPage.getColor());
				addFilterAction(a);
				break;
				
			case 1:
				addFilterAction(new IgnoreAction());
				break;
					
		}
		return true;
	}

	protected void initPages()
	{
		addPage(m_startPage);
		addPage(m_highlightPage);
		addPage(m_ignorePage);
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
        return m_canFinish;
    }
    
    public void setCanFinish(boolean canFinish)
    {
        m_canFinish = canFinish;
    }

}
