package mayday.core.gui;

import java.util.prefs.BackingStoreException;

import javax.swing.JPanel;

@SuppressWarnings("serial")
public abstract class PreferencePane extends JPanel {

	
	/** Store changes the user made in the preference panel
	 */
	public abstract void writePreferences() throws BackingStoreException; 
	
	public String toString() {
		return getName();
	}
	
}
