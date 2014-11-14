/*
 * Created on 29.11.2005
 */
package mayday.genetics.importer.gff;

import java.io.BufferedReader;
import java.io.FileReader;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import javax.swing.JLabel;

import mayday.core.io.csv.ParsedLine;
import mayday.core.io.csv.ParserSettings;
import mayday.core.io.gudi.GUDIConstants;
import mayday.core.pluma.PluginInfo;
import mayday.core.pluma.PluginManagerException;
import mayday.core.settings.Setting;
import mayday.core.settings.SettingDialog;
import mayday.core.settings.generic.ComponentPlaceHolderSetting;
import mayday.core.settings.generic.HierarchicalSetting;
import mayday.core.settings.typed.StringMapSetting;
import mayday.core.settings.typed.StringSetting;
import mayday.genetics.advanced.VariableComplexGeneticCoordinate;
import mayday.genetics.basic.ChromosomeSetContainer;
import mayday.genetics.basic.SpeciesContainer;
import mayday.genetics.basic.Strand;
import mayday.genetics.basic.chromosome.Chromosome;
import mayday.genetics.basic.coordinate.GeneticCoordinate;
import mayday.genetics.coordinatemodel.GBAtom;
import mayday.genetics.importer.AbstractLocusImportPlugin;
import mayday.genetics.importer.LocusFileImportPlugin;
import mayday.genetics.locusmap.LocusMap;

public class ModelImporter extends AbstractLocusImportPlugin implements LocusFileImportPlugin {

	public final PluginInfo register() throws PluginManagerException {
		PluginInfo pli = new PluginInfo(
				this.getClass(),
				AbstractLocusImportPlugin.MC+".GFFmodels",
				new String[0],
				AbstractLocusImportPlugin.MC,
				new HashMap<String, Object>(),
				"Florian Battke",
				"battke@informatik.uni-tuebingen.de",
				"Reads gene model data from GFF (General Feature Format) files",
				"From GFF files with gene models"
		);
		pli.getProperties().put(GUDIConstants.IMPORTER_TYPE, GUDIConstants.IMPORTERTYPE_FILESYSTEM);
		pli.getProperties().put(GUDIConstants.FILESYSTEM_IMPORTER_TYPE, GUDIConstants.MANYFILES);
		pli.getProperties().put(GUDIConstants.FILE_EXTENSIONS,"gff");
//		pli.getProperties().put(GUDIConstants.IMPORTER_DESCRIPTION,"GFF file parser");
		pli.getProperties().put(GUDIConstants.TYPE_DESCRIPTION,"Generic Feature Format (gff,...)");		
		return pli;
	}

	protected StringSetting species;
	protected HierarchicalSetting hsett;
	
	public void init() {}

