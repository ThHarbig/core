package mayday.vis3.model.manipulators;

import java.util.HashMap;
import java.util.List;

import mayday.core.pluma.PluginInfo;
import mayday.core.pluma.PluginManagerException;
import mayday.core.settings.Setting;
import mayday.core.settings.generic.PluginInstanceListSetting;
import mayday.vis3.model.ManipulationMethod;
import mayday.vis3.model.ManipulationMethodSingleValue;

public class Stacking extends ManipulationMethod implements ManipulationMethodSingleValue {
	
	protected PluginInstanceListSetting<ManipulationMethod> setting;
	protected List<ManipulationMethod> mms;

	public double[] manipulate(double[] input) {
		getSetting();
		
		double[] k = input;
		for (ManipulationMethod mm : mms) {
			k = mm.manipulate(k);
		}

		return k;
	}
	
	public Setting getSetting() {
		if (setting==null) {
			setting = new PluginInstanceListSetting<ManipulationMethod>("Manipulation Stack",
					"The manipulations will be applied in the order shown here.\n" +
					"Double click an element to change settings.", ManipulationMethod.MC);
			PluginInfo.loadDefaultSettings(setting, "PAS.manipulator.stacking");
		}
		mms = setting.getPluginList();
		return setting;
	}
	
	public String getName() {
		return "Stack of several methods";
	}
	
	public String toString() {
		String result = "Stack [";
		if (setting!=null) {
			for (ManipulationMethod mm : mms) {
				result+=mm.toString()+", ";
			}
			if (mms.size()>0)
				result = result.substring(0, result.length()-2);
		}
		return result+"]";
	}
	
	@SuppressWarnings("unchecked")
	public PluginInfo register() throws PluginManagerException {
		PluginInfo pli = new PluginInfo(
					(Class)this.getClass(),
					"PAS.manipulator.stacking",
					new String[0],
					MC,
					new HashMap<String, Object>(),
					"Florian Battke",
					"battke@informatik.uni-tuebingen.de",
					"Stack several manipulation methods for complex manipulations",
					getName()
					);
		return pli;
	}

	@Override
	public double manipulate(double input) {
		return manipulate(new double[]{input})[0]; // do not check whether all stack elements are meant to work on single values. 
	}

	@Override
	public String getDataDescription() {
		String result = "[";
		if (setting!=null) {
			for (ManipulationMethod mm : mms) {
				result+=mm.getDataDescription()+", ";
			}
			if (mms.size()>0)
				result = result.substring(0, result.length()-2);
		}
		return result+"]";
	}

}