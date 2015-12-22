/*
 * Created on 29.11.2005
 */
package mayday.genetics.importer.genbank;

import java.io.BufferedReader;
import java.io.FileReader;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;

import javax.swing.JLabel;

import mayday.core.io.ReadyBufferedReader;
import mayday.core.io.gudi.GUDIConstants;
import mayday.core.pluma.PluginInfo;
import mayday.core.pluma.PluginManagerException;
import mayday.core.settings.SettingDialog;
import mayday.core.settings.generic.ComponentPlaceHolderSetting;
import mayday.core.settings.generic.HierarchicalSetting;
import mayday.core.settings.generic.MultiselectObjectListSetting;
import mayday.core.settings.generic.ObjectSelectionSetting;
import mayday.core.settings.generic.SelectableHierarchicalSetting;
import mayday.core.settings.typed.BooleanSetting;
import mayday.core.settings.typed.RestrictedStringSetting;
import mayday.genetics.advanced.VariableComplexGeneticCoordinate;
import mayday.genetics.advanced.VariableGeneticCoordinateElement;
import mayday.genetics.basic.ChromosomeSetContainer;
import mayday.genetics.basic.coordinate.GeneticCoordinate;
import mayday.genetics.coordinatemodel.GBNode;
import mayday.genetics.coordinatemodel.GBParser;
import mayday.genetics.importer.AbstractLocusImportPlugin;
import mayday.genetics.importer.DefaultLocusSetting;
import mayday.genetics.importer.LocusFileImportPlugin;
import mayday.genetics.importer.SelectableDefaultLocusSetting;
import mayday.genetics.importer.csv.LocusColumnTypes.CTYPE;
import mayday.genetics.locusmap.LocusMap;

public class LocusImporter extends AbstractLocusImportPlugin implements LocusFileImportPlugin {

	public PluginInfo register() throws PluginManagerException {
		PluginInfo pli = new PluginInfo(
				this.getClass(),
				AbstractLocusImportPlugin.MC+".GENBANK",
				new String[0],
				AbstractLocusImportPlugin.MC,
				new HashMap<String, Object>(),
				"Florian Battke",
				"battke@informatik.uni-tuebingen.de",
				"Reads locus data from GenBank files",
				"From GenBank files"
		);
		pli.getProperties().put(GUDIConstants.IMPORTER_TYPE, GUDIConstants.IMPORTERTYPE_FILESYSTEM);
		pli.getProperties().put(GUDIConstants.FILESYSTEM_IMPORTER_TYPE, GUDIConstants.MANYFILES);
		pli.getProperties().put(GUDIConstants.FILE_EXTENSIONS,"gbk|gb|genbank");
		pli.getProperties().put(GUDIConstants.TYPE_DESCRIPTION,"GenBank files (gbk,...)");		
		return pli;
	}

	public void init() {}

	protected final static int FTYPE_POS = 5;
	protected final static int ATTRIB_POS = 20;

	protected String FORMAT="GenBank";
	protected String RECORD_START="LOCUS";
	protected String ACCESSION_START="ACCESSION";
	protected String FEATURES_START="FEATURES";
	protected String FEATURE_LINE=" ";
	
