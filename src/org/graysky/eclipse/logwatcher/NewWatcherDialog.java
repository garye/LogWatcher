package org.graysky.eclipse.logwatcher;

import java.io.File;
import java.util.Iterator;
import java.util.Vector;

import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.jface.dialogs.IDialogSettings;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.window.Window;
import org.eclipse.jface.wizard.WizardDialog;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.FileDialog;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.List;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;
import org.graysky.eclipse.logwatcher.filters.Filter;

/**
 * The dialog that contains options for creating a new watcher.
 */
public class NewWatcherDialog extends Dialog
{
    private boolean				m_editMode 		= false;
	private Text				m_fileText;
	private Text				m_numLinesText;
	private Text				m_intervalText;
	private String				m_errorMsg;
	private File				m_file;
	private int					m_interval;
	private int					m_numLines;
	private IDialogSettings		m_settings;
	private List				m_filterList;
	private Vector				m_filters		= new Vector();
	
	private static int		DEFAULT_INTERVAL	= 1;
	private static int		DEFAULT_NUMLINES	= 100;
	
	/**
	 * Constructor for NewWatcherDialog.
	 * @param shell
	 */
	public NewWatcherDialog(Shell shell, boolean editMode)
	{
		super(shell);
		m_settings = LogwatcherPlugin.getDefault().getDialogSettings();
		m_editMode = editMode;
	}

	/**
	 * Override to set the title of the dialog.
	 */
	protected void configureShell(Shell shell)
	{
		super.configureShell(shell);
		if (m_editMode) {
            shell.setText("Edit Watcher");
		}
		else {
			shell.setText("Create New Watcher");
		}
	}

	/**
	 * Returns the filters for this watcher.
	 * @return Vector
	 */
	public Vector getFilters()
	{
		return m_filters;
	}


