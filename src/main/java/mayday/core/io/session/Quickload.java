package mayday.core.io.session;

import java.util.Collection;
import java.util.HashMap;
import java.util.LinkedList;

import mayday.core.DataSet;
import mayday.core.datasetmanager.gui.DataSetManagerView;
import mayday.core.io.dataset.SimpleSnapshot.Snapshot;
import mayday.core.io.gudi.GUDIConstants;
import mayday.core.io.gudi.prototypes.DatasetFileImportPlugin;
import mayday.core.pluma.AbstractPlugin;
import mayday.core.pluma.Constants;
import mayday.core.pluma.PluginInfo;
import mayday.core.pluma.PluginManager;
import mayday.core.pluma.PluginManagerException;
import mayday.core.pluma.prototypes.GenericPlugin;

public class Quickload extends AbstractPlugin implements GenericPlugin {

	public void init() {}

	@SuppressWarnings("unchecked")
	public PluginInfo register() throws PluginManagerException {
		PluginInfo pli = new PluginInfo(
				(Class)this.getClass(),
				"PAS.Quickload.Session",
				new String[0],
				Constants.MC_SUPPORT,
				new HashMap<String, Object>(),
				"Florian Battke",
				"battke@informatik.uni-tuebingen.de",
				"Reads a snapshot of a Session (i.e. all open DataSets).",
				"Session Quickload"
		);
		pli.setIcon("mayday/images/Mayday-Session-QL.gif");
		pli.getProperties().put(GUDIConstants.IMPORTER_TYPE, GUDIConstants.IMPORTERTYPE_QUICKLOAD);
		return pli;
	}

	public void run() {
		PluginInfo pli = PluginManager.getInstance().getPluginFromID("PAS.zippedsnapshot.read");
		DatasetFileImportPlugin dsi = (DatasetFileImportPlugin)pli.getInstance();
		LinkedList<String> filenames = new LinkedList<String>();
		filenames.add(Snapshot.SESSION_QUICKSAVE_FILENAME);
		Collection<DataSet> cds = dsi.importFrom(filenames);
		for(DataSet ds : cds) {
			DataSetManagerView.getInstance().addDataSet(ds);
		}
		DataSetManagerView.getInstance().setSelectedDataSets(cds);
		
	}

}
