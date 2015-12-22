package mayday.core.plugins.dataset;

import java.util.HashMap;
import java.util.List;

import mayday.core.DataSet;
import mayday.core.meta.gui.MIManagerDialog;
import mayday.core.pluma.AbstractPlugin;
import mayday.core.pluma.Constants;
import mayday.core.pluma.PluginInfo;
import mayday.core.pluma.PluginManagerException;
import mayday.core.pluma.prototypes.DatasetPlugin;

public class ShowMIManager extends AbstractPlugin implements DatasetPlugin {

	public void init() {
	}

	@SuppressWarnings("unchecked")
	public PluginInfo register() throws PluginManagerException {
		PluginInfo pli = new PluginInfo(
				(Class)this.getClass(),
				"PAS.core.DatasetShowMIManager",
				new String[0],
				Constants.MC_DATASET,
				new HashMap<String, Object>(),
				"Nils Gehlenborg",
				"neil@mangojelly.org",
				"Show the meta information manager for a Dataset.",
				"Show MI Manager"
				);
		pli.setMenuName("\0Meta Information Manager...");
		pli.setIcon("mayday/images/metainfo.png");
		return pli;
	}

	public List<DataSet> run(List<DataSet> datasets) {
	
		for (DataSet ds : datasets)
			new MIManagerDialog(ds.getMIManager()).setVisible(true);
		
		return null;
    }


}
