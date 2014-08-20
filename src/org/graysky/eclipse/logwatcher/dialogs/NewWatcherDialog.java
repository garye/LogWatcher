package org.graysky.eclipse.logwatcher.dialogs;

import java.io.File;
import java.util.Iterator;
import java.util.ListIterator;
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
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.FileDialog;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.List;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;
import org.graysky.eclipse.logwatcher.LogwatcherPlugin;
import org.graysky.eclipse.logwatcher.filters.Filter;
import org.graysky.eclipse.logwatcher.wizards.NewFilterWizard;

/**
 * The dialog that contains options for creating and editing a watcher.
 */
public class NewWatcherDialog extends Dialog
{
	private boolean m_editMode = false;
	private Combo m_fileCombo;
	private Text m_numLinesText;
	private Text m_intervalText;
	private String m_errorMsg;
	private File m_file;
	private int m_interval;
	private int m_numLines;
	private IDialogSettings m_settings;
	private List m_filterList;
	private Vector m_filters = new Vector();
	private Button m_wholeFileButton;
	private Label m_numLinesLabel;
	private static int DEFAULT_INTERVAL = 1;
	private static int DEFAULT_NUMLINES = 100;


	/**
	 * Constructor for NewWatcherDialog.
	 * 
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
	 * 
	 * @return Vector
	 */
	public Vector getFilters()
	{
		return m_filters;
	}

	private void initControlsForFileSelection(String filename)
    {
        int defaultInterval = DEFAULT_INTERVAL;
        int defaultNumLines = DEFAULT_NUMLINES;

        // Pre-fill the interval and num lines
        try {
        	defaultInterval = m_settings.getInt("interval-" + filename);
        	defaultNumLines = m_settings.getInt("numLines-" + filename);
        }
        catch (NumberFormatException ignore) {
        	// There are no settings for this file. Use the
        	// defaults.
        }
        m_intervalText.setText(Integer.toString(defaultInterval));
        m_intervalText.setSelection(0);
        if (defaultNumLines == Integer.MAX_VALUE)
        {
        	m_wholeFileButton.setSelection(true);
        	m_numLinesText.setText(Integer.toString(DEFAULT_NUMLINES));
        	// Disable the fields
        	m_numLinesLabel.setEnabled(false);
        	m_numLinesText.setEnabled(false);
        }
        else
        {
        	m_wholeFileButton.setSelection(false);
        	// Enable fields
        	m_numLinesLabel.setEnabled(true);
        	m_numLinesText.setEnabled(true);
        	// Set text
        	m_numLinesText.setText(Integer.toString(defaultNumLines));
        	m_numLinesText.setSelection(0);
        }
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

		// Create the widgets
		Button chooserButton = createFileSelectionRow(composite);
		createRefreshIntervalRow(composite);
		createNumLinesGUI(composite);
		createFilterGUI(composite);

		// Register the browse button callback, now that all widgets have been
		// created.
		chooserButton.addSelectionListener(new SelectionAdapter()
		{
			public void widgetSelected(SelectionEvent evt)
			{
				FileDialog dialog = new FileDialog(getShell(), SWT.SINGLE);
				dialog.open();
				if (dialog.getFileName().length() > 0) {
					m_fileCombo.setText(dialog.getFilterPath() + java.io.File.separator + dialog.getFileName());
					initControlsForFileSelection(m_fileCombo.getText());
				}
			}
		});
		handleEditMode();
		return composite;
	}

	/**
	 * Modify the UI we just created to deal with being in Edit Watcher mode
	 * instead of Create Watcher mode.
	 */
	private void handleEditMode()
	{
		//
		// Edit mode handling
		//
		if (m_editMode) {
		    m_fileCombo.setText(m_file.getAbsolutePath());
			m_intervalText.setText(Integer.toString(m_interval));
			if (m_numLines == 0)
			{
				m_numLinesText.setText(Integer.toString(DEFAULT_NUMLINES));
			}
			else
			{
				m_numLinesText.setText(Integer.toString(m_numLines));
			}
			m_numLinesLabel.setEnabled(false);
			m_numLinesText.setEnabled(false);
			m_wholeFileButton.setEnabled(false);
		}
	}

	/**
	 * Create refresh interval UI
	 */
	private void createRefreshIntervalRow(Composite composite)
	{
		GridData gridData;
		new Label(composite, SWT.NONE).setText("Refresh interval (in seconds):");
		m_intervalText = new Text(composite, SWT.BORDER);
		m_intervalText.setTextLimit(4);
		gridData = new GridData(GridData.HORIZONTAL_ALIGN_BEGINNING);
		gridData.horizontalSpan = 2;
		gridData.widthHint = 40;
		m_intervalText.setLayoutData(gridData);
	}

