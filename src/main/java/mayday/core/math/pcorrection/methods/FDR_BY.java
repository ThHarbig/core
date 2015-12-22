package mayday.core.math.pcorrection.methods;

import java.util.Collection;
import java.util.HashMap;
import java.util.List;

import mayday.core.math.pcorrection.PCorrectionPlugin;
import mayday.core.pluma.PluginInfo;
import mayday.core.pluma.PluginManagerException;
import mayday.core.structures.linalg.vector.AbstractVector;
import mayday.core.structures.linalg.vector.DoubleVector;

public class FDR_BY extends PCorrectionPlugin {

	public List<Double> correct(Collection<Double> pvalues) {
		
		AbstractVector input0 = new DoubleVector(pvalues);

		// sort descending, highest value first
		input0.sort();
		input0.reverse();
		
		// ignore NaNs
		AbstractVector input = input0.subset(input0.isNA(), true);
		int size = input.size();
		
		double sum=0;
		for (int i=1; i<=size; ++i) 
			sum += 1.0/(double)i;
		
		for (int i=size-1; i>=0; --i) {
			double pval = input.get(i);
			double corrected = sum*pval*(double)size/(double)(size-i);
			if (corrected>1.0)
				corrected = 1.0;
			input.set(i, corrected); // the fdr transform			
		}

		// cumulative minimum
		double prev = input.get(0);
		
		for (int i=1; i!=size; ++i) {
			double current = input.get(i);
			if (current>prev && !Double.isNaN(prev)) {
				current = prev;
				input.set(i, current);
			}						
			prev = current;
		}
		
		input0.unpermute();		
		return input0.asList();
		
	}
	
	@SuppressWarnings("unchecked")
	public PluginInfo register() throws PluginManagerException {
		PluginInfo pli = new PluginInfo(
				(Class)this.getClass(),
				"PAS.pcorrection.FDRdep",
				new String[0],
				MC,
				new HashMap<String, Object>(),
				"Roland Keller",
				"kellerr@informatik.uni-tuebingen.de",
				"FDR (Benjamini-Yekutieli) under dependency p-value correction",
				"FDR (Benjamini-Yekutieli)"
				);
		
//		System.out.println("FDR BY");
//		System.out.println(correct(new double[]{.006529891,.144371819,.217803027,.009793726,.384139919}));
		
		return pli;
	}

}
