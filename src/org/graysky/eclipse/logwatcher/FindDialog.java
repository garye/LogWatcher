package org.graysky.eclipse.logwatcher;

import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.jface.text.IFindReplaceTarget;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;

public class FindDialog extends Dialog
{
	private Text				m_findText 		= null;
	private IFindReplaceTarget	m_target		= null;
	private Button				m_caseSensitive	= null;
	private Button				m_wrap			= null;
	private Button				m_wholeWord		= null;
	private Button				m_incremental	= null;
	private int				m_offset		= 0;
	
	public FindDialog(Shell parentShell, IFindReplaceTarget target)
	{
		super(parentShell);
		m_target = target;
	}


	/**
	 * Override to set the title of the dialog.
	 */
	protected void configureShell(Shell shell)
	{
		super.configureShell(shell);
		shell.setText("Find");
	}
	
	/**
	 * Create and layout the SWT controls for the dialog
	 */
	protected Control createDialogArea(Composite parent)
	{
		Composite composite = (Composite) super.createDialogArea(parent);
		GridData gridData;
	
		GridLayout layout = new GridLayout();
		layout.numColumns = 2;
		layout.horizontalSpacing = 10;
		composite.setLayout(layout);
	
		//
		// Find text row
		//
		new Label(composite, SWT.NONE).setText("Find:");
		m_findText = new Text(composite, SWT.BORDER);
		m_findText.setTextLimit(200);
		gridData = new GridData();
		gridData.widthHint = 200;
		m_findText.setLayoutData(gridData);
		
		
		//
		// Options 1
		//
		Group optionsGroup = new Group(composite, SWT.NONE);
		optionsGroup.setText("Options");
		GridLayout glayout = new GridLayout(2, false);
		optionsGroup.setLayout(glayout);
		gridData = new GridData();
		gridData.grabExcessHorizontalSpace = true;
		gridData.horizontalSpan = 2;
		gridData.horizontalAlignment = GridData.CENTER;
		optionsGroup.setLayoutData(gridData);
		
		m_caseSensitive = new Button(optionsGroup, SWT.CHECK);
		m_caseSensitive.setText("Case Sensitive");
		
		m_wrap = new Button(optionsGroup, SWT.CHECK);
		m_wrap.setText("Wrap Search");
		
		m_wholeWord = new Button(optionsGroup, SWT.CHECK);
		m_wholeWord.setText("Whole Word");
		
		m_incremental = new Button(optionsGroup, SWT.CHECK);
		m_incremental.setText("Incremental");
		
		//
		// Find button
		//
		Button findButton = new Button(composite, SWT.PUSH);
		findButton.setText("Find");
		findButton.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent evt)
			{
				m_offset = m_target.findAndSelect(m_offset, m_findText.getText(), true, 
										m_caseSensitive.getSelection(), m_wholeWord.getSelection());
				m_offset++;
			}
		});
		return composite;
	}

}
