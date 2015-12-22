package mayday.core.io.dataset.geosoft;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;

import mayday.core.DataSet;
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
				"PAS.io.geo.soft.read",
				new String[0],
				Constants.MC_DATASET_IMPORT,
				new HashMap<String, Object>(),
				"Florian Battke",
				"battke@informatik.uni-tuebingen.de",
				"Reads a DataSet in GEO SOFT format.",
				"GEO SOFT Series format"
		);
		pli.getProperties().put(GUDIConstants.IMPORTER_TYPE, GUDIConstants.IMPORTERTYPE_FILESYSTEM);
		pli.getProperties().put(GUDIConstants.FILESYSTEM_IMPORTER_TYPE, GUDIConstants.MANYFILES);
		pli.getProperties().put(GUDIConstants.FILE_EXTENSIONS,"soft");
		pli.getProperties().put(GUDIConstants.TYPE_DESCRIPTION,"GEO Series (SOFT)");		
		return pli;
	}

	public void init() {}


	
	public List<DataSet> importFrom(final List<String> files) {
		final LinkedList<DataSet> result = new LinkedList<DataSet>();		
		AbstractTask at = new AbstractTask("Reading DataSet") {
			
			public void doWork() throws Exception {
				for (String file : files)
					result.add(new GeoSoft().parseFile(file, this));
			}
			public void initialize() {};
			
		};
		at.start();
		at.waitFor();
		
		if (at.hasBeenCancelled())
			result.clear();
		
		return result;
	}

}
