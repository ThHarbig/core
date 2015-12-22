package mayday.genetics.sequences;

import mayday.core.settings.SettingDialog;
import mayday.core.settings.generic.PluginTypeSetting;

public class SequenceImport {
	
	public static void run(SequenceContainer sc) {
		
		PluginTypeSetting<AbstractSequenceImportPlugin> s = new PluginTypeSetting<AbstractSequenceImportPlugin>(
				"Import Plugin",
				"Select how to import sequence data", 
				new FastaSequenceImportPlugin(),
				AbstractSequenceImportPlugin.MC
		);
		
		SettingDialog sd = new SettingDialog(null, "Import Sequence Information", s).showAsInputDialog();
		
		if (!sd.canceled()) {
			AbstractSequenceImportPlugin ap = s.getInstance();
			ap.run(sc);
		}
		
	}

}
