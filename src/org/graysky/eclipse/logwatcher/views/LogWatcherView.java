package org.graysky.eclipse.logwatcher.views;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.Reader;
import java.io.Writer;
import java.net.MalformedURLException;
import java.net.URL;
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
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.jface.text.Document;
import org.eclipse.jface.text.TextViewer;
import org.eclipse.jface.window.Window;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.CTabFolder;
import org.eclipse.swt.custom.CTabItem;
import org.eclipse.swt.custom.LineStyleEvent;
import org.eclipse.swt.custom.LineStyleListener;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.ui.IActionBars;
import org.eclipse.ui.IWorkbenchActionConstants;
import org.eclipse.ui.part.ViewPart;
import org.graysky.eclipse.logwatcher.FindDialog;
import org.graysky.eclipse.logwatcher.LogwatcherPlugin;
import org.graysky.eclipse.logwatcher.NewWatcherDialog;
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
	private CTabFolder 	m_folder = null;
	private Action			m_copyAction = null;
	private Vector 		m_watchers = new Vector();

	private static ImageDescriptor eraseImage;
	private static ImageDescriptor closeImage;
	private static ImageDescriptor newImage;
	private static ImageDescriptor findImage;
	
 	static {
	    URL url = null;
	    try {
		    url = new URL(LogwatcherPlugin.getDefault().getDescriptor().getInstallURL(),
		                  "icons/clear.gif");
		    eraseImage = ImageDescriptor.createFromURL(url);
		    
		    url = new URL(LogwatcherPlugin.getDefault().getDescriptor().getInstallURL(),
		                  "icons/close.gif");
		    closeImage = ImageDescriptor.createFromURL(url);  
		    
		    url = new URL(LogwatcherPlugin.getDefault().getDescriptor().getInstallURL(),
		                  "icons/new.gif");
		    newImage = ImageDescriptor.createFromURL(url);  
		    
			url = new URL(LogwatcherPlugin.getDefault().getDescriptor().getInstallURL(),
						  "icons/search.gif");
			findImage = ImageDescriptor.createFromURL(url);  
	    } catch (MalformedURLException e) {
	    	e.printStackTrace();
	    }
	}
  			
	/**
	 * The constructor.
	 */
	public LogWatcherView() 
	{
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

		FilterLoader l = new FilterLoader();
    	try {
            l.loadFilters(new FileReader("c:\\temp\\test.xml"));
        }
        catch (FileNotFoundException e) {
        }
        catch (Exception e) {
        }

		makeActions();
		contributeToActionBars();
		
		setGlobalActionHandlers();
		
		WatcherLoader loader = new WatcherLoader();
		IPath path = LogwatcherPlugin.getDefault().getStateLocation();
		path = path.addTrailingSeparator();
		path = path.append("watcherState.xml");
		try {
            loader.loadWatchers(new FileReader(path.toFile()));
        }
        catch (Exception e) {
        	System.out.println(e.getMessage());
        	e.printStackTrace();
        }
        
	}

	private void setGlobalActionHandlers()
	{
		getViewSite().getActionBars().setGlobalActionHandler(
			IWorkbenchActionConstants.FIND, m_findAction);
			
		getViewSite().getActionBars().setGlobalActionHandler(
			IWorkbenchActionConstants.COPY, m_copyAction);

	}

	private void setViewTitle(String name)
	{
		String title = "LogWatcher";
		if (name != null) {
			title += " - " + name;
		}
		
		setTitle(title);
	}

	private void contributeToActionBars()
	{
		IActionBars bars = getViewSite().getActionBars();
		//fillLocalPullDown(bars.getMenuManager());
		fillLocalToolBar(bars.getToolBarManager());
	}

	private void fillLocalPullDown(IMenuManager manager)
	{
		//manager.add(m_closeAction);
		//manager.add(new Separator());
		//manager.add(m_newAction);
	}

	private void fillContextMenu(IMenuManager manager) 
	{
		manager.add(m_closeAction);
		manager.add(m_newAction);
		manager.add(m_clearAction);
		// Other plug-ins can contribute there actions here
		manager.add(new Separator("Additions"));
	}
	
	private void fillLocalToolBar(IToolBarManager manager)
	{
		manager.add(m_closeAction);
		manager.add(m_newAction);
		manager.add(m_clearAction);
		manager.add(m_findAction);
	}

	private void makeActions() 
	{
		// Close the currently selected watcher
		m_closeAction = new Action() {
			public void run() {
				WatcherEntry entry = findEntry(m_folder.getSelection());
				if (entry != null) {
					entry.dispose();	
					m_watchers.remove(entry);
					if (m_folder.getItemCount() == 0) {
						setViewTitle(null);
						m_closeAction.setEnabled(false);
						m_clearAction.setEnabled(false);
					}
				}
			}
		};
		m_closeAction.setText("Close");
		m_closeAction.setToolTipText("Close this watcher");
		m_closeAction.setImageDescriptor(closeImage);
		m_closeAction.setEnabled(false);
		
		// Create a new watcher
		m_newAction = new Action() {
			public void run() {
				NewWatcherDialog d = new NewWatcherDialog(m_folder.getShell());
				if (d.open() == Window.OK) {
					addWatcher(d.getFile(),d.getInterval(), d.getNumLines(), d.getFilters(), true);
				}
			}
		};
		m_newAction.setText("New Watcher");
		m_newAction.setToolTipText("Create a new watcher");
		m_newAction.setImageDescriptor(newImage);
		
		// Clear the display	
		m_clearAction = new Action() {
			
			public void run() {
				WatcherEntry entry = findEntry(m_folder.getSelection());
				if (entry != null) {
					entry.watcher.clear();
				}	
			}
		};
		m_clearAction.setText("Clear");
		m_clearAction.setToolTipText("Clear log watcher display");
		m_clearAction.setImageDescriptor(eraseImage);
		m_clearAction.setEnabled(false);	
		
		// Find in log file
		m_findAction = new Action() {
			public void run() {
				WatcherEntry entry = findEntry(m_folder.getSelection());
				if (entry != null) {

					FindDialog d = new FindDialog(m_folder.getShell(), entry.viewer.getFindReplaceTarget());
					d.open();
				}
				
			}
		};
		m_findAction.setText("Find...");
		m_findAction.setToolTipText("Find in log file");
		m_findAction.setImageDescriptor(findImage);
		
		// Copy
		m_copyAction =  new Action() {
			public void run() {
				WatcherEntry entry = findEntry(m_folder.getSelection());
				if (entry != null) {
					entry.viewer.getTextWidget().copy();
				}
			}
		};
	}
	
	private void addWatcher(File file, int interval, int numLines, Vector filters, boolean saveState)
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
		catch (FileNotFoundException e) {
			// Shouldn't happen!
			e.printStackTrace();
			return;
		}
		
		final WatcherEntry entry = new WatcherEntry(viewer,  watcher, newItem, filters);
		m_watchers.add(entry);
			
		// Add a listener
		final Display display = Display.getCurrent();
		watcher.addListener(new WatcherUpdateListener() {
			public void update(BoundedList list)
			{
				final BoundedList flist = list;
				
				display.asyncExec(new Runnable() {
					public void run()
					{
						newDoc.set(flist.getFormattedText());
		
						// Scroll to the bottom
						viewer.setTopIndex(newDoc.getNumberOfLines());
					}
				});
			}
		});
		
		viewer.getTextWidget().addLineStyleListener(new LineStyleListener() {
            public void lineGetStyle(LineStyleEvent event)
            {
            	for (Iterator iter = entry.filters.iterator(); iter.hasNext();) {
                    Filter f = (Filter) iter.next();
                    if (f.matches(event.lineText)) {
                    	f.handleViewerMatch(event);	
                    }
                }
            }
        });
		
		watcher.start();
	
		m_closeAction.setEnabled(true);
		m_clearAction.setEnabled(true);
		
		if (saveState) {
			saveWatcherState();
		}
	}

	/**
	 * Write the current set of watchers to a config file.
	 */
	private void saveWatcherState()
	{
		IPath path = LogwatcherPlugin.getDefault().getStateLocation();
		path = path.addTrailingSeparator();
		path = path.append("watcherState.xml");
		try {
            FileWriter writer = new FileWriter(path.toFile());
            writer.write("<watchers>\n");
            for (Iterator iter = m_watchers.iterator(); iter.hasNext();) {
                WatcherEntry element = (WatcherEntry) iter.next();
                element.toXML(writer);
                System.out.println("wrote watcher");
            }
            writer.write("</watchers>");
            writer.flush();
        }
        catch (IOException e) {
        	// TODO: Log an error
        	System.out.println(e.getMessage());
        }
		
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
	 * Clean up after ourselves - Stop all watchers.
	 */
	public void dispose() {
		super.dispose();
		
		for (Iterator iter = m_watchers.iterator(); iter.hasNext();) {
			WatcherEntry entry = (WatcherEntry) iter.next();
			entry.dispose();
		} 
	}
	
	public WatcherEntry findEntry(CTabItem item)
	{
		for (Iterator iter = m_watchers.iterator(); iter.hasNext();) {
			WatcherEntry entry = (WatcherEntry) iter.next();
			if (entry.tab == item) {
				return entry;	
			}
		}	
		
		// Shouldn't happen
		return null;
	}
	
	/**
	 * In-memory representation of one log file being watched.
	 */
	class WatcherEntry
	{
		TextViewer 		viewer 		= null;
		TextFileWatcher watcher 	= null;
		CTabItem 		tab 		= null;
		Vector			filters		= null;
		
		public WatcherEntry(TextViewer v, TextFileWatcher w, CTabItem t, Vector f)
		{
			viewer = v;	
			watcher = w;
			tab = t;
			filters = f;
		}
		
		public void toXML(Writer w) throws IOException
		{
			w.write("<watcher>\n");
			w.write("<file>" + watcher.getFilename() + "</file>");
			w.write("<numLines>" + watcher.getNumLines() + "</numLines>");
			w.write("<interval>" + watcher.getInterval() + "</interval>");
			for (Iterator iter = filters.iterator(); iter.hasNext();) {
                Filter filter = (Filter) iter.next();
                filter.toXML(w);
            }
			w.write("\n</watcher>");
			
		}
		
		public void dispose()
		{
			try {
				watcher.halt();
				if (viewer.getControl() != null) {
					viewer.getControl().dispose();
				}
				tab.dispose();
				for (Iterator iterator = filters.iterator(); iterator.hasNext();) {
	                Filter element = (Filter) iterator.next();
	                element.dispose();
	            }	
			}
			catch (Throwable t) {
				System.out.println("error: " + t.getMessage());
				t.printStackTrace();	
			}
		}
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
	   		System.out.println(watcherNodes.getLength());
	   		for (int i = 0; i < watcherNodes.getLength(); i++) {
	   			Node node = watcherNodes.item(i);
	   			loadWatcher(node);
	   			System.out.println("loaded watcher");
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
	        	e.printStackTrace();
	        	throw e;
	        }
		}
	}

}