	/**
	 * Create the file selection UI
	 */
	private Button createFileSelectionRow(Composite composite)
	{
		GridData gridData;
		new Label(composite, SWT.NONE).setText("Select a file to watch:");
		m_fileCombo = new Combo(composite, SWT.DROP_DOWN);
		initFileCombo();
		
		gridData = new GridData();
		gridData.widthHint = 200;
		m_fileCombo.setLayoutData(gridData);
		Button chooserButton = new Button(composite, SWT.PUSH);
		chooserButton.setText("Browse...");
		if (m_editMode) {
		    m_fileCombo.setEnabled(false);
			chooserButton.setEnabled(false);
		}
		return chooserButton;
	}

	private void initFileCombo()
    {
	    java.util.List watches = LogwatcherPlugin.getDefault().getRecentWatches();
        ListIterator iter = watches.listIterator(watches.size());
        while (iter.hasPrevious()) {
		    String item = (String) iter.previous();
		    m_fileCombo.add(item);
		}
        
        m_fileCombo.addSelectionListener(new SelectionAdapter()
        {
            public void widgetSelected(SelectionEvent e)
            {
                initControlsForFileSelection(m_fileCombo.getText());
            }
        });
    }

    /**
	 * Creates the UI elements to configure the filters.
	 * 
	 * @param parent The parent composite.
	 */
	protected void createNumLinesGUI(Composite parent)
	{
		//
		// Number of lines grouping
		//
		GridData gridData;

		// Grouping control
		Group numLinesGroup = new Group(parent, SWT.NONE);
		numLinesGroup.setText("Number of lines to show at start");
		GridLayout glayout = new GridLayout(2, false);
		numLinesGroup.setLayout(glayout);
		gridData = new GridData();
		gridData.horizontalSpan = 3;
		numLinesGroup.setLayoutData(gridData);

		// Button to show entire file
		m_wholeFileButton = new Button(numLinesGroup, SWT.CHECK);
		m_wholeFileButton.setText(("Show entire file"));
		gridData = new GridData(GridData.GRAB_HORIZONTAL);
		gridData.horizontalSpan = 2;
		m_wholeFileButton.setLayoutData(gridData);

		// Label show num of lines
		m_numLinesLabel = new Label(numLinesGroup, SWT.NONE);
		m_numLinesLabel.setText("Number of lines:");
		gridData = new GridData(GridData.GRAB_HORIZONTAL);
		gridData.horizontalSpan = 1;
		gridData.widthHint = 140;
		m_numLinesLabel.setLayoutData(gridData);

		// Text field show num of lines
		m_numLinesText = new Text(numLinesGroup, SWT.BORDER);
		m_numLinesText.setTextLimit(4);
		gridData = new GridData(GridData.VERTICAL_ALIGN_BEGINNING);
		gridData.horizontalSpan = 1;
		gridData.widthHint = 40;
		m_numLinesText.setLayoutData(gridData);

		// Add button listener
		//
		m_wholeFileButton.addSelectionListener(new SelectionAdapter()
		{
			public void widgetSelected(SelectionEvent evt)
			{
				if (m_wholeFileButton.getSelection())
				{
					// Button is selected, disable number of lines.
					//
					m_numLinesLabel.setEnabled(false);
					m_numLinesText.setEnabled(false);
				}
				else
				{
					// Button is disabled, enable number of lines.
					//
					m_numLinesLabel.setEnabled(true);
					m_numLinesText.setEnabled(true);
				}
			}
		});
	}

	/**
	 * Creates the UI elements to configure the filters.
	 * 
	 * @param parent The parent composite.
	 */
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
		gridData.heightHint = 120;
		gridData.verticalSpan = 4;
		m_filterList.setLayoutData(gridData);

		// New filter button
		Button newButton = new Button(filterGroup, SWT.PUSH);
		newButton.setText("New Filter...");
		newButton.setLayoutData(new GridData(GridData.VERTICAL_ALIGN_BEGINNING));
		
		// New filter button
		Button addButton = new Button(filterGroup, SWT.PUSH);
		addButton.setText("Add Saved Filter...");
		addButton.setLayoutData(new GridData(GridData.VERTICAL_ALIGN_BEGINNING));
		
		// Save filter button
		final Button saveButton = new Button(filterGroup, SWT.PUSH);
		saveButton.setText("Save Filter...");
		saveButton.setLayoutData(new GridData(GridData.VERTICAL_ALIGN_BEGINNING));
		saveButton.setEnabled(false);

		// Remove filter button
		final Button removeButton = new Button(filterGroup, SWT.PUSH);
		removeButton.setText("Remove Filter");
		removeButton.setLayoutData(new GridData(GridData.VERTICAL_ALIGN_BEGINNING));
		removeButton.setEnabled(false);

		// Remove All filters button
		final Button removeAllButton = new Button(filterGroup, SWT.PUSH);
		removeAllButton.setText("Remove All");
		removeAllButton.setLayoutData(new GridData(GridData.VERTICAL_ALIGN_BEGINNING));
		removeAllButton.setEnabled(m_filterList.getItemCount() > 0);

