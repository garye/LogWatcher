package org.graysky.eclipse.logwatcher.views;

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.Reader;
import java.util.Iterator;
import java.util.Vector;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.eclipse.core.runtime.IPath;
import org.eclipse.jface.action.Action;
import org.eclipse.jface.action.IMenuListener;
import org.eclipse.jface.action.IMenuManager;
import org.eclipse.jface.action.IToolBarManager;
import org.eclipse.jface.action.MenuManager;
import org.eclipse.jface.action.Separator;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.preference.PreferenceConverter;
import org.eclipse.jface.text.Document;
import org.eclipse.jface.text.TextViewer;
import org.eclipse.jface.util.IPropertyChangeListener;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.CTabFolder;
import org.eclipse.swt.custom.CTabItem;
import org.eclipse.swt.custom.LineStyleEvent;
import org.eclipse.swt.custom.LineStyleListener;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.graphics.Font;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.ui.IActionBars;
import org.eclipse.ui.IViewSite;
import org.eclipse.ui.IWorkbenchActionConstants;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.actions.ActionFactory;
import org.eclipse.ui.part.ViewPart;
import org.graysky.eclipse.logwatcher.LogwatcherPlugin;
import org.graysky.eclipse.logwatcher.actions.ClearDisplayAction;
import org.graysky.eclipse.logwatcher.actions.CloseWatcherAction;
import org.graysky.eclipse.logwatcher.actions.CopyAction;
import org.graysky.eclipse.logwatcher.actions.EditWatcherAction;
import org.graysky.eclipse.logwatcher.actions.FindAction;
import org.graysky.eclipse.logwatcher.actions.NewWatcherAction;
import org.graysky.eclipse.logwatcher.actions.ToggleScrollingAction;
import org.graysky.eclipse.logwatcher.filters.Filter;
import org.graysky.eclipse.logwatcher.filters.FilterLoader;
import org.graysky.eclipse.logwatcher.watchers.TextFileWatcher;
import org.graysky.eclipse.logwatcher.watchers.WatcherUpdateListener;
import org.graysky.eclipse.util.BoundedList;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;

/** 
 * The main view for LogWatcher. Still has lots of code generated from the PDE
 * wizard that is not used.
 */
public class LogWatcherView extends ViewPart
{
	private Action 		m_closeAction = null;
	private Action 		m_newAction = null;
	private Action			m_clearAction = null;
	private Action			m_findAction = null;
	private Action			m_scrollAction = null;
	private Action			m_editAction = null;
	private CTabFolder 	m_folder = null;
	private Action			m_copyAction = null;
	private Vector 		m_watchers = new Vector();

	private static final String WATCHER_STATE_FILENAME	= "watcherState.xml";
	
	/**
	 * Listen for changes to Logwatcher preferences. Currently, only changes to
	 * the font are noticed.
	 */
	private IPropertyChangeListener m_propListener = new IPropertyChangeListener () {
		public void propertyChange(org.eclipse.jface.util.PropertyChangeEvent event)
		{
			if (event.getProperty().equals("logwatcherFont")) {
				LogwatcherPlugin plugin = LogwatcherPlugin.getDefault();
				plugin.putFont("logwatcherFont", 
					PreferenceConverter.getFontDataArray(plugin.getPreferenceStore(), "logwatcherFont"));
					

				for (Iterator iter = m_watchers.iterator(); iter.hasNext();) {
					WatcherEntry entry = (WatcherEntry) iter.next();
					entry.getViewer().getTextWidget().setFont(plugin.getFont("logwatcherFont"));	
				}		
			}
		}
	};
	
  			
	public void init(IViewSite site) throws PartInitException
	{
		super.init(site);
		
		// Register a property change listener for the preferences page.
		LogwatcherPlugin.getDefault().getPreferenceStore().addPropertyChangeListener(m_propListener);
	}

