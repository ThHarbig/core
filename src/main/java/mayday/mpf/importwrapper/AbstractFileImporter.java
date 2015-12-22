package mayday.mpf.importwrapper;

import java.util.List;

import mayday.core.io.gudi.prototypes.DatasetFileImportPlugin;
import mayday.core.pluma.AbstractPlugin;
import mayday.core.pluma.PluginInfo;

public abstract class AbstractFileImporter extends AbstractPlugin implements DatasetFileImportPlugin {

	protected static final String IMPORT_WRAPPER_KEY = "ImportWrapper-Module";
	protected static final String FILEOPTIONINDEX_KEY = "ImportWrapper-FileOptionIndex";
	
	/**
	 * @param ModuleName The precise name of the module to call for importing
	 * @param ImporterName A nice name for this importer, this will be the name of the imported dataset
	 * @param FileOptionName The precise name of the option that the file name(s) will be sent to (see importFrom())
	 */
	public void init(PluginInfo pli, String ModuleName, String ImporterName, int FileOptionIndex) {
		pli.getProperties().put(IMPORT_WRAPPER_KEY, new ImportWrapper(ModuleName, ImporterName));
		pli.getProperties().put(FILEOPTIONINDEX_KEY, FileOptionIndex);
	}
	
	public List<mayday.core.DataSet> importFrom(List<String> files) {
		ImportWrapper importWrapper = (ImportWrapper)getPluginInfo().getProperties().get(IMPORT_WRAPPER_KEY);
		Integer fileoptionindex = (Integer)getPluginInfo().getProperties().get(FILEOPTIONINDEX_KEY);
		importWrapper.prepare();
		importWrapper.setFileNameOptionIndex(fileoptionindex);
		// merge list of files into one string separated by ", "
		StringBuilder result = new StringBuilder();		
		for (String file : files) {
	        result.append(file+", ");
	    }		
		String fileString = result.substring(0, result.lastIndexOf(", "));
		importWrapper.setFileName(fileString);
		return importWrapper.run();
	}

	public void init() {}
	
}
