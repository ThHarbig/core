package mayday.core.structures.trees.layouter;


import java.util.HashMap;

import mayday.core.pluma.PluginInfo;
import mayday.core.pluma.PluginManagerException;

/**
 * Creates a radial Layout of a given tree (root)
 * @author Andreas Friedrich
 *
 */
public class UnrootedVertical extends Unrooted {

	public UnrootedVertical() {
		super(0d);
	}

	public String toString() {
		return "Unrooted (equal-angle, vertical major axis)";
	}
	
	public PluginInfo register() throws PluginManagerException {
		PluginInfo pli = new PluginInfo(
				this.getClass(),
				"PAS.treelayout.Unrooted",
				new String[0],
				MC,
				new HashMap<String, Object>(),
				"Florian Battke",
				"battke@informatik.uni-tuebingen.de",
				"Unrooted (equal-angle, vertical major axis)",
				"Unrooted (equal-angle, vertical major axis)"
		);
		return pli;	
	}

}