	/**
	 * This is a callback that will allow us
	 * to create the viewer and initialize it.
	 */
	public void createPartControl(Composite parent)
	{
		m_folder = new CTabFolder(parent, SWT.NONE);

		// Add listeners so the title of the view is always accurate
		m_folder.addSelectionListener(new SelectionListener() {
			
			public void widgetSelected(SelectionEvent e)
			{
				CTabItem item = (CTabItem) e.item;
				setViewTitle(item.getText());	
			}
			
			public void widgetDefaultSelected(SelectionEvent e)
			{
				CTabItem item = (CTabItem) e.item;
				setViewTitle(item.getText());	
			}
		});

		makeActions();
		contributeToActionBars();
		
		setGlobalActionHandlers();
		loadWatcherState();
	}

	/**
	 * Load the watcher state from a previous instance. 
	 */
	private void loadWatcherState()
	{
		if (LogwatcherPlugin.getDefault().getPreferenceStore().getBoolean("saveWatchers")) {
			WatcherLoader loader = new WatcherLoader();
			IPath path = LogwatcherPlugin.getDefault().getStateLocation();
			path = path.addTrailingSeparator();
			path = path.append(WATCHER_STATE_FILENAME);
			try {
				loader.loadWatchers(new FileReader(path.toFile()));
			}
			catch (Exception e) {
				LogwatcherPlugin.getDefault().logError("Error loading watcher state", e);
			}
		}
	}

	private void setGlobalActionHandlers()
	{
		getViewSite().getActionBars().setGlobalActionHandler(
			ActionFactory.FIND.getId(), m_findAction);
			
		getViewSite().getActionBars().setGlobalActionHandler(
                ActionFactory.COPY.getId(), m_copyAction);
	}

	private void setViewTitle(String name)
	{
		String title = "LogWatcher";
		if (name != null) {
			title += " - " + name;
		}
		
		setPartName(title);
	}

	private void contributeToActionBars()
	{
		IActionBars bars = getViewSite().getActionBars();
		fillLocalToolBar(bars.getToolBarManager());
	}


	private void fillContextMenu(IMenuManager manager) 
	{
		manager.add(m_newAction);
		manager.add(new Separator("new"));
		manager.add(m_copyAction);
		manager.add(m_findAction);
		manager.add(m_clearAction);
		manager.add(new Separator("other"));
		manager.add(m_editAction);
		manager.add(m_scrollAction);
		manager.add(m_closeAction);
		manager.add(new Separator("Additions"));
	}
	
	private void fillLocalToolBar(IToolBarManager manager)
	{
		manager.add(m_newAction);
        manager.add(m_editAction);
		manager.add(m_clearAction);
		manager.add(m_scrollAction);
		manager.add(m_closeAction);
	}

	/**
	 * Close and dispose of the currently selected watcher
	 */
	public void closeSelectedWatcher()
	{
		WatcherEntry entry = getSelectedEntry();
		if (entry != null) {
			entry.dispose();	
			m_watchers.remove(entry);
			if (m_folder.getItemCount() == 0) {
				setViewTitle(null);
				m_closeAction.setEnabled(false);
				m_clearAction.setEnabled(false);
				m_scrollAction.setEnabled(false);
				m_editAction.setEnabled(false);
			}
			
			saveWatcherState();
		}
	}

	/**
	 * Create the actions and set their default states.
	 */
	private void makeActions() 
	{
		// Close the currently selected watcher
		m_closeAction = new CloseWatcherAction(this);
		m_closeAction.setEnabled(false);
		
		m_newAction = new NewWatcherAction(this);
		
		// Edit a watcher
		m_editAction = new EditWatcherAction(this);
		m_editAction.setEnabled(false);

		// Clear the display	
		m_clearAction = new ClearDisplayAction(this);
		m_clearAction.setEnabled(false);	
		
		// Find in log file
		m_findAction = new FindAction(this);
		
		// Copy
		m_copyAction = new CopyAction(this);
		
		// Toggle scrolling
		m_scrollAction = new ToggleScrollingAction(this);
		m_scrollAction.setChecked(false);
		m_scrollAction.setEnabled(false);
	}
	
