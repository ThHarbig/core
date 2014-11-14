package mayday.genemining2.methods;

import java.util.HashMap;

import mayday.core.math.scoring.ScoringPlugin;
import mayday.core.pluma.PluginInfo;
import mayday.core.pluma.PluginManagerException;

/**
 * @author G\u00FCnter J\u00E4ger
 *
 */
public class GiniIndex extends AbstractImpurityMiningMethod {

	@Override
	public PluginInfo register() throws PluginManagerException {
		PluginInfo pli = new PluginInfo(this.getClass(),
				"PAS.genemining.giniindex", new String[0], ScoringPlugin.MC,
				new HashMap<String, Object>(), "G\u00FCnter J\u00E4ger",
				"jaeger@informatik.uni-tuebingen.de", 
				"The Gini Index is a variant of the gini coefficient, and is a " +
				"measure of the inequality of a distribution of a variable. ",
				"Gini Index");
		return pli;
	}

	protected double calculate(double[] left_count, double[] right_count) {
		double giniValue = 0, giniLeft = 0, giniRight = 0, tmp = 0;
		double total_left_count = 0;
		double total_right_count = 0;

		total_left_count += left_count[0];
		total_left_count += left_count[1];
		total_right_count += right_count[0];
		total_right_count += right_count[1];

		if (total_left_count != 0) {
			tmp = left_count[0] / total_left_count;
			giniLeft += tmp * tmp;
			tmp = left_count[1] / total_left_count;
			giniLeft += tmp * tmp;

			giniLeft = 1.0 - giniLeft;
		}

		if (total_right_count != 0) {
			tmp = right_count[0] / total_right_count;
			giniRight += tmp * tmp;
			tmp = right_count[1] / total_right_count;
			giniRight += tmp * tmp;

			giniRight = 1.0 - giniRight;
		}

		giniValue = (total_left_count * giniLeft + total_right_count
				* giniRight)
				/ (total_left_count + total_right_count);

		return giniValue;
	}

}
