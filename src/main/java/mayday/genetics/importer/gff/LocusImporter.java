/*
 * Created on 29.11.2005
 */
package mayday.genetics.importer.gff;

import java.io.BufferedReader;
import java.io.FileReader;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;

import javax.swing.JLabel;

import mayday.core.io.csv.ParsedLine;
import mayday.core.io.csv.ParserSettings;
import mayday.core.io.gudi.GUDIConstants;
import mayday.core.pluma.PluginInfo;
import mayday.core.pluma.PluginManagerException;
import mayday.core.settings.SettingDialog;
import mayday.core.settings.generic.ComponentPlaceHolderSetting;
import mayday.core.settings.generic.HierarchicalSetting;
import mayday.core.settings.generic.MultiselectObjectListSetting;
import mayday.core.settings.generic.ObjectSelectionSetting;
import mayday.core.settings.typed.RestrictedStringSetting;
import mayday.core.settings.typed.StringMapSetting;
import mayday.core.settings.typed.StringSetting;
import mayday.genetics.basic.ChromosomeSetContainer;
import mayday.genetics.basic.Species;
import mayday.genetics.basic.SpeciesContainer;
import mayday.genetics.basic.Strand;
import mayday.genetics.basic.chromosome.Chromosome;
import mayday.genetics.basic.coordinate.GeneticCoordinate;
import mayday.genetics.importer.AbstractLocusImportPlugin;
import mayday.genetics.importer.LocusFileImportPlugin;
import mayday.genetics.locusmap.LocusMap;

public class LocusImporter extends AbstractLocusImportPlugin implements LocusFileImportPlugin {

	public final PluginInfo register() throws PluginManagerException {
		PluginInfo pli = new PluginInfo(
				this.getClass(),
				AbstractLocusImportPlugin.MC+".GFF",
				new String[0],
				AbstractLocusImportPlugin.MC,
				new HashMap<String, Object>(),
				"Florian Battke",
				"battke@informatik.uni-tuebingen.de",
				"Reads locus data from GFF (General Feature Format) files",
				"From GFF files"
		);
		pli.getProperties().put(GUDIConstants.IMPORTER_TYPE, GUDIConstants.IMPORTERTYPE_FILESYSTEM);
		pli.getProperties().put(GUDIConstants.FILESYSTEM_IMPORTER_TYPE, GUDIConstants.MANYFILES);
		pli.getProperties().put(GUDIConstants.FILE_EXTENSIONS,"gff");
//		pli.getProperties().put(GUDIConstants.IMPORTER_DESCRIPTION,"GFF file parser");
		pli.getProperties().put(GUDIConstants.TYPE_DESCRIPTION,"Generic Feature Format (gff,...)");		
		return pli;
	}

	final static String CONSTRUCT_NUMERIC_IDS = "* construct numeric ids *";
	
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
		HashSet<String> featureTypes = new HashSet<String>();
		String[] attributeTypes = new String[]{
				"ID", "locus_tag", "standard_name", "gene", "gene_synonym", "old_locus_tag", "protein_id", "product", 
				CONSTRUCT_NUMERIC_IDS
		};		
		HashSet<String> missingAttributeTypes = new HashSet<String>();
		for (String s : attributeTypes) {
			if (s!=CONSTRUCT_NUMERIC_IDS)
				missingAttributeTypes.add(s);
		}
		HashMap<String, String> seqnames = new HashMap<String, String>();
		
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
					
