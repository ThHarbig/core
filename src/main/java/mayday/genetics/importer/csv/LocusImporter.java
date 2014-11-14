/*
 * Created on 29.11.2005
 */
package mayday.genetics.importer.csv;

import java.util.HashMap;
import java.util.Map.Entry;

import javax.swing.table.TableModel;

import mayday.core.io.gudi.GUDIConstants;
import mayday.core.pluma.PluginInfo;
import mayday.core.pluma.PluginManagerException;
import mayday.genetics.advanced.VariableGeneticCoordinate;
import mayday.genetics.basic.ChromosomeSetContainer;
import mayday.genetics.basic.coordinate.GeneticCoordinate;
import mayday.genetics.importer.AbstractLocusImportPlugin;
import mayday.genetics.importer.csv.LocusColumnTypes.CTYPE;
import mayday.genetics.locusmap.LocusMap;

public class LocusImporter extends mayday.genetics.importer.GenericLocusImporter<LocusColumnDialog>  {

	public final PluginInfo register() throws PluginManagerException {

		PluginInfo pli = new PluginInfo(
				this.getClass(),
				AbstractLocusImportPlugin.MC+".CSV",
				new String[0],
				AbstractLocusImportPlugin.MC,
				new HashMap<String, Object>(),
				"Florian Battke",
				"battke@informatik.uni-tuebingen.de",
				"Reads locus data from column-separated files",
				"From tabular file"
		);
		pli.getProperties().put(GUDIConstants.IMPORTER_TYPE, GUDIConstants.IMPORTERTYPE_FILESYSTEM);
		pli.getProperties().put(GUDIConstants.FILESYSTEM_IMPORTER_TYPE, GUDIConstants.ONEFILE);
		pli.getProperties().put(GUDIConstants.FILE_EXTENSIONS,"");
//		pli.getProperties().put(GUDIConstants.IMPORTER_DESCRIPTION,"Tabular file parser");
		pli.getProperties().put(GUDIConstants.TYPE_DESCRIPTION,"Tabular text files (tsv,csv,...)");		
		return pli;
	}

	public void init() {}

	protected LocusMap makeLocusMap(String name, TableModel tm,	LocusColumnDialog ctd) {

		ChromosomeSetContainer csc=new ChromosomeSetContainer();

		HashMap<CTYPE, Object> defaults = ctd.getDefaults();
		HashMap<CTYPE, Integer> columns = ctd.getColumns();
		
		for (CTYPE present : columns.keySet())
			defaults.remove(present);

		VariableGeneticCoordinate tmp = new VariableGeneticCoordinate(csc);
		LocusMap map = new LocusMap(name);
		
		Integer idIdx = columns.get(CTYPE.Identifier);

		for (int i=0; i!= tm.getRowCount(); ++i) {
			tmp.update(CTYPE.Length.ordinal(), null);
			tmp.update(CTYPE.From.ordinal(), -1l);
			tmp.update(CTYPE.To.ordinal(), -1l);
			for (Entry<CTYPE,Object> e : defaults.entrySet()) {
				tmp.update(e.getKey().ordinal(),e.getValue());
			}
			for (Entry<CTYPE,Integer> e : columns.entrySet()) {
				if (e.getKey()!=CTYPE.Identifier && e.getKey()!=null)
					tmp.update(e.getKey().ordinal(), tm.getValueAt(i, e.getValue()));
			}
			GeneticCoordinate gc = new GeneticCoordinate(tmp);
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
