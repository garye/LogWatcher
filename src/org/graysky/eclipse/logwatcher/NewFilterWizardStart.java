package org.graysky.eclipse.logwatcher;

import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.jface.wizard.IWizardPage;
import org.eclipse.jface.wizard.WizardPage;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Text;
import org.graysky.eclipse.logwatcher.filters.Filter;

public class NewFilterWizardStart extends WizardPage
{
	private Text		m_filterText;
	private Composite	m_actionOptions;
	private Color		m_color;
	private Combo		m_actionsCombo;
	private Filter		m_filter;

	/**
	 * Constructor for FilterWizardStartPage.
	 * @param pageName
	 */
	public NewFilterWizardStart(String pageName)
	{
		super(pageName);
	}

	/**
	 * Constructor for FilterWizardStartPage.
	 * @param pageName
	 * @param title
	 * @param titleImage
	 */
	public NewFilterWizardStart(String pageName, String title, ImageDescriptor titleImage)
	{
		super(pageName, title, titleImage);
	}

	/**
	 * @see org.eclipse.jface.dialogs.IDialogPage#createControl(org.eclipse.swt.widgets.Composite)
	 */
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
	
		//
		// First row
		//
		new Label(composite, SWT.NONE).setText("Where text ");
		
		Combo containsCombo = new Combo(composite, SWT.DROP_DOWN | SWT.READ_ONLY);
		containsCombo.add("contains");
		containsCombo.add("does not contain");
		containsCombo.select(0);
		
		m_filterText = new Text(composite, SWT.BORDER);
		m_filterText.setTextLimit(200);
		gridData = new GridData();
		gridData.widthHint = 200;
		m_filterText.setLayoutData(gridData);
		
		//
		// Second row
		//
		new Label(composite, SWT.NONE).setText("Take this action:");
		
		m_actionsCombo = new Combo(composite, SWT.DROP_DOWN | SWT.READ_ONLY);
		m_actionsCombo.add("Highlight Line");
		m_actionsCombo.add("Skip Line");
		m_actionsCombo.select(0);
		gridData = new GridData();
		gridData.horizontalSpan = 2;
		m_actionsCombo.setLayoutData(gridData);
	}


	/**
	 * @see org.eclipse.jface.dialogs.IDialogPage#getTitle()
	 */
	public String getTitle()
	{
		return "Define the filter.";
	}

	/**
	 * @see org.eclipse.jface.wizard.IWizardPage#canFlipToNextPage()
	 */
	public boolean canFlipToNextPage()
	{
		return (m_filterText.getText().length() > 0);
	}

	public IWizardPage getNextPage()
	{
		switch (m_actionsCombo.getSelectionIndex()) {
			case 0:
				return getWizard().getPage("highlight_options");
				
			case 1:
				return getWizard().getPage("ignore_options");
				
			default:
				return null;
		}
	}

}