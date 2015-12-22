package mayday.genetics.sequences;

import java.io.File;
import java.util.HashMap;
import java.util.List;

import mayday.core.io.gudi.GUDIConstants;
import mayday.core.pluma.PluginInfo;
import mayday.core.pluma.PluginManagerException;
import mayday.core.settings.Setting;
import mayday.core.settings.generic.HierarchicalSetting;
import mayday.core.settings.typed.BooleanSetting;
import mayday.core.settings.typed.StringSetting;

public class FastaSequenceImportPlugin extends AbstractSequenceImportPlugin implements SequenceFileImportPlugin {

	@Override
	public void init() {}

	public PluginInfo register() throws PluginManagerException {
		PluginInfo pli = new PluginInfo(
				this.getClass(),
				AbstractSequenceImportPlugin.MC+".FASTA",
				new String[0],
				AbstractSequenceImportPlugin.MC,
				new HashMap<String, Object>(),
				"Florian Battke",
				"battke@informatik.uni-tuebingen.de",
				"Reads sequence data from FastA files",
				"From FastA files"
		);
		pli.getProperties().put(GUDIConstants.IMPORTER_TYPE, GUDIConstants.IMPORTERTYPE_FILESYSTEM);
		pli.getProperties().put(GUDIConstants.FILESYSTEM_IMPORTER_TYPE, GUDIConstants.MANYFILES);
		pli.getProperties().put(GUDIConstants.FILE_EXTENSIONS,"fa|fasta|fna");
		pli.getProperties().put(GUDIConstants.TYPE_DESCRIPTION,"FastA files");		
		return pli;
	}
	
	HierarchicalSetting hsett;
	StringSetting species;
	BooleanSetting parseHeader;
	
	public Setting getSetting() {
		if (hsett==null) {
			species = new StringSetting("Species name",null,"");
			parseHeader = new BooleanSetting("Parse FastA header",
					"If checked, tries to extract chromosome names from NCBI headers\n" +
					"and discards header comments (everything after the first space).\n" +
					"If unchecked, uses the full header line.",true);
			hsett = new HierarchicalSetting("FastA sequence import")
			.addSetting(super.getSetting())
			.addSetting(species)
			.addSetting(parseHeader);
		}
		return hsett;
		
	}
	
	@Override
	public void importFrom(List<String> files, SequenceContainer sc) {
		getSetting();
		String sspecies = species.getStringValue();
		for (String f : files)
			try {
				FastaChromosomeSequenceFactory.addChromosomeSequences(sc, new File(f), sspecies, parseHeader.getBooleanValue());
			} catch (Exception e) {
				System.err.println("Could not load sequences from "+f);
				e.printStackTrace();
			}
	}

}
