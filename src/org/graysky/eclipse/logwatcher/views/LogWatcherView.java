package org.graysky.eclipse.logwatcher.views;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.Iterator;
import java.util.Vector;

import org.eclipse.jface.action.Action;
import org.eclipse.jface.action.IMenuListener;
import org.eclipse.jface.action.IMenuManager;
import org.eclipse.jface.action.IToolBarManager;
import org.eclipse.jface.action.MenuManager;
import org.eclipse.jface.action.Separator;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.text.BadLocationException;
import org.eclipse.jface.text.Document;
import org.eclipse.jface.text.TextViewer;
import org.eclipse.jface.window.Window;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.CTabFolder;
import org.eclipse.swt.custom.CTabItem;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.graphics.Device;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.ui.IActionBars;
import org.eclipse.ui.ISharedImages;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.part.ViewPart;
import org.graysky.eclipse.logwatcher.NewWatcherDialog;
import org.graysky.eclipse.logwatcher.watchers.TextFileWatcher;
import org.graysky.eclipse.logwatcher.watchers.WatcherUpdateListener;
import org.graysky.eclipse.util.BoundedList;

/** 
 * The main view for LogWatcher. Still has lots of code generated from the PDE
 * wizard that is not used.
 */
public class LogWatcherView extends ViewPart {
	
	private Action 		m_closeAction = null;
	private Action 		m_newAction = null;
	private CTabFolder 	m_folder = null;
	private Vector 		m_watchers = new Vector();

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

		makeActions();
		contributeToActionBars();
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
		fillLocalPullDown(bars.getMenuManager());
		fillLocalToolBar(bars.getToolBarManager());
	}

	private void fillLocalPullDown(IMenuManager manager)
	{
		manager.add(m_closeAction);
		manager.add(new Separator());
		manager.add(m_newAction);
	}

	private void fillContextMenu(IMenuManager manager) 
	{
		manager.add(m_closeAction);
		manager.add(m_newAction);
		// Other plug-ins can contribute there actions here
		manager.add(new Separator("Additions"));
	}
	
	private void fillLocalToolBar(IToolBarManager manager)
	{
		manager.add(m_closeAction);
		manager.add(m_newAction);
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
					}
				}
			}
		};
		m_closeAction.setText("Close");
		m_closeAction.setToolTipText("Close this watcher");
		m_closeAction.setImageDescriptor(PlatformUI.getWorkbench().getSharedImages().
			getImageDescriptor(ISharedImages.IMG_OBJS_ERROR_TSK));
		m_closeAction.setEnabled(false);
		
		// Create a new watcher
		m_newAction = new Action() {
			public void run() {
				NewWatcherDialog d = new NewWatcherDialog(m_folder.getShell());
				if (d.open() == Window.OK) {
					addWatcher(d.getFile(),d.getInterval(), d.getNumLines());
				}
			}
		};
		m_newAction.setText("New Watcher");
		m_newAction.setToolTipText("Create a new watcher");
		m_newAction.setImageDescriptor(PlatformUI.getWorkbench().getSharedImages().
			getImageDescriptor(ISharedImages.IMG_OBJS_TASK_TSK));
	}
	
	private void addWatcher(File file, int interval, int numLines)
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
		}
		catch (FileNotFoundException e) {
			// Shouldn't happen!
			e.printStackTrace();
			return;
		}
		
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
		
		watcher.start();
		m_watchers.add(new WatcherEntry(viewer,  watcher, newItem));
		
		m_closeAction.setEnabled(true);
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
			entry.watcher.halt();	
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
	
	class WatcherEntry
	{
		TextViewer viewer = null;
		TextFileWatcher watcher = null;
		CTabItem tab = null;
		
		public WatcherEntry(TextViewer v, TextFileWatcher w, CTabItem t)
		{
			viewer = v;	
			watcher = w;
			tab = t;
		}
		
		public void dispose()
		{
			watcher.halt();
			viewer.getControl().dispose();
			tab.dispose();
		}
	}

}