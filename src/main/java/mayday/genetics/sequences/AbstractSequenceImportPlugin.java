package mayday.genetics.sequences;

import java.util.Arrays;
import java.util.List;

import mayday.core.gui.PreferencePane;
import mayday.core.io.gudi.GUDIConstants;
import mayday.core.pluma.AbstractPlugin;
import mayday.core.pluma.PluginInfo;
import mayday.core.pluma.PluginManager;
import mayday.core.settings.Setting;
import mayday.core.settings.typed.FilesSetting;
import mayday.core.settings.typed.PathSetting;
import mayday.core.tasks.AbstractTask;

public abstract class AbstractSequenceImportPlugin extends AbstractPlugin {

	public final static String MC = "SequenceData/Import";
		
	protected Setting mySetting;
	protected boolean multiSelection=false, directories=false;

	public Setting getSetting() {
		if (mySetting==null) {			
			if (this instanceof SequenceFileImportPlugin) {
				PluginInfo pli = PluginManager.getInstance().getPluginFromClass(getClass());
				Integer type = (Integer)(pli.getProperties().get(GUDIConstants.FILESYSTEM_IMPORTER_TYPE));
				if (type==null) {
					System.err.println("GUDI: "+pli.getIdentifier()+" has no valid FILESYSTEM_IMPORTER_TYPE");
				} else {
					switch (type) {
					case GUDIConstants.ONEFILE:
						multiSelection = false;
						directories = false;
						break;
					case GUDIConstants.MANYFILES:
						multiSelection = true;
						directories = false;
						break;
					case GUDIConstants.DIRECTORY:
						multiSelection = false;
						directories = true;
						break;			        
					} 
				}
				mySetting = multiSelection ? new FilesSetting("Input Files",null,null,false,null) :
					(new PathSetting("Input "+(directories?"directory":"file"),null,null,directories,true,false));
			}
		}
		return mySetting;
	}

	public void run(final SequenceContainer sc) {
		AbstractTask at = new AbstractTask("Importing sequence data") {
			public void doWork() {
				if (AbstractSequenceImportPlugin.this instanceof SequenceFileImportPlugin) {
					SequenceFileImportPlugin dsip = (SequenceFileImportPlugin)(AbstractSequenceImportPlugin.this);
					List<String> files;
					if (multiSelection)
						files = ((FilesSetting)mySetting).getFileNames();
					else
						files = Arrays.asList(new String[]{((PathSetting)mySetting).getStringValue()});			
					dsip.importFrom(files,sc);			
				} else if (AbstractSequenceImportPlugin.this instanceof SequenceImportPlugin){
					SequenceImportPlugin dsip = (SequenceImportPlugin)(AbstractSequenceImportPlugin.this);
					dsip.importFrom(sc);
				}
			}
			protected void initialize() {}
		};
		at.start();		
	}
	
	public void run() {
		run(SequenceContainer.getDefault());
	}
	
	public PreferencePane getPreferencesPanel() {
        return null;
    }

	
}
