package org.graysky.eclipse.logwatcher.preferences;

import org.eclipse.jface.preference.*;
import org.eclipse.ui.IWorkbenchPreferencePage;
import org.eclipse.ui.IWorkbench;
import org.graysky.eclipse.logwatcher.LogwatcherPlugin;
import org.eclipse.jface.preference.*;
import org.eclipse.jface.preference.IPreferenceStore;

/**
 * This class represents a preference page that
 * is contributed to the Preferences dialog. By 
 * subclassing <samp>FieldEditorPreferencePage</samp>, we
 * can use the field support built into JFace that allows
 * us to create a page that is small and knows how to 
 * save, restore and apply itself.
 * <p>
 * This page is used to modify preferences only. They
 * are stored in the preference store that belongs to
 * the main plug-in class. That way, preferences can
 * be accessed directly via the preference store.
 */
public class PrefsPage extends FieldEditorPreferencePage implements IWorkbenchPreferencePage
{
	// TODO: Put in a global constants class.
	public static final String SAVE_WATCHERS = "saveWatchers";

	public PrefsPage()
	{
		super(GRID);
		setPreferenceStore(LogwatcherPlugin.getDefault().getPreferenceStore());
		setDescription("LogWatcher preferences:");
		initializeDefaults();
	}
	
	/**
	 * Sets the default values of the preferences.
	 */
	private void initializeDefaults()
	{
		IPreferenceStore store = getPreferenceStore();
		store.setDefault(SAVE_WATCHERS, true);
	}
	
	/**
	 * Creates the field editors. Field editors are abstractions of
	 * the common GUI blocks needed to manipulate various types
	 * of preferences. Each field editor knows how to save and
	 * restore itself.
	 */
	public void createFieldEditors()
	{
		addField(new BooleanFieldEditor(SAVE_WATCHERS, 
				 "&Restore previously open watchers on restart", getFieldEditorParent()));
	}
	
	public void init(IWorkbench workbench)
	{
	}

}