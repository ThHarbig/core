package mayday.core.settings.methods;

import mayday.core.math.pcorrection.PCorrectionPlugin;
import mayday.core.settings.generic.PluginTypeSetting;


public class PCorrectionMethodSetting extends PluginTypeSetting<PCorrectionPlugin> {

	public PCorrectionMethodSetting(String Name, String Description, PCorrectionPlugin Default) {
		super(Name, Description, Default, PCorrectionPlugin.MC);	
	}
	
	public PCorrectionMethodSetting clone() {
		return new PCorrectionMethodSetting(getName(),getDescription(),getInstance());
	}
	
}
