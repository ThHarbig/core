package mayday.core.settings;

import java.awt.BorderLayout;
import java.util.prefs.BackingStoreException;

import mayday.core.gui.PreferencePane;
import mayday.core.pluma.PluginInfo;

@SuppressWarnings("serial")
public class SettingPreferencePane extends PreferencePane {

	protected Setting s;
	protected SettingComponent comp;
	protected PluginInfo pli;
	
	public SettingPreferencePane(PluginInfo pli) {
		this.pli=pli;
		s = pli.getInstance().getSetting();
		pli.loadDefaultSettings(s);
		setLayout(new BorderLayout());
		add( (comp=s.getGUIElement()).getEditorComponent(), BorderLayout.CENTER);
	}
	
	public void writePreferences() throws BackingStoreException {
		if (comp.updateSettingFromEditor(false)) 
			pli.storeDefaultSettings(s);
	}

	
}
