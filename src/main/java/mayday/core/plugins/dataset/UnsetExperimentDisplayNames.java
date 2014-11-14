package mayday.core.plugins.dataset;

import java.util.HashMap;
import java.util.List;

import mayday.core.DataSet;
import mayday.core.pluma.AbstractPlugin;
import mayday.core.pluma.Constants;
import mayday.core.pluma.PluginInfo;
import mayday.core.pluma.PluginManagerException;
import mayday.core.pluma.prototypes.DatasetPlugin;

public class UnsetExperimentDisplayNames extends AbstractPlugin implements DatasetPlugin {

	public void init() {
	}

	@SuppressWarnings("unchecked")
	public PluginInfo register() throws PluginManagerException {
		PluginInfo pli = new PluginInfo(
				(Class)this.getClass(),
				"PAS.core.DatasetUnsetExperimentDisplayNames",
				new String[0],
				Constants.MC_DATASET,
				new HashMap<String, Object>(),
				"Florian Battke",
				"battke@informatik.uni-tuebingen.de",
				"Removes Experiment display names.",
				"Unset Display Names"
				);
		pli.addCategory("Experiment Names");
		pli.setMenuName("Unset Experiment Display Names");
		return pli;
	}

	public List<DataSet> run(List<DataSet> datasets) {
		
		for (DataSet ds : datasets)
			ds.setExperimentDisplayNames( null );
		
		return null;
    }


}
