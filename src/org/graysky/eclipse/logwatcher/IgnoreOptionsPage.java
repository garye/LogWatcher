package org.graysky.eclipse.logwatcher;

import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.jface.wizard.WizardPage;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Canvas;
import org.eclipse.swt.widgets.ColorDialog;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Text;
import org.graysky.eclipse.logwatcher.filters.Filter;

public class IgnoreOptionsPage extends WizardPage
{
	private Text		m_filterText;
	private Composite	m_actionOptions;
	private Color		m_color;
	private Combo		m_actionsCombo;
	private Filter		m_filter;

	/**
	 * Constructor for IgnoreOptionsPage.
	 * @param pageName
	 */
	public IgnoreOptionsPage(String pageName)
	{
		super(pageName);
	}

	/**
	 * Constructor for IgnoreOptionsPage.
	 * @param pageName
	 * @param title
	 * @param titleImage
	 */
	public IgnoreOptionsPage(String pageName, String title, ImageDescriptor titleImage)
	{
		super(pageName, title, titleImage);
	}

	public void createControl(Composite parent) 
	{
		Composite composite = new Composite(parent, SWT.NONE);
		setControl(composite);
		
		GridData gridData;
		
		GridLayout layout = new GridLayout();
		layout.numColumns = 3;
		layout.horizontalSpacing = 10;
		layout.verticalSpacing = 20;
		composite.setLayout(layout);
	
		new Label(composite, SWT.NONE).setText("No options available");
		
		setPageComplete(true);
	}


	/**
	 * @see org.eclipse.jface.dialogs.IDialogPage#getTitle()
	 */
	public String getTitle()
	{
		return "There are no options for this action.";
	}

}
