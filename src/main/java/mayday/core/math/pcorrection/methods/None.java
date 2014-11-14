package mayday.core.math.pcorrection.methods;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;

import mayday.core.math.pcorrection.PCorrectionPlugin;
import mayday.core.pluma.PluginInfo;
import mayday.core.pluma.PluginManagerException;

public class None extends PCorrectionPlugin {

	public List<Double> correct(Collection<Double> pvalues) {
		ArrayList<Double> ret = new ArrayList<Double>(pvalues);
		return ret;
	}

	@SuppressWarnings("unchecked")
	public PluginInfo register() throws PluginManagerException {
		PluginInfo pli = new PluginInfo(
				(Class)this.getClass(),
				"PAS.pcorrection.none",
				new String[0],
				MC,
				new HashMap<String, Object>(),
				"Florian Battke",
				"battke@informatik.uni-tuebingen.de",
				"(Placeholder method) No p-value correction is performed",
				"No correction"
				);		
		
		return pli;
	}

}
