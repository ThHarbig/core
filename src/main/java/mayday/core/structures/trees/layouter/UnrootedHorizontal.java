package mayday.core.structures.trees.layouter;


import java.util.HashMap;

import mayday.core.pluma.PluginInfo;
import mayday.core.pluma.PluginManagerException;

/**
 * Creates a radial Layout of a given tree (root)
 * @author Andreas Friedrich
 *
 */
public class UnrootedHorizontal extends Unrooted {

	public UnrootedHorizontal() {
		super(Math.PI/2d);
	}

	public String toString() {
		return "Unrooted (equal-angle, horizontal major axis)";
	}
	
	public PluginInfo register() throws PluginManagerException {
		PluginInfo pli = new PluginInfo(
				this.getClass(),
				"PAS.treelayout.Unrooted.Horizontal",
				new String[0],
				MC,
				new HashMap<String, Object>(),
				"Florian Battke",
				"battke@informatik.uni-tuebingen.de",
				"Unrooted (equal-angle, horizontal major axis)",
				"Unrooted (equal-angle, horizontal major axis)"
		);
		return pli;	
	}

}
