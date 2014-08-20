package org.graysky.eclipse.logwatcher.wizards;

import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.jface.wizard.IWizardPage;
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

/**
 * The UI for the "Highlight" filter options page.
 */
public class HighlightOptionsPage extends WizardPage
{
	private Text		m_filterText;
	private Composite	m_actionOptions;
	private Color		m_bgcolor 			= null;
	private Color		m_startingBgColor	= null;
	private Color		m_fgcolor			= null;
	private Color		m_startingFgColor	= null;
	private Combo		m_actionsCombo;

	/**
	 * Constructor for HighlightOptionsPage.
	 * @param pageName
	 */
	public HighlightOptionsPage(String pageName)
	{
		super(pageName);
	}

	/**
	 * Constructor for HighlightOptionsPage.
	 */
	public HighlightOptionsPage(String pageName, String title, ImageDescriptor titleImage)
	{
		super(pageName, title, titleImage);
	}

	public void createControl(Composite parent) 
	{
		Composite composite = new Composite(parent, SWT.NONE);
		setControl(composite);
	
		// Get red & white as default starting colors. 
		// Note: We didn't allocate these colors, so don't dispose them.
		//
		m_startingFgColor = getShell().getDisplay().getSystemColor(SWT.COLOR_RED);
		m_startingBgColor = getShell().getDisplay().getSystemColor(SWT.COLOR_WHITE);
		
        // Initialize choice to the default
        m_fgcolor = new Color(getShell().getDisplay(), m_startingFgColor.getRGB() );
        m_bgcolor = new Color(getShell().getDisplay(), m_startingBgColor.getRGB() );
        
		GridData gridData;
		
		GridLayout layout = new GridLayout();
		layout.numColumns = 3;
		layout.horizontalSpacing = 10;
		layout.verticalSpacing = 20;
		composite.setLayout(layout);
	
		new Label(composite, SWT.NONE).setText("Foreground color:");
		
		final Canvas cfg = new Canvas(composite, SWT.NONE);
		gridData = new GridData(GridData.HORIZONTAL_ALIGN_FILL);
		gridData.heightHint = 20;
		gridData.widthHint = 20;
		cfg.setLayoutData(gridData);
		cfg.setBackground(m_startingFgColor);
		
		Button colorButton = new Button(composite, SWT.PUSH);
		colorButton.setText("Select...");
		colorButton.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent evt)
			{
				ColorDialog dialog = new ColorDialog(getShell());
				dialog.open();
				
				if (dialog.getRGB() != null) {
					if (m_fgcolor != null) {
                        // Clean up any previous choice
						m_fgcolor.dispose();	
					}
					
					m_fgcolor = new Color(getShell().getDisplay(), dialog.getRGB());
					cfg.setBackground(m_fgcolor);
				}
			}
		});
		
		new Label(composite, SWT.NONE).setText("Background color:");
		
		final Canvas cbg = new Canvas(composite, SWT.NONE);
		gridData = new GridData(GridData.HORIZONTAL_ALIGN_FILL);
		gridData.heightHint = 20;
		gridData.widthHint = 20;
		cbg.setLayoutData(gridData);
		cbg.setBackground(m_startingBgColor);
		
		Button bgColorButton = new Button(composite, SWT.PUSH);
		bgColorButton.setText("Select...");
		bgColorButton.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent evt)
			{
				ColorDialog dialog = new ColorDialog(getShell());
				dialog.open();
				
				if (dialog.getRGB() != null) {
					if (m_bgcolor != null) {
                        // Clean up any previous choice
						m_bgcolor.dispose();	
					}
					
					m_bgcolor = new Color(getShell().getDisplay(), dialog.getRGB());
					cbg.setBackground(m_bgcolor);
				}
			}
		});

		setPageComplete(true);
	}

	public String getTitle()
	{
		return "Set the highlight colors.";
	}

	public IWizardPage getNextPage()
	{
		return null;
	}

    public void dispose()
    {
        super.dispose();
    }

    public Color getFgColor()
    {
        return m_fgcolor;
    }
    
    public Color getBgColor()
    {
    	return m_bgcolor;
    }

}