		// Event listeners
		//
		newButton.addSelectionListener(new SelectionAdapter()
		{
			public void widgetSelected(SelectionEvent evt)
			{
				// Launch the wizard
				NewFilterWizard wizard = new NewFilterWizard(m_editMode);
				WizardDialog dialog = new WizardDialog(m_filterList.getShell(), wizard);
				int status = dialog.open();
				if (status == Window.OK && wizard.getFilter() != null) {
					m_filters.add(wizard.getFilter());
					m_filterList.add(wizard.getFilter().getDescription());
				}
				removeAllButton.setEnabled(m_filterList.getItemCount() > 0);
			}
		});
		addButton.addSelectionListener(new SelectionAdapter()
        {
            public void widgetSelected(SelectionEvent e)
            {
                // Launch the add saved dialog
                AddSavedFilterDialog dialog = new AddSavedFilterDialog(getShell());
                dialog.open();
                m_filters.addAll(dialog.getFiltersToAdd());
                for (Iterator iter = dialog.getFiltersToAdd().iterator(); iter.hasNext();) {
                    Filter filter = (Filter) iter.next();
                    m_filterList.add(filter.getDescription());
                }
            }
        });
		saveButton.addSelectionListener(new SelectionAdapter()
        {
            public void widgetSelected(SelectionEvent e)
            {
                // Launch the add saved dialog
                int filterIndex = m_filterList.getSelectionIndex();
                Filter filter = (Filter) m_filters.get(filterIndex);
                SaveFilterDialog dialog = new SaveFilterDialog(getShell(), filter);
               
                if (dialog.open() == Window.OK) {
                    LogwatcherPlugin.getDefault().addSavedFilter(filter);
                }   
            }
        });
		removeButton.addSelectionListener(new SelectionAdapter()
		{
			public void widgetSelected(SelectionEvent evt)
			{
				int[] selections = m_filterList.getSelectionIndices();
				for (int i = 0; i < selections.length; i++) {
					m_filterList.remove(selections[i]);
					m_filters.remove(selections[i]);
					if (m_filterList.getSelectionCount() == 0) {
						removeButton.setEnabled(false);
					}
					// Do enabling of remove all button
					removeAllButton.setEnabled(m_filterList.getItemCount() > 0);
					saveButton.setEnabled(false);
				}
			}
		});
		removeAllButton.addSelectionListener(new SelectionAdapter()
		{
			public void widgetSelected(SelectionEvent evt)
			{
				m_filterList.removeAll();
				m_filters.clear();

				// Disable buttons
				removeButton.setEnabled(false);
				removeAllButton.setEnabled(false);
				saveButton.setEnabled(false);
			}
		});

		// Now that the remove button has been created, add the selection
		// listener
		// to the filter list.
		m_filterList.addSelectionListener(new SelectionAdapter()
		{
			public void widgetSelected(SelectionEvent evt)
			{
			    if (m_filterList.getSelectionCount() == 1) {
				    saveButton.setEnabled(true);
				}
			    else {
			        saveButton.setEnabled(false);
			    }
			    
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
	 * 
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
	 * 
	 * @return int
	 */
	public int getInterval()
	{
		return m_interval;
	}

	/**
	 * Returns the number of lines to show.
	 * 
	 * @return int
	 */
	public int getNumLines()
	{
		return m_numLines;
	}

	/**
	 * Sets the interval.
	 * 
	 * @param interval The interval to set
	 */
	public void setInterval(int interval)
	{
		m_interval = interval;
	}

	/**
	 * Sets the numLines.
	 * 
	 * @param numLines The numLines to set
	 */
	public void setNumLines(int numLines)
	{
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
		m_file = new File(m_fileCombo.getText());
		if (!m_file.exists() || !m_file.isFile()) {
			m_errorMsg = "File not found:\n" + m_fileCombo.getText();
			return false;
		}
		if (!m_file.canRead()) {
			m_errorMsg = "Cannot read file:\n" + m_fileCombo.getText();
			return false;
		}
		try {
			if (m_wholeFileButton.getSelection())
			{
				// They want whole file.
				m_numLines = Integer.MAX_VALUE;
			}
			else
			{
				Integer i = new Integer(m_numLinesText.getText());
				if (i.intValue() < 0) {
					throw new NumberFormatException();
				}
				m_numLines = i.intValue();
			}
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
			m_settings.put("interval-" + m_file.getAbsolutePath(), m_interval);
			m_settings.put("numLines-" + m_file.getAbsolutePath(), m_numLines);
			LogwatcherPlugin.getDefault().addRecentWatch(m_file.getAbsolutePath());
			super.okPressed();
		}
		else {
			// Show error msg
			MessageDialog.openError(getShell(), "LogWatcher", m_errorMsg);
		}
	}
}