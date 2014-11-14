package mayday.mpf.importwrapper;

import java.util.HashMap;

import mayday.core.io.gudi.GUDIConstants;
import mayday.core.pluma.PluginInfo;
import mayday.core.pluma.PluginManagerException;

public class RandomImporter extends AbstractImporter {
	
	static String ImporterName = "Random Probe Creator";
	
	@SuppressWarnings("unchecked")
	public PluginInfo register() throws PluginManagerException {
		PluginInfo pli = new PluginInfo(
				(Class)this.getClass(),
				"PAS.mpf.import.RandomProbes",
				new String[]{"PAS.mpf"},
				mayday.core.pluma.Constants.MC_DATASET_IMPORT,
				new HashMap<String, Object>(),
				"Florian Battke",
				"battke@informatik.uni-tuebingen.de",
				"Creates a new Dataset filled with random probes",
				"Random Probes"
				);
		pli.getProperties().put(GUDIConstants.IMPORTER_TYPE, GUDIConstants.IMPORTERTYPE_OTHER);
		init(pli, "Random Probes",ImporterName);
//		pli.getProperties().put(GUDIConstants.IMPORTER_DESCRIPTION,ImporterName);
		return pli;
	}
	
}
