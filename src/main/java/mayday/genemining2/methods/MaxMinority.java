package mayday.genemining2.methods;

import java.util.HashMap;

import mayday.core.math.scoring.ScoringPlugin;
import mayday.core.pluma.PluginInfo;
import mayday.core.pluma.PluginManagerException;

/**
 * @author G\u00FCnter J\u00E4ger
 *
 */
public class MaxMinority extends AbstractImpurityMiningMethod {

	@Override
	public PluginInfo register() throws PluginManagerException {
		PluginInfo pli = new PluginInfo(this.getClass(),
				"PAS.genemining.maxminority", new String[0], ScoringPlugin.MC,
				new HashMap<String, Object>(), "G\u00FCnter J\u00E4ger",
				"jaeger@informatik.uni-tuebingen.de", 
				"Max Minority calculates the maximum of misclassified instances \n " +
				"under the assumption of a hyperplance applied to a decision tree",
				"Max Minority");
		return pli;
	}

	protected double calculate(double[] left_count,
			double[] right_count) {
		double lminor = 0, rminor = 0;
		int i;
		i = largestElement(left_count, 2);
		if (i < 2)
			for (int j = 0; j < 2; j++)
				if (i != j)
					lminor += left_count[j];

		i = largestElement(right_count, 2);
		if (i < 2)
			for (int j = 0; j < 2; j++)
				if (i != j)
					rminor += right_count[j];
		if (lminor > rminor)
			return lminor;
		else
			return rminor;
	}

	// Returns the index of the largest element in the array.
	// count+1 if the largest element <= 0
	private int largestElement(double[] array, int count) {
		int major = 0;
		for (int i = 1; i < count; i++)
			if (array[i] > array[major])
				major = i;
		if (array[major] <= 0)
			return (count + 1);
		return major;
	}

}