	/**
	 * Create and layout the SWT controls for the dialog
	 */
	protected Control createDialogArea(Composite parent)
	{
		Composite composite = (Composite) super.createDialogArea(parent);
		GridData gridData;
		
		GridLayout layout = new GridLayout();
		layout.numColumns = 3;
		layout.horizontalSpacing = 10;
		composite.setLayout(layout);
		
		//
		// File row
		//
		new Label(composite, SWT.NONE).setText("Select a file to watch:");
		m_fileText = new Text(composite, SWT.BORDER);
		m_fileText.setTextLimit(200);
		gridData = new GridData();
		gridData.widthHint = 200;
		m_fileText.setLayoutData(gridData);
		Button chooserButton = new Button(composite, SWT.PUSH);
		chooserButton.setText("Browse...");
		if (m_editMode) {
		    m_fileText.setEnabled(false);
		    chooserButton.setEnabled(false);
		}
		
		//
		// Number of lines row
		//
		new Label(composite, SWT.NONE).setText("Number of lines to show:");
		m_numLinesText = new Text(composite, SWT.BORDER);
		m_numLinesText.setTextLimit(4);
		gridData = new GridData(GridData.HORIZONTAL_ALIGN_BEGINNING);
		gridData.horizontalSpan = 2;
		gridData.widthHint = 40;
	    m_numLinesText.setLayoutData(gridData);

		//
		// Refresh interval row
		//
		new Label(composite, SWT.NONE).setText("Refresh interval (in seconds):");
		m_intervalText = new Text(composite, SWT.BORDER);
		m_intervalText.setTextLimit(4);
		gridData = new GridData(GridData.HORIZONTAL_ALIGN_BEGINNING);
		gridData.horizontalSpan = 2;
        gridData.widthHint = 40;
		m_intervalText.setLayoutData(gridData);
		
		// Register the browse button callback, now that all widgets have been created.
		chooserButton.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent evt)
			{
				FileDialog dialog = new FileDialog(getShell(), SWT.SINGLE);
				dialog.open();

				if (dialog.getFileName().length() > 0) {
					m_fileText.setText(dialog.getFilterPath() + java.io.File.separator + dialog.getFileName());
			
                    int defaultInterval = DEFAULT_INTERVAL;
                    int defaultNumLines = DEFAULT_NUMLINES;
					
					// Pre-fill the interval and num lines
					try {
						defaultInterval = m_settings.getInt("interval-" + dialog.getFileName());
						defaultNumLines = m_settings.getInt("numLines-" + dialog.getFileName());
					}
					catch (NumberFormatException ignore) {
						// There are no settings for this file. Use the defaults.
					}
					
			
					m_intervalText.setText(Integer.toString(defaultInterval));
					m_intervalText.setSelection(0);
					m_numLinesText.setText(Integer.toString(defaultNumLines));
					m_numLinesText.setSelection(0);
				}
			}
		});
		
		//
		// Filters
		//
		createFilterGUI(composite);
		
		
		if (m_editMode) {
		    Label changes = new Label(composite, SWT.NONE);
		    changes.setText("Changes will take effect after the next watcher update.");
            gridData = new GridData(GridData.HORIZONTAL_ALIGN_BEGINNING);
            gridData.horizontalSpan = 3;
            changes.setLayoutData(gridData);
            
            m_intervalText.setText(Integer.toString(m_interval));
            m_numLinesText.setText(Integer.toString(m_numLines));
            m_fileText.setText(m_file.getAbsolutePath());  
		}
		
		return composite;
	}

	protected void createFilterGUI(Composite parent)
	{
		GridData gridData;
	
		Group filterGroup = new Group(parent, SWT.NONE);
		filterGroup.setText("Filters");
		GridLayout glayout = new GridLayout(2, false);
		filterGroup.setLayout(glayout);
		gridData = new GridData();
		gridData.horizontalSpan = 3;
		filterGroup.setLayoutData(gridData);
		
		m_filterList = new List(filterGroup, SWT.MULTI | SWT.BORDER | SWT.V_SCROLL | SWT.H_SCROLL);
		gridData = new GridData(GridData.GRAB_HORIZONTAL);
		gridData.widthHint = 300;
		gridData.heightHint = 60;
		gridData.verticalSpan = 2;
		m_filterList.setLayoutData(gridData);
		
		Button newButton = new Button(filterGroup, SWT.PUSH);
		newButton.setText("New Filter...");
		newButton.setLayoutData(new GridData(GridData.VERTICAL_ALIGN_BEGINNING));
		newButton.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent evt)
			{
				// Launch the wizard
                NewFilterWizard wizard = new NewFilterWizard();
                WizardDialog dialog = new WizardDialog(m_filterList.getShell(), wizard);
                int status = dialog.open();
                
				if (status ==  Window.OK && wizard.getFilter() != null) {
					m_filters.add(wizard.getFilter());
					m_filterList.add(wizard.getFilter().getDescription());
				}
			}
		});
		
		final Button removeButton = new Button(filterGroup, SWT.PUSH);
		removeButton.setText("Remove Filter");
		removeButton.setLayoutData(new GridData(GridData.VERTICAL_ALIGN_BEGINNING));
		removeButton.setEnabled(false);
		removeButton.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent evt)
			{
				int[] selections = m_filterList.getSelectionIndices();
				for (int i = 0; i < selections.length; i++) {
					m_filterList.remove(selections[i]);
					m_filters.remove(selections[i]);
					if (m_filterList.getSelectionCount() == 0) {
						removeButton.setEnabled(false);
					}
				}
			}
		});
		
		// Now that the remove button has been created, add the selection listener
		// to the filter list.
		m_filterList.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent evt)
			{
				if (m_filterList.getSelectionCount() > 0) {
					removeButton.setEnabled(true);
				}
				else {
					removeButton.setEnabled(false);
				}
			}
		});
		
		if (m_editMode) {
            for (Iterator iter = m_filters.iterator(); iter.hasNext();) {
                Filter element = (Filter) iter.next();
                m_filterList.add(element.getDescription());
            }
		}
	}

	/**
	 * Gets the file to watch
	 * @return File
	 */
	public File getFile()
	{
		return m_file;
	}
	
	public void setFile(File f)
	{
	    m_file = f;
	}
	
	/**
	 * Returns the refresh interval.
	 * @return int
	 */
	public int getInterval()
	{
		return m_interval;
	}

	/**
	 * Returns the number of lines to show.
	 * @return int
	 */
	public int getNumLines()
	{
		return m_numLines;
	}

	/**
	 * Sets the interval.
	 * @param interval The interval to set
	 */
	public void setInterval(int interval) {
		m_interval = interval;
	}

	/**
	 * Sets the numLines.
	 * @param numLines The numLines to set
	 */
	public void setNumLines(int numLines) {
		m_numLines = numLines;
	}

	public void setFilters(Vector filters)
	{
	    m_filters = filters;
	}

	/**
	 * Validate the user input
	 */
	protected boolean validate()
	{
		m_file = new File(m_fileText.getText());
		if (!m_file.exists() || !m_file.isFile()) {
			m_errorMsg = "File not found:\n" + m_fileText.getText();
			return false;
		}
		
		try {
			Integer i = new Integer(m_numLinesText.getText());
			if (i.intValue() <= 0) {
				throw new NumberFormatException();
			}
			
			m_numLines = i.intValue();
		}
		catch (NumberFormatException e) {
			m_errorMsg = "Number of lines to show must be a positive integer.";
			return false;
		}
		
		try {
			Integer i = new Integer(m_intervalText.getText());
			if (i.intValue() <= 0) {
				throw new NumberFormatException();
			}
			
			m_interval = i.intValue();
		}
		catch (NumberFormatException e) {
			m_errorMsg = "Refresh interval must be a positive integer.";
			return false;
		}
		
		return true;
	}

	/**
	 * Validate user input, set return values.
	 */
	protected void okPressed()
	{
		if (validate()) {
			// Input was valid
			m_settings.put("interval-" + m_file.getName(), m_interval);
			m_settings.put("numLines-" + m_file.getName(), m_numLines);
				
			super.okPressed();
		}
		else {
			// Show error msg
			MessageDialog.openError(getShell(), "LogWatcher", m_errorMsg);
		}	
	}

	
}
