package org.graysky.eclipse.logwatcher;

import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.jface.wizard.WizardPage;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;

public class IgnoreOptionsPage extends WizardPage
{
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
