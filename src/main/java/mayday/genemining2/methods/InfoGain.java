package mayday.genemining2.methods;

import java.util.HashMap;

import mayday.core.math.scoring.ScoringPlugin;
import mayday.core.pluma.PluginInfo;
import mayday.core.pluma.PluginManagerException;

/**
 * @author G\u00FCnter J\u00E4ger
 *
 */
public class InfoGain extends AbstractImpurityMiningMethod {

	@Override
	public PluginInfo register() throws PluginManagerException {
		PluginInfo pli = new PluginInfo(this.getClass(),
				"PAS.genemining.informationgain", new String[0], ScoringPlugin.MC,
				new HashMap<String, Object>(), "G\u00FCnter J\u00E4ger",
				"jaeger@informatik.uni-tuebingen.de", 
				"Calculates the difference of entropy resulting from partitioning the\n" +
				"experiments into two classes. Also known as Kullback-Leibler divergence.",
				"Information Gain");
		return pli;
	}

	protected double calculate(double[] left_count,
			double[] right_count) {
		double presplit_info = 0, postsplit_info = 0, left_info = 0;
		double right_info = 0, ratio = 0, infogain = 0, total_count;
		double total_left_count = 0, total_right_count = 0;

		total_left_count += left_count[0];
		total_left_count += left_count[1];
		total_right_count += right_count[0];
		total_right_count += right_count[1];
		total_count = total_left_count + total_right_count;

		if (total_count > 0) {
			ratio = (left_count[0] + right_count[0]) / total_count;
			if (ratio > 0)
				presplit_info += -1 * ratio * log2(ratio);
			ratio = (left_count[1] + right_count[1]) / total_count;
			if (ratio != 0)
				presplit_info += -1 * ratio * log2(ratio);
		}

		if (total_left_count > 0) {
			ratio = left_count[0] / total_left_count;
			if (ratio != 0)
				left_info += -1 * ratio * log2(ratio);
			ratio = left_count[1] / total_left_count;
			if (ratio != 0)
				left_info += -1 * ratio * log2(ratio);
			postsplit_info += total_left_count * left_info / total_count;
		}

		if (total_right_count > 0) {
			ratio = right_count[0] / total_right_count;
			if (ratio != 0)
				right_info += -1 * ratio * log2(ratio);
			ratio = right_count[1] / total_right_count;
			if (ratio != 0)
				right_info += -1 * ratio * log2(ratio);
			postsplit_info += total_right_count * right_info / total_count;
		}

		infogain = presplit_info - postsplit_info;

		if (infogain == 0)
			return infogain;
		else
			return -infogain;
	}

	/*
	 * computes the log-base-2 of a double
	 */
	private final double log2(double value) {
		return Math.log(value) / Math.log(2.0);
	}
	
}
