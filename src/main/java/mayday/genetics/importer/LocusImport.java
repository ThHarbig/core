package mayday.genetics.importer;

import mayday.core.settings.SettingDialog;
import mayday.core.settings.generic.PluginTypeSetting;
import mayday.genetics.importer.csv.LocusImporter;
import mayday.genetics.locusmap.LocusMap;
import mayday.genetics.locusmap.LocusMapContainer;

public class LocusImport {
	
	public static LocusMap run() {
		
		PluginTypeSetting<AbstractLocusImportPlugin> s = new PluginTypeSetting<AbstractLocusImportPlugin>(
				"Import Plugin",
				"Select how to import locus data", 
				new LocusImporter(), 
				AbstractLocusImportPlugin.MC
		);
		
		SettingDialog sd = new SettingDialog(null, "Import/Create Locus Information", s).showAsInputDialog();
		
		if (!sd.canceled()) {
			AbstractLocusImportPlugin ap = s.getInstance();
			LocusMap lm = ap.run();
			if (lm!=null)
				LocusMapContainer.INSTANCE.add(lm);
			return lm;
		}
		return null;		
	}

}
