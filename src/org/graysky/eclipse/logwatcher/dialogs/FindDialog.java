package org.graysky.eclipse.logwatcher.dialogs;

import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.jface.text.IFindReplaceTarget;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
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

/**
 * A limited clone of the normal Eclipse "Find" dialog, used for searching
 * through an open watcher's text window.
 */
public class FindDialog extends Dialog
{
	private Text				m_findText 		= null;
	private IFindReplaceTarget	m_target		= null;
	private Button				m_caseSensitive	= null;
	private Button				m_wrap			= null;
	private Button				m_wholeWord		= null;
	private Button				m_incremental	= null;
	private Button				m_findButton	= null;
	private Label				m_statusLabel	= null;
	private int					m_offset		= 0;
	
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
		layout.verticalSpacing = 10;
		composite.setLayout(layout);
	
		//
		// Find text row
		//
		new Label(composite, SWT.NONE).setText("Find:");
		m_findText = new Text(composite, SWT.BORDER);
		m_findText.setTextLimit(200);
		gridData = new GridData();
		gridData.widthHint = 160;
		m_findText.setLayoutData(gridData);
		m_findText.addModifyListener(new ModifyListener() {
			public void modifyText(ModifyEvent e)
			{
				if (m_findText.getText().length() > 0) {
					m_findButton.setEnabled(true);
					
					if (m_incremental.getSelection()) {
						m_offset = search(m_findText.getText());	
					}
				}
				else {
					m_findButton.setEnabled(false);
				}
			}
		});	
		
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
		gridData.horizontalAlignment = GridData.FILL;
		optionsGroup.setLayoutData(gridData);
		
		m_caseSensitive = new Button(optionsGroup, SWT.CHECK);
		m_caseSensitive.setText("Case Sensitive");
		
		m_wrap = new Button(optionsGroup, SWT.CHECK);
		m_wrap.setText("Wrap Search");
		
		m_wholeWord = new Button(optionsGroup, SWT.CHECK);
		m_wholeWord.setText("Whole Word");
		
		m_incremental = new Button(optionsGroup, SWT.CHECK);
		m_incremental.setText("Incremental");
		
		return composite;
	}

	protected void createButtonsForButtonBar(Composite parent)
	{
		//
		// Find button
		//
		m_findButton = new Button(parent, SWT.PUSH);
		m_findButton.setText("Find");
		m_findButton.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent evt)
			{
				int offset = search(m_findText.getText());
				if (offset == -1) {
					if (m_wrap.getSelection()) {
						m_offset = 0;
					}
					// Else, leave m_offset alone
				}
				else {
					m_offset = offset + 1;
				}
			}
		});
		m_findButton.setEnabled(false);
		getShell().setDefaultButton(m_findButton);
		
		GridData gridData = new GridData(GridData.HORIZONTAL_ALIGN_FILL);
		gridData.horizontalSpan = 2;
		gridData.grabExcessHorizontalSpace = true;
		m_findButton.setLayoutData(gridData);
		
		// Status label
		m_statusLabel = new Label(parent, SWT.NONE);
		
		// This doesn't seem right, but we seem to need to set the initial width
		// of the label text to at least as long as the text we will be setting
		// it to later... 
		m_statusLabel.setText("                                       ");
		
		// Close button
		Button closeButton = new Button(parent, SWT.PUSH);
		closeButton.setText("Close");
		gridData = new GridData(GridData.HORIZONTAL_ALIGN_END);
		closeButton.setLayoutData(gridData);
		closeButton.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent evt)
			{
				close();
			}
		});
	}

	protected Control createButtonBar(Composite parent) {
		Composite composite = new Composite(parent, SWT.NONE);
		GridLayout layout = new GridLayout();
		layout.numColumns = 2;
		layout.horizontalSpacing = 10;
		layout.verticalSpacing = 10;
		composite.setLayout(layout);
		GridData gridData = new GridData(GridData.HORIZONTAL_ALIGN_FILL);
		gridData.horizontalSpan = 2;
		composite.setLayoutData(gridData);
		
		createButtonsForButtonBar(composite);
		
		return composite;
	}

	protected int  search(String text)
	{
		int offset = m_target.findAndSelect(m_offset, text, true, 
										  m_caseSensitive.getSelection(), 
										  m_wholeWord.getSelection());
		if (offset == -1) {
			m_statusLabel.setText("String not found");
			m_statusLabel.redraw();
		}
		
		return offset;
	}
	
}
