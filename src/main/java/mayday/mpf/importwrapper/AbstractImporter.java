package mayday.mpf.importwrapper;

import java.util.List;

import mayday.core.io.gudi.prototypes.DatasetImportPlugin;
import mayday.core.pluma.AbstractPlugin;
import mayday.core.pluma.PluginInfo;

/** 
 * @author Florian Battke
 */
public abstract class AbstractImporter extends AbstractPlugin implements DatasetImportPlugin {

	protected static final String IMPORT_WRAPPER_KEY = "ImportWrapper-Module";
	
	/**
	 * @param ModuleName The precise name of the MPF module to call
	 * @param ImporterName A nice name for this importer, this will be the name of the new dataset
	 */
	public void init(PluginInfo pli, String ModuleName, String ImporterName) {
		pli.getProperties().put(IMPORT_WRAPPER_KEY, new ImportWrapper(ModuleName, ImporterName));
	}
	
	public List<mayday.core.DataSet> run() {
		ImportWrapper iw = (ImportWrapper)getPluginInfo().getProperties().get(IMPORT_WRAPPER_KEY);
		iw.prepare();
		return iw.run();
	}	
	
	public void init() {}
}
