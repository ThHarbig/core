/*
 * Created on 29.11.2005
 */
package mayday.genetics.importer.ptt;

import java.io.BufferedReader;

import java.io.FileReader;
import java.util.HashMap;
import java.util.List;
import java.util.Map.Entry;

import javax.swing.JLabel;
import javax.swing.table.TableModel;

import mayday.core.io.csv.ParsedLine;
import mayday.core.io.csv.ParserSettings;
import mayday.core.io.gudi.GUDIConstants;
import mayday.core.pluma.PluginInfo;
import mayday.core.pluma.PluginManagerException;
import mayday.core.settings.SettingDialog;
import mayday.core.settings.generic.ComponentPlaceHolderSetting;
import mayday.core.settings.generic.HierarchicalSetting;
import mayday.genetics.advanced.VariableGeneticCoordinate;
import mayday.genetics.basic.ChromosomeSetContainer;
import mayday.genetics.basic.coordinate.GeneticCoordinate;
import mayday.genetics.importer.AbstractLocusImportPlugin;
import mayday.genetics.importer.DefaultLocusSetting;
import mayday.genetics.importer.LocusFileImportPlugin;
import mayday.genetics.importer.SelectableDefaultLocusSetting;
import mayday.genetics.importer.csv.LocusColumnDialog;
import mayday.genetics.importer.csv.LocusColumnTypes.CTYPE;
import mayday.genetics.locusmap.LocusMap;

public class LocusImporter extends AbstractLocusImportPlugin implements LocusFileImportPlugin {

	public final PluginInfo register() throws PluginManagerException {
		PluginInfo pli = new PluginInfo(
				this.getClass(),
				AbstractLocusImportPlugin.MC+".PTT",
				new String[0],
				AbstractLocusImportPlugin.MC,
				new HashMap<String, Object>(),
				"Florian Battke",
				"battke@informatik.uni-tuebingen.de",
				"Reads locus data from protein-table files",
				"From PTT files"
		);
		pli.getProperties().put(GUDIConstants.IMPORTER_TYPE, GUDIConstants.IMPORTERTYPE_FILESYSTEM);
		pli.getProperties().put(GUDIConstants.FILESYSTEM_IMPORTER_TYPE, GUDIConstants.MANYFILES);
		pli.getProperties().put(GUDIConstants.FILE_EXTENSIONS,"ptt");
//		pli.getProperties().put(GUDIConstants.IMPORTER_DESCRIPTION,"PTT file parser");
		pli.getProperties().put(GUDIConstants.TYPE_DESCRIPTION,"Protein table files");		
		return pli;
	}

	public void init() {}

	protected LocusMap makeLocusMap(String name, TableModel tm,	LocusColumnDialog ctd) {

		ChromosomeSetContainer csc=new ChromosomeSetContainer();

		HashMap<CTYPE, Object> defaults = ctd.getDefaults();
		HashMap<CTYPE, Integer> columns = ctd.getColumns();

		VariableGeneticCoordinate tmp = new VariableGeneticCoordinate(csc);
		LocusMap map = new LocusMap(name);

		for (int i=0; i!= tm.getRowCount(); ++i) {
			for (Entry<CTYPE,Object> e : defaults.entrySet()) {
				tmp.update(e.getKey().ordinal(),e.getValue());
			}
			for (Entry<CTYPE,Integer> e : columns.entrySet()) {
				tmp.update(e.getKey().ordinal(), tm.getValueAt(i, e.getValue()));
			}
			map.put((String)tm.getValueAt(i, columns.get(CTYPE.Identifier)), new GeneticCoordinate(tmp));
		}

		return map;
	}

