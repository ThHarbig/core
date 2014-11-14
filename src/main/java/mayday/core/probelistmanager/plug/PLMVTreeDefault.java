package mayday.core.probelistmanager.plug;

import java.util.HashMap;

import mayday.core.pluma.AbstractPlugin;
import mayday.core.pluma.PluginInfo;
import mayday.core.pluma.PluginManagerException;
import mayday.core.probelistmanager.ProbeListManager;
import mayday.core.probelistmanager.gui.ProbeListManagerView;
import mayday.core.probelistmanager.gui.ProbeListManagerViewTree;

public class PLMVTreeDefault extends AbstractPlugin implements PLMVPlugin {
	
	@Override
	public void init() {
	}

	@Override
	public PluginInfo register() throws PluginManagerException {
		PluginInfo pli = new PluginInfo(
				this.getClass(),
				"PAS.core.plmvt.tree",
				new String[0],
				PLMVPlugin.MC,
				new HashMap<String, Object>(),
				"Florian Battke",
				"battke@informatik.uni-tuebingen.de",
				"Shows the tree representation of the probelist manager",
				"\0Tree"
				);
		return pli;
	}

	@Override
	public ProbeListManagerView createView(ProbeListManager plm) {
		return new ProbeListManagerViewTree(plm);
	}
}