	/**
	 * Add a Watcher for the specified File. Will set up the UI
	 * components as well as the actual TextFileWatcher.
	 */
	public void addWatcher(File file, int interval, int numLines, Vector filters, boolean saveState)
	{	
		// Add the new tab
		CTabItem newItem = new CTabItem(m_folder, 0);
		newItem.setText(file.getName());
		setViewTitle(file.getName());

		m_folder.setSelection(newItem);
		
		// Create the text viewer and associated document
		final TextViewer viewer = new TextViewer(m_folder, SWT.H_SCROLL | SWT.V_SCROLL);
		newItem.setControl(viewer.getControl());
		final Document newDoc = new Document();
		viewer.setDocument(newDoc);
		viewer.setEditable(false);
		
		// Add a context menu to the text viewer
		MenuManager menuMgr = new MenuManager("#PopupMenu");
		menuMgr.setRemoveAllWhenShown(true);
		menuMgr.addMenuListener(new IMenuListener() {
			public void menuAboutToShow(IMenuManager manager) {
				LogWatcherView.this.fillContextMenu(manager);
			}
		});
		Menu menu = menuMgr.createContextMenu(viewer.getControl());
		viewer.getControl().setMenu(menu);
		getSite().registerContextMenu(menuMgr, viewer);
		
		// Add the watcher
		TextFileWatcher watcher;
		try {
			watcher = new TextFileWatcher(file, interval, numLines);
			watcher.setFilters(filters);
		}
		catch (Exception e) {
			// Shouldn't happen!
			e.printStackTrace();
			return;
		}
		
		final WatcherEntry entry = new WatcherEntry(viewer,  watcher, newItem, filters);
		m_watchers.add(entry);
			
		// Add a Watcher listener
		final Display display = Display.getCurrent();
		watcher.addListener(new WatcherUpdateListener() {
			public void update(BoundedList list)
			{
				final BoundedList flist = list;
				
				display.asyncExec(new Runnable() {
					public void run()
					{
						entry.getViewer().getTextWidget().append(flist.getFormattedText());
		
						if (entry.isScroll()) {
							// Scroll to the bottom
							viewer.setTopIndex(newDoc.getNumberOfLines());
						}
					}
				});
			}
		});
		
		// Allow filters to set line styles.
		viewer.getTextWidget().addLineStyleListener(new LineStyleListener() {
            public void lineGetStyle(LineStyleEvent event)
            {
            	for (Iterator iter = entry.getFilters().iterator(); iter.hasNext();) {
                    Filter f = (Filter) iter.next();
                    if (f.matches(event.lineText)) {
                    	f.handleViewerMatch(event);	
                    }
                }
            }
        });
        
        // Set the font.
        Font f = LogwatcherPlugin.getDefault().getFont("logwatcherFont");
        viewer.getTextWidget().setFont(f);
        
		
		watcher.start();
	
		m_closeAction.setEnabled(true);
		m_clearAction.setEnabled(true);
		m_scrollAction.setEnabled(true);
		m_editAction.setEnabled(true);
		
		if (saveState) {
			saveWatcherState();
		}
	}

	/**
	 * Change the properties of a currently active watcher.
	 * 
	 * @param entry
	 * @param interval
	 * @param numLines
	 * @param filters
	 */
	public void editWatcher(WatcherEntry entry, int interval, int numLines, Vector filters)
	{
	    entry.getWatcher().setInterval(interval);
	    entry.getWatcher().setNumLines(numLines);
	    entry.getWatcher().setFilters(filters);
	    entry.setFilters(filters);
	    
	    saveWatcherState();
	}

