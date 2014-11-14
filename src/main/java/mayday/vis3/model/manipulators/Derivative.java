package mayday.vis3.model.manipulators;

import java.util.HashMap;

import mayday.core.pluma.PluginInfo;
import mayday.core.pluma.PluginManagerException;
import mayday.vis3.model.ManipulationMethod;

public class Derivative extends ManipulationMethod {
	
	public String getName() {
		return "first derivative (growth/decline)";
	}

	@SuppressWarnings("unchecked")
	public PluginInfo register() throws PluginManagerException {
		PluginInfo pli = new PluginInfo(
					(Class)this.getClass(),
					"PAS.manipulator.derivative",
					new String[0],
					MC,
					new HashMap<String, Object>(),
					"Florian Battke",
					"battke@informatik.uni-tuebingen.de",
					"Show the first derivative, e.g. value'[i] = value[i]-value[i-1]",
					getName()
					);
		return pli;
	}
	
	public double[] manipulate(double[] input) {
		double[] ret = new double[input.length];
		if (ret.length==0)
			return ret;
		double last = input[0];
		for (int i=0; i!=input.length; ++i) {
			ret[i] = input[i]-last;
			last = input[i];
		}
		return ret;
	}

	@Override
	public String getDataDescription() {
		return "first derivative";
	}	
		
}