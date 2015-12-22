package mayday.core.math.pcorrection.methods;

import java.util.Collection;
import java.util.HashMap;
import java.util.List;

import mayday.core.math.pcorrection.PCorrectionPlugin;
import mayday.core.pluma.PluginInfo;
import mayday.core.pluma.PluginManagerException;
import mayday.core.structures.linalg.vector.AbstractVector;
import mayday.core.structures.linalg.vector.DoubleVector;

public class Bonferroni extends PCorrectionPlugin {

	public List<Double> correct(Collection<Double> pvalues) {
		
		AbstractVector input = new DoubleVector(pvalues);
		int size = input.size();
		
		input.multiply(size);
		for (int i=0; i!=size; ++i)
			if (input.get(i)>1)
				input.set(i,1);
		
		return input.asList();
	}

	@SuppressWarnings("unchecked")
	public PluginInfo register() throws PluginManagerException {
		PluginInfo pli = new PluginInfo(
				(Class)this.getClass(),
				"PAS.pcorrection.Bonferroni",
				new String[0],
				MC,
				new HashMap<String, Object>(),
				"Florian Battke",
				"battke@informatik.uni-tuebingen.de",
				"The bonferroni p-value correction: p'' = p*n",
				"Bonferroni"
				);
		
//		System.out.println("BONFERRONI");
//		System.out.println(correct(new double[]{.006529891,.144371819,.217803027,.009793726,.384139919}));
		
		return pli;
	}	

}
