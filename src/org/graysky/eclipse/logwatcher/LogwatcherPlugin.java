package org.graysky.eclipse.logwatcher;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.MissingResourceException;
import java.util.ResourceBundle;
import java.util.Vector;

import javax.xml.transform.Result;
import javax.xml.transform.Source;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import org.eclipse.core.resources.IWorkspace;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.IPluginDescriptor;
import org.eclipse.core.runtime.Status;
import org.eclipse.jface.preference.IPreferenceStore;
import org.eclipse.jface.preference.PreferenceConverter;
import org.eclipse.jface.resource.FontRegistry;
import org.eclipse.swt.graphics.Font;
import org.eclipse.swt.graphics.FontData;
import org.eclipse.ui.plugin.AbstractUIPlugin;
import org.graysky.eclipse.logwatcher.filters.Filter;
import org.graysky.eclipse.logwatcher.filters.FilterLoader;
import org.graysky.eclipse.util.BoundedList;
import org.graysky.eclipse.util.XmlUtils;
import org.w3c.dom.Element;

/**
 * The main plugin class for the LogWatcher plugin. Contains PDE-generated code
 * as well font management.
 */
public class LogwatcherPlugin extends AbstractUIPlugin
{
	/** The shared instance of the plugin */
	private static LogwatcherPlugin plugin;
	private ResourceBundle resourceBundle;
	private FontRegistry m_fontRegistry = new FontRegistry();
	private File m_recentWatchesFile;
	private List m_recentWatches = new BoundedList(20);
	private List m_savedFilters = new ArrayList();

	// Preferences constants
	private static final String LOG_FONT = "logwatcherFont";
	private static final String SAVE_WATCHERS = "saveWatchers";
	private static final String RECENT_WATCHERS_FILE = "recentWatchers";
	private static final String SAVED_FILTERS_FILE = "savedFilters.xml";

	/**
	 * Construct a new LogWatcher plugin.
	 */
	public LogwatcherPlugin(IPluginDescriptor descriptor) {
		super(descriptor);
		plugin = this;
		try {
			resourceBundle = ResourceBundle.getBundle("org.graysky.eclipse.logwatcher.LogwatcherPluginResources");
		}
		catch (MissingResourceException x) {
			resourceBundle = null;
		}

		m_recentWatchesFile = getRecentWatchesFile();
		try {
			if (m_recentWatchesFile.exists()) {
				initRecentWatchers();
			}
		}
		catch (Exception e) {
			logError("Error loading recent watchers", e);
		}

		try {
			File savedFilters = getSavedFiltersFile();
			if (savedFilters.exists()) {
				initSavedFilters(savedFilters);
			}
		}
		catch (Exception e) {
			logError("Error loading recent watchers", e);
		}
	}

	private File getRecentWatchesFile() {
		IPath path = getStateLocation();
		path = path.addTrailingSeparator();
		path = path.append(RECENT_WATCHERS_FILE);
		return path.toFile();
	}

	private void initRecentWatchers() throws FileNotFoundException, IOException {
		BufferedReader reader = new BufferedReader(new FileReader(m_recentWatchesFile));
		String line;
		while ((line = reader.readLine()) != null) {
			m_recentWatches.add(line);
		}
	}

	/**
	 * Returns the shared instance.
	 */
	public static LogwatcherPlugin getDefault() {
		return plugin;
	}

	/**
	 * Returns the workspace instance.
	 */
	public static IWorkspace getWorkspace() {
		return ResourcesPlugin.getWorkspace();
	}

	/**
	 * Returns the string from the plugin's resource bundle, or 'key' if not
	 * found.
	 */
	public static String getResourceString(String key) {
		ResourceBundle bundle = LogwatcherPlugin.getDefault().getResourceBundle();
		try {
			return bundle.getString(key);
		}
		catch (MissingResourceException e) {
			return key;
		}
	}

	/**
	 * Returns the plugin's resource bundle,
	 */
	public ResourceBundle getResourceBundle() {
		return resourceBundle;
	}

	protected void initializeDefaultPreferences(IPreferenceStore store) {
		super.initializeDefaultPreferences(store);
		store.setDefault("saveWatchers", false);
	}

	/**
	 * Log an error to this plugin's log
	 */
	public void logError(String msg, Exception e) {
		Status s = new Status(Status.ERROR, "logwatcher", 1, msg, e);
		getLog().log(s);
	}

	/**
	 * Get a font from the plugin's font registry.
	 * 
	 * @param name
	 *            Symbolic name of the font
	 * @return Font The requested font, or the default font if not found.
	 */
	public Font getFont(String name) {
		return m_fontRegistry.get(name);
	}

	public void putFont(String name, FontData[] data) {
		m_fontRegistry.put(name, data);
	}

	public void startup() throws CoreException {
		super.startup();

		// Store the preferred font in the registry
		if (getPreferenceStore().contains("logwatcherFont")) {
			m_fontRegistry.put("logwatcherFont", PreferenceConverter.getFontDataArray(getPreferenceStore(),
					"logwatcherFont"));
		}
	}

	public void addSavedFilter(Filter filter) {
		m_savedFilters.add(filter);
		persistSavedFilters();
	}

	public List getSavedFilters() {
		return Collections.unmodifiableList(m_savedFilters);
	}

	public List getRecentWatches() {
		return Collections.unmodifiableList(m_recentWatches);
	}

	public void addRecentWatch(String filename) {
		if (m_recentWatches.contains(filename)) {
			m_recentWatches.remove(filename);
		}

		m_recentWatches.add(filename);
		persistRecentWatches();
	}

	private synchronized void persistRecentWatches() {
		BufferedWriter out = null;
		try {
			out = new BufferedWriter(new FileWriter(m_recentWatchesFile));
			for (Iterator iter = m_recentWatches.iterator(); iter.hasNext();) {
				String element = (String) iter.next();
				out.write(element + "\n");
			}
		}
		catch (IOException e) {
			logError("Error persisting watches", e);
		}
		finally {
			if (out != null) {
				try {
					out.close();
				}
				catch (IOException e1) {
					// ignore
				}
			}
		}
	}

	private void initSavedFilters(File file) throws Exception {
		FilterLoader loader = new FilterLoader();
		Vector filters = loader.loadFilters(new FileReader(file));
		m_savedFilters.addAll(filters);
	}

	private synchronized void persistSavedFilters() {
		File path = getSavedFiltersFile();
		try {
			org.w3c.dom.Document doc = XmlUtils.createDocument();
			Element watcher = doc.createElement("filters");
			doc.appendChild(watcher);
			for (Iterator iter = m_savedFilters.iterator(); iter.hasNext();) {
				Filter element = (Filter) iter.next();
				element.toXML(doc, watcher);
			}

			// Write to a file
			Source source = new DOMSource(doc);
			Result result = new StreamResult(path);
			Transformer xformer = TransformerFactory.newInstance().newTransformer();
			xformer.transform(source, result);
		}
		catch (Exception e) {
			LogwatcherPlugin.getDefault().logError("Error saving filters", e);
		}
	}

	private File getSavedFiltersFile() {
		IPath path = LogwatcherPlugin.getDefault().getStateLocation();
		path = path.addTrailingSeparator();
		path = path.append(SAVED_FILTERS_FILE);
		return path.toFile();
	}
}
