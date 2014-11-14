package mayday.core.plugins.probelist;

import java.awt.Color;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;

import mayday.core.MasterTable;
import mayday.core.ProbeList;
import mayday.core.gui.GUIUtilities;
import mayday.core.pluma.AbstractPlugin;
import mayday.core.pluma.Constants;
import mayday.core.pluma.PluginInfo;
import mayday.core.pluma.PluginManagerException;
import mayday.core.pluma.prototypes.ProbelistPlugin;

public class RecolorProbeLists extends AbstractPlugin implements ProbelistPlugin {

	public void init() {
	}

	@SuppressWarnings("unchecked")
	public PluginInfo register() throws PluginManagerException {
		PluginInfo pli = new PluginInfo(
				(Class)this.getClass(),
				"PAS.core.RecolorProbelists",
				new String[0],
				Constants.MC_PROBELIST,
				new HashMap<String, Object>(),
				"Florian Battke",
				"battke@informatik.uni.tuebingen.de",
				"Sets the color of each selected probe list to a distinct value",
				"Unique ProbeList Colors"
				);
		pli.setMenuName("\0Unique colors");
		return pli;
	}

	public List<ProbeList> run(List<ProbeList> probelists, MasterTable masterTable) {

		Color[] r = GUIUtilities.rainbow(probelists.size(), 0.75);
		
		for (int i=0; i!=probelists.size(); ++i) {
			probelists.get(i).setColor(r[i]);
		}
		
        return new LinkedList<ProbeList>();
    }



}
