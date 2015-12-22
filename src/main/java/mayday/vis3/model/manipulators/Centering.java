package mayday.vis3.model.manipulators;

import java.util.Arrays;
import java.util.HashMap;

import mayday.core.math.Statistics;
import mayday.core.pluma.PluginInfo;
import mayday.core.pluma.PluginManagerException;
import mayday.vis3.model.ManipulationMethod;

public class Centering extends ManipulationMethod {

	public double[] manipulate(double[] input) {
		double[] k = Arrays.copyOf(input,input.length);
		double mean = Statistics.mean(input, true);
		for(int i=0; i!=k.length; ++i)
			k[i] = (k[i]-mean);
		return k;
	}

	public String getName() {
		return "centering";
	}

	@SuppressWarnings("unchecked")
	public PluginInfo register() throws PluginManagerException {
		PluginInfo pli = new PluginInfo(
					(Class)this.getClass(),
					"PAS.manipulator.centering",
					new String[0],
					MC,
					new HashMap<String, Object>(),
					"Florian Battke",
					"battke@informatik.uni-tuebingen.de",
					"Centering: f(x) = x - mean(x)",
					getName()
					);
		return pli;
	}

	@Override
	public String getDataDescription() {
		return "centered";
	}

}