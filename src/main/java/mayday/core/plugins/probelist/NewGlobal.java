package mayday.core.plugins.probelist;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;


import mayday.core.MasterTable;
import mayday.core.MaydayDefaults;
import mayday.core.ProbeList;
import mayday.core.pluma.AbstractPlugin;
import mayday.core.pluma.Constants;
import mayday.core.pluma.PluginInfo;
import mayday.core.pluma.PluginManagerException;
import mayday.core.pluma.prototypes.ProbelistPlugin;

public class NewGlobal extends AbstractPlugin implements ProbelistPlugin {

	public void init() {
	}

	@SuppressWarnings("unchecked")
	public PluginInfo register() throws PluginManagerException {
		PluginInfo pli = new PluginInfo(
				(Class)this.getClass(),
				"PAS.core.NewGlobal",
				new String[0],
				Constants.MC_PROBELIST_CREATE,
				new HashMap<String, Object>(),
				"Nils Gehlenborg",
				"neil@mangojelly.org",
				"Creates a new global probelist for a Dataset",
				"New Global Probelist"
				);
		pli.setMenuName("Global");
		return pli;
	}

	public List<ProbeList> run(List<ProbeList> probeLists, MasterTable masterTable) {
        ProbeList l_probeList = masterTable.createGlobalProbeList(true);
        String l_name = l_probeList.getName() + ++MaydayDefaults.s_globalProbeListCounter;
        l_probeList.setName( l_name );
        LinkedList<ProbeList> ret = new LinkedList<ProbeList>();
        ret.add(l_probeList);
		return ret;
	}


}