	public LocusMap importFrom(List<String> files) {
		
		// for each file, find out what version it is, parse contents, find out what feature types we could extract
		// then ask for a mapping of seqname to species/chrome
		
		ParserSettings mainLineSettings = new ParserSettings();
		mainLineSettings.separator = "\t";
		mainLineSettings.hasHeader=false;		
		ParserSettings attribLineSettings = new ParserSettings();
		attribLineSettings.separator = ";";

		ParsedLine mainLine = new ParsedLine("",mainLineSettings);
		ParsedLine attribLine = new ParsedLine("",attribLineSettings);

		HashMap<String, Integer> gffVersions = new HashMap<String, Integer>();

		HashMap<String, String> seqnames = new HashMap<String, String>();
		
		String Species = species.getStringValue();
		
		for (int i=0; i!=files.size(); ++i) {

			try {
				BufferedReader br = new BufferedReader(new FileReader(files.get(i)));
				String line;
				Integer version = null;
				
				while (br.ready()) {
					line = br.readLine();
					mainLine.replaceLine(line);
					
					// Version data
					if (version==null) {
						if (!mainLine.isCommentLine()) {
							if (mainLine.size()>=9) {
								String attrib = mainLine.get(8);
								if (attrib.contains("%20") || attrib.contains("=")) {
									version = 3;
								} else {
									version = 2;
								}
							}
						} else if (line.contains("gff-version")) {
							String[] parts = line.split("[\\s]+");
							if (parts.length>1) {
								String vString = parts[1];
								if (vString.trim().equals("3")) {
									version = 3;
								} else {
									version = 2;
								}
							}
						}
					}
					
					// gather seqnames
					if (!mainLine.isCommentLine()) {
						// GFF2.seqname == GFF3.seqid
						seqnames.put(mainLine.get(0), mainLine.get(0));
					}					
					
				}
				
				gffVersions.put(files.get(i),version);
				
			} catch (Exception ex) {
				System.out.println("Problem parsing "+files.get(i)+": "+ex);
				ex.printStackTrace();
			}
			
		}
		
		StringMapSetting chromeMapping
		 	= new StringMapSetting(
		 			"Sequence id mapping",
		 			"Specify an organism and a chromosome id for each sequence ID,\nfollowing this example:\n" +
		 			"Human chromosome 1 would be \"hsa 1\".",seqnames);
		HierarchicalSetting conversionSettings = new HierarchicalSetting("GFF import settings")
			.addSetting(new ComponentPlaceHolderSetting("c",new JLabel("Sequence identifier mapping")))
			.addSetting(chromeMapping);
		
		SettingDialog sd = new SettingDialog(null, "GFF import settings", conversionSettings);
		sd.showAsInputDialog();
		if (sd.canceled())
			return null;
		
		Map<String, String> idmapping = chromeMapping.getStringMapValue();
		
		ChromosomeSetContainer csc=ChromosomeSetContainer.getDefault();

		LocusMap lm = new LocusMap(files.get(0)+ (files.size()>1?"... ("+files.size()+" files)":""));
		
		HashMap<String, VariableComplexGeneticCoordinate> models = new HashMap<String, VariableComplexGeneticCoordinate>();
		
		for (int i=0; i!=files.size(); ++i) {

			try {
				BufferedReader br = new BufferedReader(new FileReader(files.get(i)));
				String line;
				Integer version = gffVersions.get(files.get(i));
				
				while (br.ready()) {
					line = br.readLine();
					mainLine.replaceLine(line);
					
					// gather features etc
					if (!mainLine.isCommentLine() && mainLine.size()>=9) {
						String seqname = mainLine.get(0);
						String ftype = mainLine.get(2);
						
						if (ftype.equals("exon")) {
							String attributes = mainLine.get(8);
							if (version!=null) {
								attribLine.replaceLine(attributes);
								for (String attrib : attribLine) {
									String[] parts = null;
									if (version==2) 
										parts = attrib.split(" ");
									else if (version==3) {
										parts = attrib.split("=");
									}									
									if (parts!=null && parts.length>0) {
										String key = parts[0];
										if (key.equalsIgnoreCase("parent")) {
											String name = parts[1];
											// we pick all the values provided
											if (version==2) {
												// space separated --> join
												for (int j=3; j<parts.length; ++j) 
													name += ","+parts[j];
											} else if (version==3){
												// already comma separated, nothing to do
											}
											String seqname_ = idmapping.get(seqname);
											if (seqname_ != null)
												seqname = seqname_;
											parts = seqname.split("[\\s]+");
											String theSpecies;
											String theChrome; 
											if (parts.length==1) {
												theSpecies = Species;
												theChrome = seqname;
											} else {
												theSpecies = parts[0];
												theChrome = parts[1];
											}
											Chromosome c = csc.getChromosome(SpeciesContainer.getSpecies(theSpecies), theChrome);
											String Sstart = mainLine.get(3);
											String Sstop = mainLine.get(4);
											String Sstrand = mainLine.get(6);
											try {
												long start = Long.parseLong(Sstart);
												long stop = Long.parseLong(Sstop);
												char cstrand = Sstrand.trim().charAt(0);
												if (cstrand=='.')
													cstrand='#';
												Strand strand = Strand.fromChar(cstrand);
												GBAtom exon = new GBAtom(start, stop, strand);

												VariableComplexGeneticCoordinate vcgc = models.get(name);
												if (vcgc == null) {
													models.put(name, vcgc = new VariableComplexGeneticCoordinate(csc));
													vcgc.setChromosome(c);
												} else {
													if (!vcgc.getChromosome().equals(c)) {
														System.out.println("Object \'"+name+"\' illegaly spans several chromosomes - ignoring some exons.");
													}
												}

												vcgc.addAtoms(exon);													
											} catch (Exception ex) {
												System.out.println("Could not parse coordinate "+ex);
												ex.printStackTrace();
											}
										}
										
									}
								}
							}
						}
					}					
				}

				
			} catch (Exception ex) {
				System.out.println("Problem parsing "+files.get(i)+": "+ex);
				ex.printStackTrace();
			}
			
			for (Entry<String, VariableComplexGeneticCoordinate> e : models.entrySet()) {
				lm.put(e.getKey(), new GeneticCoordinate(e.getValue()));
			}
			
		}		
		
		return lm;

	}

	
	public Setting getSetting() {
		if (hsett==null) {
			Setting files = super.getSetting();
			hsett = new HierarchicalSetting("GFF Gene Model Import")
			  .addSetting(files)
			  .addSetting(species = new StringSetting("Species",
					 "GFF files do not contain species names, supply the name here.\n" +
			  		"If chromosome identifiers are of the form \"Species <SPACE> Chromosome\" in the GFF files,\n" +
			  		"or in the mapping step (next dialog), they will be used by preference. \n" +
			  		"Otherwise, the value supplied here will be used.", "", false));
		}
		return hsett; 
	}



}