	/**
	 * Write the current set of watchers to a config file.
	 */
	private void saveWatcherState()
	{
		IPath path = LogwatcherPlugin.getDefault().getStateLocation();
		path = path.addTrailingSeparator();
		path = path.append(WATCHER_STATE_FILENAME);
		try {
            FileWriter writer = new FileWriter(path.toFile());
            writer.write("<watchers>\n");
            for (Iterator iter = m_watchers.iterator(); iter.hasNext();) {
                WatcherEntry element = (WatcherEntry) iter.next();
                element.toXML(writer);
            }
            writer.write("</watchers>");
            writer.flush();
        }
        catch (IOException e) {
        	LogwatcherPlugin.getDefault().logError("Error saving watcher state", e);
        }
		
	}

	public CTabFolder getFolder()
	{
		return m_folder;
	}

	public void setFolder(CTabFolder folder)
	{
		m_folder = folder;
	}

	private void showMessage(String message)
	{
		MessageDialog.openInformation(
			m_folder.getShell(), "LogWatcher", message);
	}
	
	public void setFocus()
	{
		m_folder.setFocus();
	}
		
	/**
	 * Clean up after ourselves.
	 */
	public void dispose() {
		super.dispose();
		
		for (Iterator iter = m_watchers.iterator(); iter.hasNext();) {
			WatcherEntry entry = (WatcherEntry) iter.next();
			entry.dispose();
		} 
		
		LogwatcherPlugin.getDefault().getPreferenceStore().removePropertyChangeListener(m_propListener);
	}
	
	/**
	 * Find the WatcherEntry associated with the given CTabItem.
	 */
	private WatcherEntry findEntry(CTabItem item)
	{
		for (Iterator iter = m_watchers.iterator(); iter.hasNext();) {
			WatcherEntry entry = (WatcherEntry) iter.next();
			if (entry.getTab() == item) {
				return entry;	
			}
		}	
		
		// Shouldn't happen
		return null;
	}
	
	
	/**
	 * Returns the WatcherEntry for the currently selected watcher
	 */
	public WatcherEntry getSelectedEntry()
	{
		return findEntry(m_folder.getSelection());
	}

	/**
	 * Loader for watchers stored in XML. Also starts each watcher in the view.
	 */
	class WatcherLoader
	{
		FilterLoader filterLoader = new FilterLoader();
		
		public void loadWatchers(Reader r) throws Exception
		{
			org.w3c.dom.Document doc = getDocument(r);
			loadWatchers(doc);
		}
	   
	   	protected void loadWatchers(org.w3c.dom.Document doc)
	   	{
	   		NodeList watcherNodes = doc.getElementsByTagName("watcher");
	   		for (int i = 0; i < watcherNodes.getLength(); i++) {
	   			Node node = watcherNodes.item(i);
	   			loadWatcher(node);
	   		}	
	   	}
		
		protected void loadWatcher(Node watcherNode)
		{
			File file = null;
			int interval = 0;
			int numLines = 0;;
			Vector filters = new Vector();
			
			NodeList children = watcherNode.getChildNodes();
	   		
	   		for (int i = 0; i < children.getLength(); i++) {
	   			Node node = children.item(i);
	   			String name = node.getNodeName();
	   			
	   			if (name.equals("file")) {
	   				file = new File(node.getFirstChild().getNodeValue());	
	   			}
	   			else if (name.equals("numLines")) {
	   				numLines = Integer.parseInt(node.getFirstChild().getNodeValue());
	   			}
	   			else if (name.equals("interval")) {
	   				interval = Integer.parseInt(node.getFirstChild().getNodeValue());	
	   			}
	   			else if (name.equals("filter")) {
	   				filters.add(filterLoader.loadFilter(node));	
	   			}
			}
			
			addWatcher(file, interval, numLines, filters, false);
		}
		
		protected org.w3c.dom.Document getDocument(Reader r) throws Exception
		{
			org.w3c.dom.Document document;
	        try {
	            DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
	            DocumentBuilder parser = factory.newDocumentBuilder();
	            document = parser.parse(new InputSource(r));
	            
	            return document;
	        }
	        catch (Exception e) {
	        	throw e;
	        }
		}
	}

}