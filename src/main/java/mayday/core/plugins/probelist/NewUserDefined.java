package mayday.core.plugins.probelist;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;


import mayday.core.MasterTable;
import mayday.core.MaydayDefaults;
import mayday.core.ProbeList;
import mayday.core.gui.probelist.ProbeListContentDialog;
import mayday.core.gui.properties.PropertiesDialogFactory;
import mayday.core.pluma.AbstractPlugin;
import mayday.core.pluma.Constants;
import mayday.core.pluma.PluginInfo;
import mayday.core.pluma.PluginManagerException;
import mayday.core.pluma.prototypes.ProbelistPlugin;

public class NewUserDefined extends AbstractPlugin implements ProbelistPlugin {

	public void init() {
	}

	@SuppressWarnings("unchecked")
	public PluginInfo register() throws PluginManagerException {
		PluginInfo pli = new PluginInfo(
				(Class)this.getClass(),
				"PAS.core.NewUserDefined",
				new String[0],
				Constants.MC_PROBELIST_CREATE,
				new HashMap<String, Object>(),
				"Nils Gehlenborg",
				"neil@mangojelly.org",
				"Creates a new user defined probelist for a Dataset",
				"New user-defined Probelist"
				);
		pli.setMenuName("User-defined...");
		return pli;
	}

	public List<ProbeList> run(List<ProbeList> probeLists, MasterTable masterTable) {
		ProbeList l_probeList = null;
        String l_name = MaydayDefaults.NEW_PROBE_LIST_NAME + ++MaydayDefaults.s_newProbeListCounter;
        ProbeListContentDialog l_dialog = new ProbeListContentDialog( l_probeList,
                masterTable.getDataSet().getProbeListManager(),
                l_name );
        l_dialog.setVisible( true );
        LinkedList<ProbeList> ret = new LinkedList<ProbeList>();
        ProbeList newPl = l_dialog.getResultProbeList();
        if (newPl != null) {
        	ret.add(newPl);
            PropertiesDialogFactory.createDialog(newPl).setVisible(true);
        }
		return ret;
	}
        
}
