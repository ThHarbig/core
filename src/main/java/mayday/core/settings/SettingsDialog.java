package mayday.core.settings;

import java.awt.Window;

import javax.swing.JPanel;

@SuppressWarnings("serial")
public class SettingsDialog extends SettingDialog {
	
	protected Settings settings;
	
	public SettingsDialog(Window owner, String title, Settings settings) {
		super(owner, title, null);
		settingComponent = settings.getSettingComponent();		
		this.settings = settings;
		init();
	}
	
	public boolean applyAndSave() {
		boolean b = apply(); 
		if (b)
			settings.storeCurrentSettingAsDefault();
		return b;
	}
	
	public void additionalButtons(JPanel buttons) {
		if (settings.getPrefTreeRoot()!=null)
			buttons.add(settings.getLoadStoreGUIElement(this));		
	}

	public Settings getSettings() {
		return settings;
	}
	
}
