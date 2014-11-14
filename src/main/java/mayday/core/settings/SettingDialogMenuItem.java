package mayday.core.settings;

import java.awt.Window;

@SuppressWarnings("serial")
public class SettingDialogMenuItem extends DialogMenuItem<Setting> {	
	
	public SettingDialogMenuItem(final Setting s, final Window parent) {
		super(s,parent);
	}
	
	public String getName(Setting o) {
		return o.getName();
		
	}
	public Window createDialog(Setting o) {
		return new SettingDialog(null, o.getName(), o);
	}

	@Override
	public String getTooltip(Setting o) {
		return o.getDescription();
	}
		
}
