/*
 * Created on 29.11.2005
 */
package mayday.genetics.importer.embl;

import java.util.HashMap;

import mayday.core.io.gudi.GUDIConstants;
import mayday.core.pluma.PluginInfo;
import mayday.core.pluma.PluginManagerException;
import mayday.genetics.importer.AbstractLocusImportPlugin;

public class LocusImporter extends mayday.genetics.importer.genbank.LocusImporter {

	public PluginInfo register() throws PluginManagerException {
		PluginInfo pli = new PluginInfo(
				this.getClass(),
				AbstractLocusImportPlugin.MC+".EMBL",
				new String[0],
				AbstractLocusImportPlugin.MC,
				new HashMap<String, Object>(),
				"Florian Battke",
				"battke@informatik.uni-tuebingen.de",
				"Reads locus data from EMBL files",
				"From EMBL files"
		);
		pli.getProperties().put(GUDIConstants.IMPORTER_TYPE, GUDIConstants.IMPORTERTYPE_FILESYSTEM);
		pli.getProperties().put(GUDIConstants.FILESYSTEM_IMPORTER_TYPE, GUDIConstants.MANYFILES);
		pli.getProperties().put(GUDIConstants.FILE_EXTENSIONS,"emb|embl");
		pli.getProperties().put(GUDIConstants.TYPE_DESCRIPTION,"EMBL files (embl,...)");		
		return pli;
	}

	public LocusImporter() {
		FORMAT = "EMBL";
		RECORD_START="ID";
		ACCESSION_START="AC";
		FEATURES_START="FH";
		FEATURE_LINE="FT";
	}	

}
