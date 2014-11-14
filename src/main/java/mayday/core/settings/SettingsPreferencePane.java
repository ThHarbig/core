package mayday.core.settings;

import java.awt.BorderLayout;
import java.util.prefs.BackingStoreException;

import mayday.core.gui.PreferencePane;

@SuppressWarnings("serial")
public class SettingsPreferencePane extends PreferencePane {

	protected Settings s;
	protected SettingComponent comp;
	
	public SettingsPreferencePane(Settings s) {
		this.s = s;
		setLayout(new BorderLayout());
		add( (comp=s.getSettingComponent()).getEditorComponent(), BorderLayout.CENTER);
		setName(s.getRoot().getName());
	}
	
	public void writePreferences() throws BackingStoreException {
		if (comp.updateSettingFromEditor(false)) 
			s.storeCurrentSettingAsDefault();
	}

	
}
