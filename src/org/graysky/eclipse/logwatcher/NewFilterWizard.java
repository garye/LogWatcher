package org.graysky.eclipse.logwatcher;

import org.eclipse.jface.wizard.Wizard;

/**
 * @author gelliott
 *
 * To change this generated comment edit the template variable "typecomment":
 * Window>Preferences>Java>Templates.
 * To enable and disable the creation of type comments go to
 * Window>Preferences>Java>Code Generation.
 */
public class NewFilterWizard extends Wizard {

	/**
	 * Constructor for NewFilterWizard.
	 */
	public NewFilterWizard()
	{
		super();
		setWindowTitle("Window Title");
		addPage(new NewFilterWizardStart("start"));
		addPage(new HighlightOptionsPage("highlight_options"));
		addPage(new IgnoreOptionsPage("ignore_options"));
		System.out.println("done constructing");
	}

	/**
	 * @see org.eclipse.jface.wizard.IWizard#performFinish()
	 */
	public boolean performFinish()
	{
		return false;
	}

}
