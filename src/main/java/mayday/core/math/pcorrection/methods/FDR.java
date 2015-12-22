package mayday.core.math.pcorrection.methods;

import java.util.Collection;
import java.util.HashMap;
import java.util.List;

import mayday.core.math.pcorrection.PCorrectionPlugin;
import mayday.core.pluma.PluginInfo;
import mayday.core.pluma.PluginManagerException;
import mayday.core.structures.linalg.vector.AbstractVector;
import mayday.core.structures.linalg.vector.DoubleVector;

public class FDR extends PCorrectionPlugin {

	public List<Double> correct(Collection<Double> pvalues) {

		AbstractVector input0 = new DoubleVector(pvalues);
		
		// ignore NaNs
		AbstractVector input = input0.subset(input0.isNA(), true);
		int size = input.size();
		
		// sort descending, highest value first
		input.sort();
		input.reverse();
		
		for (int i=size-1; i>=0; --i) {
			double pval = input.get(i);
			double corrected = pval*(double)size/(double)(size-i);
			if (corrected>1.0)
				corrected = 1.0;
			input.set(i, corrected); // the fdr transform			
		}
		
		// cumulative minimum
		if (input.size()>0) {
			double prev = input.get(0);

			for (int i=1; i!=size; ++i) {
				double current = input.get(i);
				if (current>prev && !Double.isNaN(prev)) {
					current = prev;
					input.set(i, current);
				}						
				prev = current;
			}
		}
		
		return input0.asList();

	}
	
	@SuppressWarnings("unchecked")
	public PluginInfo register() throws PluginManagerException {
		PluginInfo pli = new PluginInfo(
				(Class)this.getClass(),
				"PAS.pcorrection.FDR",
				new String[0],
				MC,
				new HashMap<String, Object>(),
				"Florian Battke, Roland Keller",
				"battke@informatik.uni-tuebingen.de",
				"FDR (Benjamini-Hochberg) p-value correction",
				"FDR"
				);
		
//		System.out.println("FDR");
//		System.out.println(correct(new double[]{.006529891,.144371819,.217803027,.009793726,.384139919}));
		
		return pli;
	}

}
