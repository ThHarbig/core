package mayday.core.io.dataset;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;

import mayday.core.DataSet;
import mayday.core.io.dataset.SimpleSnapshot.Snapshot;
import mayday.core.io.gudi.GUDIConstants;
import mayday.core.io.gudi.prototypes.DatasetFileImportPlugin;
import mayday.core.io.gudi.prototypes.DatasetImportPlugin;
import mayday.core.pluma.AbstractPlugin;
import mayday.core.pluma.Constants;
import mayday.core.pluma.PluginInfo;
import mayday.core.pluma.PluginManager;
import mayday.core.pluma.PluginManagerException;

public class DatasetQuickload extends AbstractPlugin implements DatasetImportPlugin {

	public void init() {}

	@SuppressWarnings("unchecked")
	public PluginInfo register() throws PluginManagerException {
		PluginInfo pli = new PluginInfo(
				(Class)this.getClass(),
				"PAS.Quickload",
				new String[0],
				Constants.MC_DATASET_IMPORT,
				new HashMap<String, Object>(),
				"Florian Battke",
				"battke@informatik.uni-tuebingen.de",
				"Restores a Dataset saved with the Quicksave plugin.",
				"Dataset Quickload"
		);
		pli.setIcon(Snapshot.QUICKLOAD_ICON);
		pli.getProperties().put(GUDIConstants.IMPORTER_TYPE, GUDIConstants.IMPORTERTYPE_QUICKLOAD);
		return pli;
	}

	public List<DataSet> run() {
		PluginInfo pli = PluginManager.getInstance().getPluginFromID("PAS.zippedsnapshot.read");
		DatasetFileImportPlugin dsi = (DatasetFileImportPlugin)pli.getInstance();
		LinkedList<String> filenames = new LinkedList<String>();
		filenames.add(Snapshot.QUICKSAVE_FILENAME);
		return dsi.importFrom(filenames);
	}

}