					// gather features etc
					if (!mainLine.isCommentLine()) {
						// GFF2.seqname == GFF3.seqid
						seqnames.put(mainLine.get(0), mainLine.get(0));
						if (mainLine.size()>=3) {				
							// GFF2.Feature == GFF3.type
							featureTypes.add(mainLine.get(2)); 
						}
						if (mainLine.size()>=9) {
							// GFF2.Attributes =^= GFF3.attributes
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
										String key = parts[0].trim();
										missingAttributeTypes.remove(key);
									}
								}
							}
						}
					}					
				}
				
				gffVersions.put(files.get(i),version);
				
			} catch (Exception ex) {
				System.out.println("Problem parsing "+files.get(i)+": "+ex);
				ex.printStackTrace();
			}
			
		}
		
		// Build the questions for the user. Each feature type will be selectable, as well as each attrib type
		HashSet<String> availableAttributeTypes = new HashSet<String>();
		availableAttributeTypes.addAll(Arrays.asList(attributeTypes));
		availableAttributeTypes.removeAll(missingAttributeTypes);
		
		MultiselectObjectListSetting<String> featuresToExtract 
			= new MultiselectObjectListSetting<String>(
					"Feature types",
					"Select which feature types to extract",
					featureTypes
					);
		RestrictedStringSetting attributeToExtract 
			= new RestrictedStringSetting(
					"Attribute name",
					"Select which attribute to use for naming the feature",
					0, 
					availableAttributeTypes.toArray(new String[0])
					).setLayoutStyle(ObjectSelectionSetting.LayoutStyle.RADIOBUTTONS); 
		StringMapSetting chromeMapping
		 	= new StringMapSetting(
		 			"Sequence id mapping",
		 			"You can change chromosome names here",seqnames);
		StringSetting speciesName = new StringSetting("Species Name","GFF files do not contain the species name, please enter it here.","",false);
		HierarchicalSetting conversionSettings = new HierarchicalSetting("GFF import settings")
			.addSetting(featuresToExtract)
			.addSetting(attributeToExtract)
			.addSetting(speciesName)
			.addSetting(new ComponentPlaceHolderSetting("c",new JLabel("Sequence identifier mapping")))			
			.addSetting(chromeMapping);
//			.setLayoutStyle(HierarchicalSetting.LayoutStyle.PANEL_HORIZONTAL);
		
		SettingDialog sd = new SettingDialog(null, "GFF import settings", conversionSettings);
		sd.showAsInputDialog();
		if (sd.canceled())
			return null;
		
		List<String> desiredFeatureTypes = featuresToExtract.getSelection();
		String desiredAttribute = attributeToExtract.getStringValue();
		Map<String, String> idmapping = chromeMapping.getStringMapValue();
		
		ChromosomeSetContainer csc=ChromosomeSetContainer.getDefault();
		
		Species species = SpeciesContainer.getSpecies(speciesName.getStringValue());

		LocusMap lm = new LocusMap(files.get(0)+ (files.size()>1?"... ("+files.size()+" files)":""));

		long numericId=0;		
		
		for (int i=0; i!=files.size(); ++i) {

			try {
				BufferedReader br = new BufferedReader(new FileReader(files.get(i)));
				String line;
				Integer version = gffVersions.get(files.get(i));
				
				while (br.ready()) {
					line = br.readLine();
					mainLine.replaceLine(line);
					
					// gather features etc
					if (!mainLine.isCommentLine() && mainLine.size()>=8) {
						String seqname = mainLine.get(0);
						String ftype = mainLine.get(2);
						if (desiredFeatureTypes.contains(ftype)) {
							String name = null;
							if (desiredAttribute==CONSTRUCT_NUMERIC_IDS) {
								name = Long.toString(++numericId);
							} else {
								if (mainLine.size()>=9) {
									String attributes = mainLine.get(8);
									if (version!=null) {
										attribLine.replaceLine(attributes);
										for (String attrib : attribLine) {
											String[] parts = null;
											if (version==2) 
												parts = attrib.trim().split(" ");
											else if (version==3) {
												parts = attrib.trim().split("=");
											}									
											if (parts!=null && parts.length>0) {
												String key = parts[0].trim();
												if (key.equalsIgnoreCase(desiredAttribute)) {
													name = parts[1].trim();
													// 	we pick all the values provided
													if (version==2) {
														// 	space separated --> join
														for (int j=3; j<parts.length; ++j) 
															name += ","+parts[j].trim();
													} else if (version==3){
														// already comma separated, nothing to do
													}
												}
											}
										}
									}
								}
							}
							// OK, i found a feature of correct type, got a name , lets get a coordinate
							seqname = idmapping.get(seqname);
							Chromosome c = csc.getChromosome(species, seqname);
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
								GeneticCoordinate g = new GeneticCoordinate(c, strand, start, stop);
								lm.put(name, g);
							} catch (Exception ex) {
								System.out.println("Could not parse coordinate "+ex);
								ex.printStackTrace();
							}
						}
					}					
				}

				
			} catch (Exception ex) {
				System.out.println("Problem parsing "+files.get(i)+": "+ex);
				ex.printStackTrace();
			}
			
		}		
		
		return lm;

	}




}
