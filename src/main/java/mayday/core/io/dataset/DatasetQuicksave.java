package mayday.core.io.dataset;

import java.util.HashMap;
import java.util.List;

import mayday.core.DataSet;
import mayday.core.io.dataset.SimpleSnapshot.Snapshot;
import mayday.core.io.gude.GUDEConstants;
import mayday.core.io.gude.prototypes.DatasetExportPlugin;
import mayday.core.io.gude.prototypes.DatasetFileExportPlugin;
import mayday.core.pluma.AbstractPlugin;
import mayday.core.pluma.Constants;
import mayday.core.pluma.PluginInfo;
import mayday.core.pluma.PluginManager;
import mayday.core.pluma.PluginManagerException;

public class DatasetQuicksave extends AbstractPlugin implements DatasetExportPlugin {

	public void init() {}

	@SuppressWarnings("unchecked")
	public PluginInfo register() throws PluginManagerException {
		PluginInfo pli = new PluginInfo(
				(Class)this.getClass(),
				"PAS.Quicksave",
				new String[0],
				Constants.MC_DATASET_EXPORT,
				new HashMap<String, Object>(),
				"Florian Battke",
				"battke@informatik.uni-tuebingen.de",
				"Saves a snapshot of a Dataset.",
				"Dataset Quicksave"
		);
		pli.setIcon(Snapshot.QUICKSAVE_ICON);
		pli.getProperties().put(GUDEConstants.EXPORTER_TYPE, GUDEConstants.EXPORTERTYPE_QUICKSAVE);
		return pli;
	}

	public void run(List<DataSet> datasets) {
		PluginInfo pli = PluginManager.getInstance().getPluginFromID("PAS.zippedsnapshot.write");
		DatasetFileExportPlugin dsi = (DatasetFileExportPlugin)pli.getInstance();
		dsi.exportTo(datasets, Snapshot.QUICKSAVE_FILENAME);
	}
	
}
