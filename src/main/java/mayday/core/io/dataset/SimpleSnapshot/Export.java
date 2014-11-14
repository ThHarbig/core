package mayday.core.io.dataset.SimpleSnapshot;

import java.io.File;
import java.io.FileOutputStream;
import java.util.HashMap;
import java.util.List;

import mayday.core.DataSet;
import mayday.core.io.gude.GUDEConstants;
import mayday.core.io.gude.prototypes.DatasetFileExportPlugin;
import mayday.core.pluma.AbstractPlugin;
import mayday.core.pluma.Constants;
import mayday.core.pluma.PluginInfo;
import mayday.core.pluma.PluginManagerException;
import mayday.core.tasks.AbstractTask;

public class Export extends AbstractPlugin implements DatasetFileExportPlugin {
	
	@SuppressWarnings("unchecked")
	public PluginInfo register() throws PluginManagerException {
		PluginInfo pli = new PluginInfo(
				(Class)this.getClass(),
				"PAS.simplesnapshot.write",
				new String[0],
				Constants.MC_DATASET_EXPORT,
				new HashMap<String, Object>(),
				"Florian Battke",
				"battke@informatik.uni-tuebingen.de",
				"Writes a Mayday snapshot of one Dataset to a text file.",
				"Snapshot Export"
		);
		pli.getProperties().put(GUDEConstants.EXPORTER_TYPE, GUDEConstants.EXPORTERTYPE_FILESYSTEM);
		pli.getProperties().put(GUDEConstants.FILE_EXTENSION,"mayday");
//		pli.getProperties().put(GUDEConstants.EXPORTER_DESCRIPTION,"Mayday Snapshot Format");
		pli.getProperties().put(GUDEConstants.TYPE_DESCRIPTION,"Text-based (uncompressed) snapshot");		
		return pli;
	}

	public void init() {}


	public void exportTo(final List<DataSet> datasets, final String file) {
		AbstractTask at = new AbstractTask("Snapshot Export") {

			@Override
			protected void doWork() throws Exception {
				Snapshot snap = Snapshot.getNewestVersion();
				snap.setDataSet(datasets.get(0));
				snap.setProcessingTask(this);
				snap.setStreamProvider(new FileStreamProvider(new File(file).getParent()));
				snap.write(new FileOutputStream(file));
			}

			@Override
			protected void initialize() {}
			
		};
		at.start();
		
		
	}
	
}
