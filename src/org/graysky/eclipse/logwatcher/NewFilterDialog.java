package org.graysky.eclipse.logwatcher;

import org.eclipse.jface.dialogs.Dialog;
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
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;
import org.graysky.eclipse.logwatcher.filters.Filter;
import org.graysky.eclipse.logwatcher.filters.FilterAction;
import org.graysky.eclipse.logwatcher.filters.HighlightAction;

public class NewFilterDialog extends Dialog
{
	private Composite	m_dialogArea;
	private Text		m_filterText;
	private Composite	m_actionOptions;
	private Color		m_color;
	private Combo		m_actionsCombo;
	private Filter		m_filter;
	
    public NewFilterDialog(Shell parent)
    {
        super(parent);
    }


	/**
	 * Override to set the title of the dialog.
	 */
	protected void configureShell(Shell shell)
	{
		super.configureShell(shell);
		shell.setText("Create New Filter");
	}

	/**
	 * Create and layout the SWT controls for the dialog
	 */
	protected Control createDialogArea(Composite parent)
	{
		Composite m_dialogArea = (Composite) super.createDialogArea(parent);
		GridData gridData;
		
		GridLayout layout = new GridLayout();
		layout.numColumns = 3;
		layout.horizontalSpacing = 10;
		layout.verticalSpacing = 20;
		m_dialogArea.setLayout(layout);
	
		//
		// First row
		//
		new Label(m_dialogArea, SWT.NONE).setText("Where text ");
		
		Combo containsCombo = new Combo(m_dialogArea, SWT.DROP_DOWN | SWT.READ_ONLY);
		containsCombo.add("contains");
		containsCombo.add("does not contain");
		containsCombo.select(0);
		
		m_filterText = new Text(m_dialogArea, SWT.BORDER);
		m_filterText.setTextLimit(200);
		gridData = new GridData();
		gridData.widthHint = 200;
		m_filterText.setLayoutData(gridData);
		
		//
		// Second row
		//
		new Label(m_dialogArea, SWT.NONE).setText("Take this action:");
		
		m_actionsCombo = new Combo(m_dialogArea, SWT.DROP_DOWN | SWT.READ_ONLY);
		m_actionsCombo.add("Highlight Line");
		m_actionsCombo.add("Skip Line");
		m_actionsCombo.select(0);
		gridData = new GridData();
		gridData.horizontalSpan = 2;
		m_actionsCombo.setLayoutData(gridData);
		
		//
		// Filter actions		
		//
		Group filterGroup = new Group(m_dialogArea, SWT.NONE);
		filterGroup.setText("Action Details");
		GridLayout glayout = new GridLayout(3, false);
		filterGroup.setLayout(glayout);
		gridData = new GridData(GridData.FILL_HORIZONTAL);
		gridData.horizontalSpan = 3;
		filterGroup.setLayoutData(gridData);
		
		createHighlightOptions(filterGroup);
		
		return m_dialogArea;	
	}
	
	private void createHighlightOptions(Composite parent)
	{
		new Label(parent, SWT.NONE).setText("Highlight color:");
		
		final Canvas c = new Canvas(parent, SWT.NONE);
		GridData gridData = new GridData(GridData.HORIZONTAL_ALIGN_FILL);
		gridData.heightHint = 20;
		gridData.widthHint = 20;
		c.setLayoutData(gridData);
		c.setBackground(new Color(getShell().getDisplay(), 0,0,0));
		
		Button colorButton = new Button(parent, SWT.PUSH);
		colorButton.setText("Select...");
		colorButton.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent evt)
			{
				ColorDialog dialog = new ColorDialog(getShell());
				dialog.open();
				
				if (dialog.getRGB() != null) {
					m_color = new Color(getShell().getDisplay(), dialog.getRGB());
					c.setBackground(m_color);
				}
			}
		});
	}
	
	private Composite createHideOptions(Composite parent)
	{
		return new Composite(parent, SWT.NONE);	
	}
	
	/**
	 * Returns the filter created by this dialog.
	 * @return Filter
	 */
	public Filter getFilter() 
	{
		return m_filter;
	}
	
	protected void okPressed()
	{
		// validate();
		switch (m_actionsCombo.getSelectionIndex()) {
			case 0:
				m_filter = new Filter();
				m_filter.addAction(new HighlightAction(m_color));
				m_filter.setPattern(m_filterText.getText());
				break;
				
			default:
				m_filter = null;
				break;
		}
		
		super.okPressed();
	}

	

}
