package mayday.vis3.model.manipulators;

import java.util.HashMap;

import mayday.core.pluma.PluginInfo;
import mayday.core.pluma.PluginManagerException;
import mayday.core.settings.Setting;
import mayday.core.settings.SettingDialog;
import mayday.core.settings.typed.IntSetting;
import mayday.vis3.model.ManipulationMethod;

public class Smoothing extends ManipulationMethod {
	
	protected IntSetting winsize;

	public String getName() {
		return "smoothing (windowed mean)";
	}

	@SuppressWarnings("unchecked")
	public PluginInfo register() throws PluginManagerException {
		PluginInfo pli = new PluginInfo(
					(Class)this.getClass(),
					"PAS.manipulator.smoothing",
					new String[0],
					MC,
					new HashMap<String, Object>(),
					"Florian Battke",
					"battke@informatik.uni-tuebingen.de",
					"Smoothing over a window: f(x) = mean( neighborhood(x) )",
					getName()
					);
		return pli;
	}
	
	public double[] manipulate(double[] input) {
    	if (winsize==null) {
    		SettingDialog md = new SettingDialog(null, "Windowed smoothing", getSetting());
    		md.setModal(true);
    		md.setVisible(true);
    	}
		double[] ret = new double[input.length];
		for (int i=0; i!=input.length; ++i) {
			int effectiveWS = (winsize.getIntValue()-1)/2;
			int start = i-effectiveWS;
			start = Math.max(0, Math.min(start, input.length-winsize.getIntValue()));
			int end = start+winsize.getIntValue();
			end = Math.max(0, Math.min(end, input.length));			
			double smoothed = 0;
			for (int j=start; j!=end; ++j) 
				smoothed+=input[j];
			ret[i] = smoothed/(double)winsize.getIntValue();
		}
		return ret;
	}	
	
	public String toString() {
		return super.toString()+(winsize!=null?", window size "+winsize.getIntValue():"");
	}
	
	public Setting getSetting() {
		if (winsize==null) {
			winsize = new IntSetting("Window Size",null,3,2,null,true,true);
			PluginInfo.loadDefaultSettings(winsize, "PAS.manipulator.smoothing");
		}
		return winsize;
	}

	@Override
	public String getDataDescription() {
		return "smoothed"+(winsize!=null?" (window of "+winsize.getIntValue()+")":"");
	}
	
}