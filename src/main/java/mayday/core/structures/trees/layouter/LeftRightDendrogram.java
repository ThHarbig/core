package mayday.core.structures.trees.layouter;

import java.util.HashMap;

import mayday.core.pluma.PluginInfo;
import mayday.core.pluma.PluginManagerException;

public class LeftRightDendrogram extends TopDownDendrogram {
	
	public LeftRightDendrogram() {
		this.topDown = false;
	}
	
	public String toString() {
		return "Dendrogram (left-right)";
	}

	public PluginInfo register() throws PluginManagerException {
		PluginInfo pli = new PluginInfo(
				this.getClass(),
				"PAS.treelayout.LeftRightDendrogram",
				new String[0],
				MC,
				new HashMap<String, Object>(),
				"Florian Battke",
				"battke@informatik.uni-tuebingen.de",
				"Dendrogram (left-right)",
				"Dendrogram (left-right)"
		);
		return pli;	
	}

	public void init() {
	}

}
