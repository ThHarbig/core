package mayday.core.plugins.dataset;

import java.util.HashMap;
import java.util.List;

import mayday.core.DataSet;
import mayday.core.pluma.AbstractPlugin;
import mayday.core.pluma.Constants;
import mayday.core.pluma.PluginInfo;
import mayday.core.pluma.PluginManagerException;
import mayday.core.pluma.prototypes.DatasetPlugin;

public class UnsetProbeDisplayNames extends AbstractPlugin implements DatasetPlugin {

	public void init() {
	}

	@SuppressWarnings("unchecked")
	public PluginInfo register() throws PluginManagerException {
		PluginInfo pli = new PluginInfo(
				(Class)this.getClass(),
				"PAS.core.DatasetUnsetProbeDisplayNames",
				new String[0],
				Constants.MC_DATASET,
				new HashMap<String, Object>(),
				"Nils Gehlenborg",
				"neil@mangojelly.org",
				"Removes Probe display names.",
				"Unset Display Names"
				);
		pli.addCategory("Probe Names");
		pli.setMenuName("Unset Probe Display Names");
		return pli;
	}

	public List<DataSet> run(List<DataSet> datasets) {
		
		for (DataSet ds : datasets)
			ds.setProbeDisplayNames( null );
		
		return null;
    }


}
