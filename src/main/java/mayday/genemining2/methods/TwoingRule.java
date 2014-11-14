package mayday.genemining2.methods;

import java.util.HashMap;

import mayday.core.math.scoring.ScoringPlugin;
import mayday.core.pluma.PluginInfo;
import mayday.core.pluma.PluginManagerException;

/**
 * @author G\u00FCnter J\u00E4ger
 *
 */
public class TwoingRule extends AbstractImpurityMiningMethod {

	

	@Override
	public PluginInfo register() throws PluginManagerException {
		PluginInfo pli = new PluginInfo(this.getClass(),
				"PAS.genemining.twoingrule", new String[0], ScoringPlugin.MC,
				new HashMap<String, Object>(), "G\u00FCnter J\u00E4ger",
				"jaeger@informatik.uni-tuebingen.de", 
				"Twoing Rule calculates the quality of a hyperplane that separates two partitions.",
				"Twoing Rule");
		return pli;
	}

	protected double calculate(double[] left_count, double[] right_count) {
		double twoingValue = 0;
		double goodness = 0;
		double total_left_count = 0;
		double total_right_count = 0;

		total_left_count += left_count[0];
		total_left_count += left_count[1];
		total_right_count += right_count[0];
		total_right_count += right_count[1];

		Double total_count = total_left_count + total_right_count;
		if (total_count == null)
			return 0;

		double tmp = 0;
		if (total_left_count != 0)
			tmp = left_count[0] / total_left_count;
		if (total_right_count != 0)
			tmp -= right_count[0] / total_right_count;
		if (tmp < 0)
			goodness += tmp * -1.0;
		else
			goodness += tmp;
		
		tmp = 0;
		if (total_left_count != 0)
			tmp = left_count[1] / total_left_count;
		
		if (total_right_count != 0)
			tmp -= right_count[1] / total_right_count;
		
		if (tmp < 0)
			goodness += tmp * -1.0;
		else
			goodness += tmp;

		total_left_count /= total_count;
		total_right_count /= total_count;
		twoingValue = total_left_count * total_right_count * goodness
				* goodness / 4;

		if (twoingValue == 0)
			return 0;
		else
			return (twoingValue * -1.0);
	}
	

}
