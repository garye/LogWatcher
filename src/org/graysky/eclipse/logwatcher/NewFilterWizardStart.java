package org.graysky.eclipse.logwatcher;

import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.jface.wizard.IWizardPage;
import org.eclipse.jface.wizard.WizardPage;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Text;
import org.graysky.eclipse.logwatcher.filters.Filter;
import org.apache.oro.text.regex.*;

public class NewFilterWizardStart extends WizardPage
{
	private Text			m_filterText;
	private Composite		m_actionOptions;
	private Color			m_color;
	private Combo			m_actionsCombo;
	private Combo			m_containsCombo;
	private Button			m_caseSensitiveBox;
	private Perl5Compiler	m_regExpCompiler = new Perl5Compiler();

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
		
		m_containsCombo = new Combo(composite, SWT.DROP_DOWN | SWT.READ_ONLY);
		m_containsCombo.add("contains");
		m_containsCombo.add("does not contain");
		m_containsCombo.select(0);
		
		m_filterText = new Text(composite, SWT.BORDER);
		m_filterText.setTextLimit(200);
		gridData = new GridData();
		gridData.widthHint = 200;
		m_filterText.setLayoutData(gridData);
		m_filterText.addModifyListener(new ModifyListener() {
            public void modifyText(ModifyEvent e)
            {
            	setPageComplete(validatePage());
            }
        });
	
		//
		// Second row
		//
		new Label(composite, SWT.NONE).setText("Take this action:");
		
		m_actionsCombo = new Combo(composite, SWT.DROP_DOWN | SWT.READ_ONLY);
		m_actionsCombo.add("Highlight Line");
		m_actionsCombo.add("Skip Line");
		m_actionsCombo.add("Add Todo Task");
		m_actionsCombo.select(0);
		gridData = new GridData();
		gridData.horizontalSpan = 2;
		m_actionsCombo.setLayoutData(gridData);
		m_actionsCombo.addSelectionListener(new SelectionAdapter() {
            public void widgetSelected(SelectionEvent e)
            {
            	setPageComplete(validatePage()); 
            }
        });
        
        //
		// Third row
		//
		m_caseSensitiveBox = new Button(composite, SWT.CHECK);
		m_caseSensitiveBox.setText("Case sensitive");
		gridData = new GridData();
		gridData.horizontalSpan = 3;
		m_caseSensitiveBox.setLayoutData(gridData);
	
		setDescription("Text must be a valid regular expression.");
		
		setPageComplete(validatePage());
	}


	/**
	 * @see org.eclipse.jface.dialogs.IDialogPage#getTitle()
	 */
	public String getTitle()
	{
		return "Define the filter.";
	}

	public int getActionType()
	{
		return m_actionsCombo.getSelectionIndex();	
	}

	public Filter getFilter()
	{
		try {
		
			Filter f = new Filter();
			f.setPattern(m_filterText.getText(), m_caseSensitiveBox.getSelection());
			f.setContains(m_containsCombo.getSelectionIndex() == 0 ? true : false);
			return f;
		}
		catch (MalformedPatternException e) {
			// Shouldn't happen - we have already compiled the pattern.
			return null;
		}
	}

	public IWizardPage getNextPage()
	{	
		switch (m_actionsCombo.getSelectionIndex()) {
			case 0:
				return getWizard().getPage("highlight_options");
				
			case 1:
				return getWizard().getPage("ignore_options");
				
			case 2:
				return getWizard().getPage("addTask_options");
				
			default:
				return null;
		}
	}

	protected boolean validatePage()
	{
		if (m_filterText.getText().length() > 0) {
		
			try {
				m_regExpCompiler.compile(m_filterText.getText());
				setErrorMessage(null);
				return true;
			}
			catch (MalformedPatternException e) {
				setErrorMessage("Invalid regular expression: " + e.getMessage());
				return false;
			}
		}
		else {
			return false;
		}
	}

}
