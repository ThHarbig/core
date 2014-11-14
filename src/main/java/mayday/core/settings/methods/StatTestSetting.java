package mayday.core.settings.methods;

import mayday.core.math.stattest.StatTestPlugin;
import mayday.core.settings.generic.PluginTypeSetting;


public class StatTestSetting extends PluginTypeSetting<StatTestPlugin> {

	public StatTestSetting(String Name, String Description, StatTestPlugin Default) {
		super(Name, Description, Default, StatTestPlugin.MC);	
	}
	
	public StatTestSetting clone() {
		return new StatTestSetting(getName(),getDescription(),getInstance());
	}
	
}
