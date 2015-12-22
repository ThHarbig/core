package mayday.dynamicpl;

import java.util.HashMap;

import mayday.core.pluma.Constants;
import mayday.core.pluma.PluginInfo;
import mayday.core.pluma.PluginManagerException;

public class FilterProbelist extends NewDynamicProbelist {

	public void init() {
	}

	public PluginInfo register() throws PluginManagerException {
		PluginInfo pli = new PluginInfo(
				this.getClass(),
				"PAS.dynamicPL.asFilter",
				new String[0],
				Constants.MC_PROBELIST,
				new HashMap<String, Object>(),
				"Florian Battke",
				"battke@informatik.uni-tuebingen.de",
				"Create a new dynamic probelist based on input lists",
				"Create a new dynamic probelist based on input lists"
				);
		pli.setMenuName("Filter...");
		return pli;
	}



}
