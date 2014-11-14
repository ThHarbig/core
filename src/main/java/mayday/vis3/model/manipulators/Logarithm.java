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

public class Logarithm extends ManipulationMethod implements ManipulationMethodSingleValue {
	
	protected double fact, minv;
	protected DoubleSetting logbase;
	protected DoubleSetting minimum;
	protected HierarchicalSetting setting;
	
	
	public double[] manipulate(double[] input) {
		getSetting();
		fact = 1d/Math.log(logbase.getDoubleValue());
		minv = minimum.getDoubleValue();

		
		double[] k = Arrays.copyOf(input,input.length);
		
		for(int i=0; i!=k.length; ++i) {
			k[i] = manipulate(input[i]);
		}		
		return k;
	}
	
	public double manipulate(double singlevalue) {
		if (fact==0 || minv==0) {
			getSetting();
			fact = 1d/Math.log(logbase.getDoubleValue());
			minv = minimum.getDoubleValue();
		}
		return Math.max(minv, Math.log(singlevalue)*fact );
	}
	
	public Setting getSetting() {
		if (setting==null) {
			
			logbase = new DoubleSetting("Base",null,2.0);
			minimum = new DoubleSetting("Minimum value", "Specify the minimal resulting value, to prevent -Infinity.",-10);
			setting = new HierarchicalSetting("Logarithm").addSetting(logbase).addSetting(minimum);
			
			setting.addChangeListener(new SettingChangeListener() {
				public void stateChanged(SettingChangeEvent e) {
					fact = 1d/Math.log(logbase.getDoubleValue());
					minv = minimum.getDoubleValue();
				}
			});
			PluginInfo.loadDefaultSettings(setting, "PAS.manipulator.logarithm");
			fact = 1d/Math.log(logbase.getDoubleValue());
			minv = minimum.getDoubleValue();
		}
		return setting;
	}
	
	public String getName() {
		return "logarithm";
	}
	
	public String toString() {
		return super.toString()+(setting!=null?" base "+logbase.getDoubleValue():"");
	}
	

	@SuppressWarnings("unchecked")
	public PluginInfo register() throws PluginManagerException {
		PluginInfo pli = new PluginInfo(
					(Class)this.getClass(),
					"PAS.manipulator.logarithm",
					new String[0],
					MC,
					new HashMap<String, Object>(),
					"Florian Battke",
					"battke@informatik.uni-tuebingen.de",
					"Computing the logarithm of each value, to a user-defined base.",
					getName()
					);
		return pli;
	}

	@Override
	public String getDataDescription() {
		return "log"+(setting!=null?" "+logbase.getDoubleValue():"");
	}

}