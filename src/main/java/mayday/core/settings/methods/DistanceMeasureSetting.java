package mayday.core.settings.methods;

import mayday.core.math.distance.DistanceMeasurePlugin;
import mayday.core.settings.generic.PluginTypeSetting;


public class DistanceMeasureSetting extends PluginTypeSetting<DistanceMeasurePlugin> {

	public DistanceMeasureSetting(String Name, String Description, DistanceMeasurePlugin Default) {
		super(Name, Description, Default, DistanceMeasurePlugin.MC);	
	}
	
	public DistanceMeasureSetting clone() {
		return new DistanceMeasureSetting(getName(),getDescription(),getInstance());
	}
	
}
