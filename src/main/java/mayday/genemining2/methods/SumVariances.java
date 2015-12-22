package mayday.genemining2.methods;

import java.util.HashMap;

import mayday.core.math.scoring.ScoringPlugin;
import mayday.core.pluma.PluginInfo;
import mayday.core.pluma.PluginManagerException;

/**
 * @author G\u00FCnter J\u00E4ger
 *
 */
public class SumVariances extends AbstractImpurityMiningMethod {

	@Override
	public PluginInfo register() throws PluginManagerException {
		PluginInfo pli = new PluginInfo(this.getClass(),
				"PAS.genemining.sumvariances", new String[0], ScoringPlugin.MC,
				new HashMap<String, Object>(), "G\u00FCnter J\u00E4ger",
				"jaeger@informatik.uni-tuebingen.de", 
				"Sum Variances calculates the variance of the class labels in a partition.\n" +
				"A low variance correlates with a good partition induced by a gene.",
				"Sum Variances");
		return pli;
	}

	protected double calculate(double[] left_count,
			double[] right_count) {
		double lsum1 = 0, lsum2 = 0, rsum1 = 0, rsum2 = 0;
		double lavg = 0, ravg = 0, lerror = 0, rerror = 0;

		lsum1 += left_count[0];
		lsum1 += left_count[1];
		lsum2 += 1 * left_count[0];
		lsum2 += 2 * left_count[1];
		rsum1 += right_count[0];
		rsum1 += right_count[1];
		rsum2 += 1 * right_count[0];
		rsum2 += 2 * right_count[1];

		if (lsum1 != 0)
			lavg = lsum2 / lsum1;
		if (rsum1 != 0)
			ravg = rsum2 / rsum1;

		lerror += left_count[0] * (1 - lavg) * (1 - lavg);
		lerror += left_count[1] * (2 - lavg) * (2 - lavg);
		rerror += right_count[0] * (1 - ravg) * (1 - ravg);
		rerror += right_count[1] * (2 - ravg) * (2 - ravg);

		return (lerror + rerror);
	}
	
}