	public LocusMap importFrom(List<String> files) {
		
		boolean _species = false;
		boolean _chrome = false;
		boolean _strand = true;
		boolean _length = true;
		
		DefaultLocusSetting defaultLocus = null;
		SelectableDefaultLocusSetting[] fileDefaultLocus = new SelectableDefaultLocusSetting[files.size()];
		
		if (!_species || !_chrome || !_strand || !_length) {
			HierarchicalSetting topSet = new HierarchicalSetting("Fill in missing locus information");
			String istr = "<html>PTT files do not contain species and chromosome information<br>" +
					"Please supply the missing information below.<br>" +
					(files.size()>1?"You can also supply per-file information.":"");
			defaultLocus = new DefaultLocusSetting();
			defaultLocus.hideElements(_species, _chrome, _strand, _length);
			topSet.addSetting(new ComponentPlaceHolderSetting("info", new JLabel(istr)));
			topSet.addSetting(defaultLocus);
			if (files.size()>1) {
				HierarchicalSetting perFileLocus = new HierarchicalSetting("Per-file settings");
				perFileLocus.setLayoutStyle(HierarchicalSetting.LayoutStyle.TREE);			
				topSet.addSetting(perFileLocus);
				int i=0;
				for (String f : files) {
					SelectableDefaultLocusSetting ns = new SelectableDefaultLocusSetting(f);
					ns.hideElements(_species, _chrome, _strand, _length);
					ns.setOverride(false, true, false, false);
					fileDefaultLocus[i++] = ns;
					perFileLocus.addSetting(ns);  
				}
			}		
			SettingDialog sd = new SettingDialog(null, "Locus completion", topSet).showAsInputDialog();
			if (sd.canceled()) 
				return null;
		}
		
		// parse the files with the given settings, line per line, use locus completion
		ChromosomeSetContainer csc=new ChromosomeSetContainer();
		VariableGeneticCoordinate tmp = new VariableGeneticCoordinate(csc);
		
		LocusMap lm = new LocusMap(files.get(0)+ (files.size()>1?"... ("+files.size()+" files)":""));
		
		for (int i=0; i!=files.size(); ++i) {

			// build the default locus
			SelectableDefaultLocusSetting sdls = fileDefaultLocus[i];
			
			if (defaultLocus!=null) {
				String chrome = (sdls!=null && sdls.overrideChromosome())?sdls.getChromosome():defaultLocus.getChromosome();
				String specie = (sdls!=null && sdls.overrideSpecies())?sdls.getSpecies():defaultLocus.getSpecies();
				tmp.update(CTYPE.Species.ordinal(), specie);
				tmp.update(CTYPE.Chromosome.ordinal(), chrome);
			}
			
			// now read and add elements
			
			ParserSettings sett = new ParserSettings();
			sett.separator = "\t";
			ParsedLine pl = new ParsedLine("",sett);
			try {
				BufferedReader br = new BufferedReader(new FileReader(files.get(i)));
				boolean goBackOne = true;
				String line=null;
				
				// read until the content starts
				while (br.ready()) {
					line = br.readLine();
					pl.replaceLine(line);
					if (pl.size()==9) {
						String[] split = pl.get(0).split("\\.\\.");
						if (split.length==2) {
							try {
								Integer.parseInt(split[0]);
								Integer.parseInt(split[1]);
								break; // YAY!! we got the first line!! 
							} catch (NumberFormatException nfe) {
								// not yet there
							}
						}
					}
				}

				
				while (br.ready()) {
					if (!goBackOne)
						line = br.readLine();
					else
						goBackOne = false;
					pl.replaceLine(line);
					if (!pl.isCommentLine()) {
						// reset
						// now put the right things in
						String[] location = pl.get(0).split("\\.\\.");
						String strand = pl.get(1);
						String id = pl.get(5);
						
						tmp.update(CTYPE.From.ordinal(), location[0]);
						tmp.update(CTYPE.To.ordinal(), location[1]);
						tmp.update(CTYPE.Strand.ordinal(), strand);
						
						lm.put(id, new GeneticCoordinate(tmp));
					}
				}
			} catch (Exception e) {
				return null;
			}
		}
		
		return lm;

	}




}
