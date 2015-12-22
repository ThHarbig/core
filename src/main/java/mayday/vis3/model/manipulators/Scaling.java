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

public class Scaling extends ManipulationMethod implements ManipulationMethodSingleValue {

	protected DoubleSetting translate1, translate2;
	protected DoubleSetting scale;
	protected HierarchicalSetting setting;
	
	protected double a=Double.NaN, b, c;
	
	protected void updateVariables() {
		getSetting();
		a = translate1.getDoubleValue();
		b = scale.getDoubleValue();
		c = translate2.getDoubleValue();
	}
	
	public double[] manipulate(double[] input) {
		if (Double.isNaN(a)) {			
			updateVariables();
		}
		
		double[] k = Arrays.copyOf(input,input.length);
		
		for(int i=0; i!=k.length; ++i) {
			k[i] = manipulate(input[i]);
		}		
		return k;
	}
	
	public double manipulate(double singlevalue) {
		if (Double.isNaN(a)) {
			updateVariables();
		}
		return (singlevalue+a)*b+c;
	}
	
	public Setting getSetting() {
		if (setting==null) {
			
			translate1 = new DoubleSetting("1. Translate by","This value is added to the data values",0.0);
			scale = new DoubleSetting("2. Scale by", "This value is multiplied to the translated data values",1.0);
			translate2 = new DoubleSetting("3. Translate by","This value is added to the scaled values",0.0);
			setting = new HierarchicalSetting("Scaling").addSetting(translate1).addSetting(scale).addSetting(translate2);
			
			setting.addChangeListener(new SettingChangeListener() {
				public void stateChanged(SettingChangeEvent e) {
					updateVariables();
				}
			});
			PluginInfo.loadDefaultSettings(setting, "PAS.manipulator.scaling");
		}
		return setting;
	}

	public String getName() {
		return "scaling";
	}
	
	public String toString() {
		return super.toString()+((!Double.isNaN(a))?(" (x+"+a+")*"+b+"+"+c):"");
	}

	@SuppressWarnings("unchecked")
	public PluginInfo register() throws PluginManagerException {
		PluginInfo pli = new PluginInfo(
					(Class)this.getClass(),
					"PAS.manipulator.scaling",
					new String[0],
					MC,
					new HashMap<String, Object>(),
					"Florian Battke",
					"battke@informatik.uni-tuebingen.de",
					"Scaling: f(x) = ( x + a ) * b + c",
					getName()
					);
		return pli;
	}

	@Override
	public String getDataDescription() {
		updateVariables();
		return "scaled"+setting!=null?("(x+"+a+")*"+b+"+"+c):"";
	}

}