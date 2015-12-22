package mayday.vis3.model.manipulators;

import java.util.Arrays;
import java.util.HashMap;

import mayday.core.pluma.PluginInfo;
import mayday.core.pluma.PluginManagerException;
import mayday.core.settings.Setting;
import mayday.core.settings.events.SettingChangeEvent;
import mayday.core.settings.events.SettingChangeListener;
import mayday.core.settings.generic.HierarchicalSetting;
import mayday.core.settings.typed.DoubleSetting;
import mayday.vis3.model.ManipulationMethod;
import mayday.vis3.model.ManipulationMethodSingleValue;

public class Exponent extends ManipulationMethod implements ManipulationMethodSingleValue {
	
	protected DoubleSetting logbase;
	protected HierarchicalSetting setting;
	protected double base = Double.NaN;
	
	
	public double[] manipulate(double[] input) {				
		getSetting();
		base = logbase.getDoubleValue();
		
		double[] k = Arrays.copyOf(input,input.length);
		
		for(int i=0; i!=k.length; ++i) {
			k[i] = manipulate(input[i]);
		}		
		return k;
	}
	
	public double manipulate(double singlevalue) {
		if (Double.isNaN(base)) {
			getSetting();
			base = logbase.getDoubleValue();
		}
		return Math.pow(base, singlevalue);
	}
	
	public Setting getSetting() {
		if (setting==null) {
			
			logbase = new DoubleSetting("Base","Transformed values will be base^original_value",2.0);
			setting = new HierarchicalSetting("Exponent").addSetting(logbase);
			
			setting.addChangeListener(new SettingChangeListener() {
				public void stateChanged(SettingChangeEvent e) {
					base = logbase.getDoubleValue();
				}
			});
			PluginInfo.loadDefaultSettings(setting, "PAS.manipulator.exponent");
			base = logbase.getDoubleValue();
		}
		return setting;
	}
	
	public String getName() {
		return "exponential";
	}
	
	public String toString() {
		return super.toString()+(setting!=null?" base "+logbase.getDoubleValue():"");
	}
	

	@SuppressWarnings("unchecked")
	public PluginInfo register() throws PluginManagerException {
		PluginInfo pli = new PluginInfo(
					(Class)this.getClass(),
					"PAS.manipulator.exponent",
					new String[0],
					MC,
					new HashMap<String, Object>(),
					"Florian Battke",
					"battke@informatik.uni-tuebingen.de",
					"Computing the exponential of each value, to a user-defined base.",
					getName()
					);
		return pli;
	}

	@Override
	public String getDataDescription() {
		return "exponential"+(setting!=null?" to base "+logbase.getDoubleValue():"");
	}

}