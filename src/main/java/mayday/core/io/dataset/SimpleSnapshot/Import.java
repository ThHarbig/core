package mayday.core.io.dataset.SimpleSnapshot;

import java.io.File;
import java.io.FileReader;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;

import mayday.core.DataSet;
import mayday.core.io.ReadyBufferedReader;
import mayday.core.io.gudi.GUDIConstants;
import mayday.core.io.gudi.prototypes.DatasetFileImportPlugin;
import mayday.core.pluma.AbstractPlugin;
import mayday.core.pluma.Constants;
import mayday.core.pluma.PluginInfo;
import mayday.core.pluma.PluginManagerException;
import mayday.core.tasks.AbstractTask;

public class Import extends AbstractPlugin implements DatasetFileImportPlugin {
	
	@SuppressWarnings("unchecked")
	public PluginInfo register() throws PluginManagerException {
		PluginInfo pli = new PluginInfo(
				(Class)this.getClass(),
				"PAS.simplesnapshot.read",
				new String[0],
				Constants.MC_DATASET_IMPORT,
				new HashMap<String, Object>(),
				"Florian Battke",
				"battke@informatik.uni-tuebingen.de",
				"Reads a Mayday snapshot of a Dataset.",
				"Snapshot Import"
		);
		pli.getProperties().put(GUDIConstants.IMPORTER_TYPE, GUDIConstants.IMPORTERTYPE_FILESYSTEM);
		pli.getProperties().put(GUDIConstants.FILESYSTEM_IMPORTER_TYPE, GUDIConstants.MANYFILES);
		pli.getProperties().put(GUDIConstants.FILE_EXTENSIONS,"mayday");
//		pli.getProperties().put(GUDIConstants.IMPORTER_DESCRIPTION,"Mayday Snapshot Format");
		pli.getProperties().put(GUDIConstants.TYPE_DESCRIPTION,"Text-based (uncompressed) snapshot");		
		return pli;
	}

	public void init() {}


	
	public List<DataSet> importFrom(final List<String> files) {
		final LinkedList<DataSet> result = new LinkedList<DataSet>();
		
		AbstractTask at = new AbstractTask("Reading Snapshot") {

			@Override
			protected void doWork() throws Exception {
				for (String file : files) {
					ReadyBufferedReader br = new ReadyBufferedReader(new FileReader(file));		            
		            
		            Snapshot snap = Snapshot.getCorrectVersion(br);
		            
		            if (snap==null)
		            	throw new Exception("Snapshot format not supported");
		            
		            snap.setProcessingTask(this);
					snap.setStreamProvider(new FileStreamProvider(new File(file).getParent()));
					snap.read(br);
					
					result.add(snap.getDataSet());
				}
			}

			@Override
			protected void initialize() {}
			
		};
		at.start();
		at.waitFor();
		
		if (at.hasBeenCancelled())
			result.clear();
		
		return result;
	}
	

}
