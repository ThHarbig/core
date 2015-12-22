package mayday.core.settings;

import java.awt.Window;

@SuppressWarnings("serial")
public class SettingsDialogMenuItem extends DialogMenuItem<Settings> {	
	
	public SettingsDialogMenuItem(final Settings s, final Window parent) {
		super(s,parent);
	}
	
	public String getName(Settings o) {
		return "Detach menu";
		
	}
	public Window createDialog(Settings o) {
		return new SettingsDialog(null, o.getRoot().getName(), o);
	}

	@Override
	public String getTooltip(Settings o) {		
		return null;
	}
		
}
