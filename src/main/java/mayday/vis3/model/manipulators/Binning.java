package mayday.vis3.model.manipulators;

import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;

import mayday.core.math.binning.BinningThresholdSetting;
import mayday.core.pluma.PluginInfo;
import mayday.core.pluma.PluginManagerException;
import mayday.core.settings.Setting;
import mayday.vis3.model.ManipulationMethod;
import mayday.vis3.model.ManipulationMethodSingleValue;

public class Binning extends ManipulationMethod implements ManipulationMethodSingleValue {
	
	protected BinningThresholdSetting setting;
	protected List<Double> thresholds = Collections.emptyList();

	public double[] manipulate(double[] input) {
		getSetting();
		thresholds = setting.getThresholds();

		double[] k = Arrays.copyOf(input,input.length);
		
		for(int i=0; i!=k.length; ++i) {
			k[i] = manipulate(k[i]);
		}		
		return k;
	}
	
	public double manipulate(double singlevalue) {
		boolean a=false;
		double k = singlevalue;
		for(int j=0; j!=thresholds.size(); ++j ) {
			if (k <= thresholds.get(j)) {
				k = j;
				a=true;
				break;
			}
		}
		if (!a) 
			k=thresholds.size();
		return k;
	}
	
	public Setting getSetting() {
		if (setting==null) {
			setting = new BinningThresholdSetting();
			PluginInfo.loadDefaultSettings(setting, "PAS.manipulator.binning");
			thresholds = setting.getThresholds();
		}
		return setting;
	}
	
	public String getName() {
		return "binning";
	}
	
	public String toString() {
		return super.toString()+(setting!=null?" "+thresholds.toString():"");
	}
	

	@SuppressWarnings("unchecked")
	public PluginInfo register() throws PluginManagerException {
		PluginInfo pli = new PluginInfo(
					(Class)this.getClass(),
					"PAS.manipulator.binning",
					new String[0],
					MC,
					new HashMap<String, Object>(),
					"Florian Battke",
					"battke@informatik.uni-tuebingen.de",
					"Binning of data using several strategies",
					getName()
					);
		return pli;
	}

	@Override
	public String getDataDescription() {
		return "binned"+(setting!=null?" "+thresholds.toString():"");
	}

}