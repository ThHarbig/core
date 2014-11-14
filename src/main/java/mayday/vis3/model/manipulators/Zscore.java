/**
 * 
 */
package mayday.vis3.model.manipulators;

import java.util.Arrays;
import java.util.HashMap;

import mayday.core.math.Statistics;
import mayday.core.pluma.PluginInfo;
import mayday.core.pluma.PluginManagerException;
import mayday.vis3.model.ManipulationMethod;

public class Zscore extends ManipulationMethod {

	public double[] manipulate(double[] input) {
		double[] k = Arrays.copyOf(input,input.length);
		Statistics.normalize(k, true);
		return k;
	}
	
	public String getName() {
		return "zscore (centering and scaling)";
	}

	@SuppressWarnings("unchecked")
	public PluginInfo register() throws PluginManagerException {
		PluginInfo pli = new PluginInfo(
					(Class)this.getClass(),
					"PAS.manipulator.zscore",
					new String[0],
					MC,
					new HashMap<String, Object>(),
					"Florian Battke",
					"battke@informatik.uni-tuebingen.de",
					"Z-score transformation:: f(x) = (x - mean(x)) / sd(x)",
					getName()
					);
		return pli;
	}

	@Override
	public String getDataDescription() {
		return "zscore";
	}

}