	/** parse one GenBank LOCUS record. 
	 * @param br a reader over the genbank input
	 * @param featureTypes The feature types to import
	 * @param namingAttribute the attribute to use for naming each feature found. if NULL, the record's ACCESSION
	 * field will be used, but then only one feature per locus is imported (the first one to be found of a fitting
	 * type, to be precise).
	 * @param lm the LocusMap we are importing INTO
	 * @param tmp the genetic coordinate object used for speedup
	 * @param csc the ChromosomeSetContainer used as a coordinate basis
	 * @param defaultSpecies the species to use if no species if found in the source feature or if ignoreSourceSpecies is set
	 * @param defaultChrome the chromosome to use if no species if found in the source feature or if ignoreSourceChrome is set
	 * @param ignoreSourceSpecies if true, the "source" feature attribute "organism" will be ignored
	 * @param ignoreSourceChromosome if true, the "source" feature attribute "chromosome" will be ignored
	 */
	protected void parseRecord(BufferedReader br, List<String> featureTypes, String namingAttribute, 
							   LocusMap lm, VariableComplexGeneticCoordinate tmp, 
							   ChromosomeSetContainer csc, 
							   boolean ignoreSourceSpecies, boolean ignoreSourceChrome) throws Exception {
		String line = null;
		
		// skip until a new LOCUS begins
		while (br.ready() && (!(line=br.readLine()).startsWith(RECORD_START)));
		
		boolean useAttributeForNaming = namingAttribute!=null;
		
		String currentFeature = null;
		String position = null;				
		String name = null;
		String species = null;
		String chrome = null;
		
		boolean specialSourceHandling = false;
		
		if (!useAttributeForNaming) {
			// skip until finding the ACCESSION id
			while (br.ready() && (!(line=br.readLine()).startsWith(ACCESSION_START)));
			
			if (line==null)
				return;
			
			String[] accparts = line.split("[\\s]+");
			if (accparts.length<2)
				return;
			name = accparts[1];
		}
		
		// skip until finding the FEATURES list
		while (br.ready() && (!(line=br.readLine()).startsWith(FEATURES_START)));
		
		do {
			br.mark(1000); // test one line
			line = br.readLine();
		} while (line!=null && !line.startsWith(FEATURE_LINE));
		br.reset();

		// read until end of locus FEATURE list
		while (br.ready() && (line=br.readLine()).startsWith(FEATURE_LINE)) { 

			if (line.length()<ATTRIB_POS)
				continue; // ignore too short line
			
			if (line.charAt(FTYPE_POS)!=' ') {
				// a feature is starting
				finishFeature(currentFeature, name, position, lm, tmp);
				if (useAttributeForNaming) {
					position=null;
					name = null;
				}
				
				currentFeature = line.substring(FTYPE_POS, ATTRIB_POS).trim();
				
				specialSourceHandling = currentFeature.equals("source") && !(ignoreSourceChrome && ignoreSourceSpecies);
				
				if (featureTypes.contains(currentFeature) && position==null) {
					// there MUST be a location on the same line
					position = line.substring(ATTRIB_POS).trim();
					// and it may span several lines
					int openbraces = 0;
					for (int i=0; i!=position.length(); ++i) {
						if (position.charAt(i)=='(')
							++openbraces;
						else if (position.charAt(i)==')')
							--openbraces;
					}
					while (openbraces>0 && br.ready()) {
						String morePosition = br.readLine().substring(ATTRIB_POS).trim();
						for (int i=0; i!=morePosition.length(); ++i) {
							if (morePosition.charAt(i)=='(')
								++openbraces;
							else if (morePosition.charAt(i)==')')
								--openbraces;
						}
						position+=morePosition;
					}
					
				} else {
					currentFeature = null; // no feature of interest
				}
				
			} else if ((useAttributeForNaming && currentFeature!=null) || specialSourceHandling) {
				// parse all attributes into a hashmap
				HashMap<String, String> attributeTable = new HashMap<String, String>();
				while (line.length()>FTYPE_POS && line.charAt(FTYPE_POS)==' ' && line.startsWith(FEATURE_LINE)) { // parse ALL attributes
					String attrib = line.substring(ATTRIB_POS).trim();
					if (attrib.length()>0) {
						if (attrib.charAt(0)=='/') { // attribute, handle this now
							attrib = attrib.substring(1);
							if (attrib.contains("=")) { 
								String key = attrib.substring(0, attrib.indexOf("="));
								String val = attrib.substring(key.length()+1).trim();
								if (val.startsWith("\"")) {
									// remove leading quote
									val = val.substring(1);
									if (!val.contains("\"")) { // no trailing quote so far, read more lines until found
										do {
											line = br.readLine();
											val += line;
										} while (br.ready() && !line.contains("\""));
									}
									val = val.replace("\"", ""); //remove remaining quotes
								}
								attributeTable.put(key, val);
							}
						} 
					}
					br.mark(500);
					line = br.readLine();
				}
				br.reset();

				
				// use the hashmap to find source organism/chrome
				if (specialSourceHandling) {
					species = attributeTable.get("organism");
					if (species!=null && !ignoreSourceSpecies)
						tmp.update(CTYPE.Species.ordinal(), species);
					chrome = attributeTable.get("chromosome");	
					if (chrome!=null && !ignoreSourceChrome)
						tmp.update(CTYPE.Chromosome.ordinal(), chrome);
				}
				
				// extract the name from attributes of interest
				if (currentFeature!=null && useAttributeForNaming) {
					String nameVal = attributeTable.get(namingAttribute);
					if (nameVal!=null) {
						if (name==null)
							name = nameVal;
						else
							name +=", " + nameVal;
					}
				}
			} 
		}
		
		finishFeature(currentFeature, name, position, lm, tmp);
		
	}
	
	
	public LocusMap importFrom(List<String> files) {
		
		// first find the available feature names and attribute types
		HashSet<String> availableAttributeTypes = new HashSet<String>(); 
		HashSet<String> featureTypes = new HashSet<String>(); 
		
		getAvailableAttributes(files, availableAttributeTypes, featureTypes);

		
		DefaultLocusSetting defaultLocus = null;
		SelectableDefaultLocusSetting[] fileDefaultLocus = new SelectableDefaultLocusSetting[files.size()];
		
		HierarchicalSetting topSet = new HierarchicalSetting("Fill in missing locus information");
		String istr = "<html>Not all "+FORMAT+" files contain species and chromosome information<br>" +
		"Please supply the missing information below.<br>" +
		(files.size()>1?"You can also supply per-file information.":"");
		defaultLocus = new DefaultLocusSetting();
		defaultLocus.hideElements(false, false, true, true);
		topSet.addSetting(new ComponentPlaceHolderSetting("info", new JLabel(istr)));
		topSet.addSetting(defaultLocus);
		if (files.size()>1) {
			HierarchicalSetting perFileLocus = new HierarchicalSetting("Per-file settings");
			perFileLocus.setLayoutStyle(HierarchicalSetting.LayoutStyle.TREE);			
			topSet.addSetting(perFileLocus);
			int i=0;
			for (String f : files) {
				SelectableDefaultLocusSetting ns = new SelectableDefaultLocusSetting(f);
				ns.hideElements(false, false, true, true);
				ns.setOverride(false, true, false, false);
				fileDefaultLocus[i++] = ns;
				perFileLocus.addSetting(ns);  
			}
		}
		
		BooleanSetting alwaysOverrideSpecies = new BooleanSetting("Always override \"source\" species",null,false);
		BooleanSetting alwaysOverrideChrome = new BooleanSetting("Always override \"source\" Chromosome",null,false);
		topSet.addSetting(alwaysOverrideSpecies).addSetting(alwaysOverrideChrome);
			
		ChromosomeSetContainer csc=ChromosomeSetContainer.getDefault();
		VariableComplexGeneticCoordinate tmp = new VariableComplexGeneticCoordinate(csc);
		
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
		
		SelectableHierarchicalSetting namingSource = new SelectableHierarchicalSetting(
				"Naming of imported features",
				"You can either select an attribute value as a source for names, or use the ACCESSION provided for each LOCUS.\n" +
				"If using the ACCESSION, only one feature can be imported per Locus, and it will be the first feature in the\n" +
				"record that matches your selection of features to extract.",
				1,
				new Object[]{"Locus ACCESSION",attributeToExtract}				
		);

		HierarchicalSetting conversionSettings = new HierarchicalSetting(FORMAT+" import settings")
		.addSetting(featuresToExtract)
		.addSetting(namingSource)
		.addSetting(topSet);

		SettingDialog sd = new SettingDialog(null, FORMAT+" import settings", conversionSettings);
		sd.showAsInputDialog();
		if (sd.canceled())
			return null;

		List<String> desiredFeatureTypes = featuresToExtract.getSelection();
		String desiredAttribute = 
			namingSource.getSelectedIndex()==0?null:attributeToExtract.getStringValue();
		
		
		LocusMap lm = new LocusMap(files.get(0)+ (files.size()>1?"... ("+files.size()+" files)":""));
		
		for (int i=0; i!=files.size(); ++i) {

			try {
				// build the default locus
				SelectableDefaultLocusSetting sdls = fileDefaultLocus[i];
				
				String chrome = (sdls!=null && sdls.overrideChromosome())?sdls.getChromosome():defaultLocus.getChromosome();
				String specie = (sdls!=null && sdls.overrideSpecies())?sdls.getSpecies():defaultLocus.getSpecies();

				BufferedReader br = new ReadyBufferedReader(new FileReader(files.get(i)));

				while (br.ready()) {
					tmp.update(CTYPE.Species.ordinal(), specie);
					tmp.update(CTYPE.Chromosome.ordinal(), chrome);
					parseRecord(br, desiredFeatureTypes, desiredAttribute, lm, tmp, csc, alwaysOverrideSpecies.getBooleanValue(), alwaysOverrideChrome.getBooleanValue());
				}
				
				
			} catch (Exception ex) {
				System.out.println("Problem parsing "+files.get(i)+": "+ex);
				ex.printStackTrace();
			}
			
		}
		
		return lm;
	}
	
