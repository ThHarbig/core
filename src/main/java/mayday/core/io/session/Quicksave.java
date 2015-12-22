package mayday.core.io.session;

import java.util.HashMap;
import java.util.List;

import mayday.core.DataSet;
import mayday.core.datasetmanager.DataSetManager;
import mayday.core.io.dataset.SimpleSnapshot.Snapshot;
import mayday.core.io.gude.prototypes.DatasetFileExportPlugin;
import mayday.core.pluma.AbstractPlugin;
import mayday.core.pluma.Constants;
import mayday.core.pluma.PluginInfo;
import mayday.core.pluma.PluginManager;
import mayday.core.pluma.PluginManagerException;
import mayday.core.pluma.prototypes.GenericPlugin;

public class Quicksave extends AbstractPlugin implements GenericPlugin {

	public void init() {}

	@SuppressWarnings("unchecked")
	public PluginInfo register() throws PluginManagerException {
		PluginInfo pli = new PluginInfo(
				(Class)this.getClass(),
				"PAS.Quicksave.Session",
				new String[0],
				Constants.MC_SUPPORT,
				new HashMap<String, Object>(),
				"Florian Battke",
				"battke@informatik.uni-tuebingen.de",
				"Saves a snapshot of a Session (i.e. all open DataSets).",
				"Session Quicksave"
		);
		pli.setIcon("mayday/images/Mayday-Session-QS.gif");
		return pli;
	}

	@SuppressWarnings("unchecked")
	public void run() {
		PluginInfo pli = PluginManager.getInstance().getPluginFromID("PAS.zippedsnapshot.write");
		DatasetFileExportPlugin dsi = (DatasetFileExportPlugin)pli.getInstance();
		List<DataSet> datasets = (List<DataSet>)DataSetManager.singleInstance.getObjects();
		dsi.exportTo(datasets, Snapshot.SESSION_QUICKSAVE_FILENAME);
	}
	
}
