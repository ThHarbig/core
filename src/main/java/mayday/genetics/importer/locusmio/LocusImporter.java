/*
 * Created on 29.11.2005
 */
package mayday.genetics.importer.locusmio;

import java.util.HashMap;

import javax.swing.table.TableModel;

import mayday.core.io.gudi.GUDIConstants;
import mayday.core.pluma.PluginInfo;
import mayday.core.pluma.PluginManagerException;
import mayday.genetics.basic.ChromosomeSetContainer;
import mayday.genetics.basic.coordinate.GeneticCoordinate;
import mayday.genetics.importer.AbstractLocusImportPlugin;
import mayday.genetics.importer.locusmio.LocusColumnTypes.CTYPE;
import mayday.genetics.locusmap.LocusMap;

public class LocusImporter extends mayday.genetics.importer.GenericLocusImporter<LocusColumnDialog>  {

	public final PluginInfo register() throws PluginManagerException {

		PluginInfo pli = new PluginInfo(
				this.getClass(),
				AbstractLocusImportPlugin.MC+".MIO",
				new String[0],
				AbstractLocusImportPlugin.MC,
				new HashMap<String, Object>(),
				"Florian Battke",
				"battke@informatik.uni-tuebingen.de",
				"Reads locus data from LocusMIO columns",
				"From tabular LocusMIO file"
		);
		pli.getProperties().put(GUDIConstants.IMPORTER_TYPE, GUDIConstants.IMPORTERTYPE_FILESYSTEM);
		pli.getProperties().put(GUDIConstants.FILESYSTEM_IMPORTER_TYPE, GUDIConstants.ONEFILE);
		pli.getProperties().put(GUDIConstants.FILE_EXTENSIONS,"");
//		pli.getProperties().put(GUDIConstants.IMPORTER_DESCRIPTION,"LocusMIO file parser");
		pli.getProperties().put(GUDIConstants.TYPE_DESCRIPTION,"Tabular file with LocusMIOs (tsv,csv,...)");		
		return pli;
	}

	public void init() {}

	protected LocusMap makeLocusMap(String name, TableModel tm,	LocusColumnDialog ctd) {
		ChromosomeSetContainer csc=new ChromosomeSetContainer();

		int colIdx = ctd.getColumns().get(CTYPE.LocusMIO);
		Integer idIdx = ctd.getColumns().get(CTYPE.Identifier);

		LocusMap map = new LocusMap(name);		

		for (int i=0; i!= tm.getRowCount(); ++i) {
			GeneticCoordinate gc = new GeneticCoordinate((String)tm.getValueAt(i, colIdx), csc);
			if (idIdx!=null)
				map.put( (String)tm.getValueAt(i, idIdx), gc );
			else
				map.put( gc.toString(), gc);
		}

		return map;
	}

	@Override
	protected LocusColumnDialog makeDialog(TableModel tm) {
		return new LocusColumnDialog(tm);
	}



}