	protected void getAvailableAttributes(List<String> files, HashSet<String> availableAttributeTypes, HashSet<String> featureTypes) {
		
		availableAttributeTypes.clear();
		featureTypes.clear();
		
		String[] attributeTypes = new String[]{
				"ID", "locus_tag", "standard_name", "gene", "gene_synonym", "old_locus_tag", "protein_id", "product", "transcript_id"
		};		
		HashSet<String> missingAttributeTypes = new HashSet<String>();
		for (String s : attributeTypes) {
			missingAttributeTypes.add(s);
		}
		
		for (int i=0; i!=files.size(); ++i) {

			try {
				BufferedReader br = new ReadyBufferedReader(new FileReader(files.get(i)));
				String line;
				
				// skip header
				while (br.ready() && (!(line=br.readLine()).startsWith(FEATURES_START)));

				do {
					br.mark(1000); // test one line
					line = br.readLine();
				} while (line!=null && !line.startsWith(FEATURE_LINE));
				br.reset();

				String currentFeature = null;
				
				while (br.ready() && (line=br.readLine()).startsWith(FEATURE_LINE)) {

					if (line.length()<ATTRIB_POS)
						continue; // ignore too short line
					
					if (line.charAt(FTYPE_POS)!=' ' && line.charAt(FTYPE_POS-1)==' ') {
						// a feature is starting
						currentFeature = line.substring(FTYPE_POS, ATTRIB_POS).trim();
						featureTypes.add(currentFeature);
					}
					if (currentFeature!=null && missingAttributeTypes.size()>0) {
						String attrib = line.substring(ATTRIB_POS).trim();
						if (attrib.length()>0) {
							if (attrib.charAt(0)=='/') { // attribute start, handle this now
								attrib = attrib.substring(1);
								if (attrib.contains("=")) { 
									String key = attrib.substring(0, attrib.indexOf("="));
									missingAttributeTypes.remove(key);
								}
							} 
						}
					}
					
				}
			} catch (Exception ex) {
				System.out.println("Problem parsing "+files.get(i)+": "+ex);
				ex.printStackTrace();
			}
			
		}
		
		availableAttributeTypes.addAll(Arrays.asList(attributeTypes));
		availableAttributeTypes.removeAll(missingAttributeTypes);
	}


	protected void finishFeature(String currentString, String name, String position, LocusMap lm, VariableComplexGeneticCoordinate tmp) {
		if (currentString==null || name ==null || position==null)
			return;
		
		// parse the genbank position line
		
		// we remove the "unknown boundary" / "start outside of sequence" indicators
		position = position.replace("<", "");
		position = position.replace(">", "");
				
		if (position.contains(":")) {
			System.err.println("Can not handle foreign reference locations, such as "+position);
			return;
		}
		
		GBNode coordModel = GBParser.parse(position);
//		Strand s = coordModel.getStrand();
		long start = coordModel.getStart();
		long stop = coordModel.getEnd();
		if (start==-1 || stop==-1) {
			System.err.println("Could not assign a valid position to location: "+position);
			return;
		}
		
		try {
			tmp.update(VariableGeneticCoordinateElement.Model, coordModel);
			lm.put(name, new GeneticCoordinate(tmp));
		} catch (Exception ex) {
			System.err.println("Could not parse location: "+position);
			ex.printStackTrace();
		}
		
	}

}
