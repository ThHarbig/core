package mayday.core.plugins;

import java.io.File;
import java.util.HashMap;

import mayday.core.DelayedUpdateTask;
import mayday.core.Mayday;
import mayday.core.MaydayDefaults;
import mayday.core.datasetmanager.DataSetManager;
import mayday.core.io.dataset.ZippedSnapshot.Export;
import mayday.core.pluma.AbstractPlugin;
import mayday.core.pluma.Constants;
import mayday.core.pluma.PluginInfo;
import mayday.core.pluma.PluginManager;
import mayday.core.pluma.PluginManagerException;
import mayday.core.pluma.prototypes.CorePlugin;
import mayday.core.settings.Setting;
import mayday.core.settings.events.SettingChangeEvent;
import mayday.core.settings.events.SettingChangeListener;
import mayday.core.settings.generic.HierarchicalSetting;
import mayday.core.settings.typed.BooleanSetting;
import mayday.core.settings.typed.IntSetting;
import mayday.core.tasks.TaskManager;

public class Autosave extends AbstractPlugin implements CorePlugin, SettingChangeListener {

	
	protected static HierarchicalSetting setting;
	protected static IntSetting minutes;
	protected static BooleanSetting active;
	protected static IntSetting maxNum;
	protected static DelayedUpdateTask dup;
	
	protected int current=0;
	
	public void init() {
	}

	public static boolean suppress = false;
	
	@SuppressWarnings("unchecked")
	public PluginInfo register() throws PluginManagerException {
		PluginInfo pli = new PluginInfo(
				(Class)this.getClass(),
				"PAS.core.Autosave",
				new String[0],
				Constants.MC_CORE,
				new HashMap<String, Object>(),
				"Florian Battke",
				"battke@informatik.uni-tuebingen.de",
				"Periodically saves your current mayday workspace",
				"Auto-Save Workspace"
		);
		pli.setIcon(MaydayDefaults.PROGRAM_ICON_IMAGE);
		return pli;	
	}
	
	public Setting getSetting() {
		Setting s = getSettingStatic();
		s.addChangeListener(this);
		return s;
	}
	
	public static Setting getSettingStatic() {
		if (setting==null) {
			active = new BooleanSetting("Active",null,false);
			minutes = new IntSetting("Time between saves", null, 15, 1, null, true, false);
			maxNum = new IntSetting("Maximum number of autosave files",null,5,3,null,true,false);
			setting = new HierarchicalSetting("Auto-save the workspace regularly").addSetting(active).addSetting(minutes).addSetting(maxNum);
			PluginInfo.loadDefaultSettings(setting, "PAS.core.Autosave");			
		}
		return setting; 
	}

	public void run() {		
		getSetting();		
		dup = new DelayedUpdateTask("Autosave "+minutes.getIntValue(),minutes.getIntValue()*1000*60) {
		
			@Override
			protected void performUpdate() {
				
				if (this!=dup || !active.getBooleanValue())
					return;
				
				if (!suppress && DataSetManager.singleInstance.getNumberOfObjects()>0) {
					
					// only start when nothing else is running
					if (TaskManager.sharedInstance.getTasks().size()==0) {
						
						String lastTitle = Mayday.sharedInstance.getTitle();
						Mayday.sharedInstance.setTitle("[Saving...] "+ lastTitle);
						
						File saveDir = getSaveDir();
						
						// move old stuff away
						for (int i=maxNum.getIntValue()-1; i!=0; --i) {
							File fold = new File( saveDir, ""+(i-1) );
							File fnew = new File( saveDir, ""+i );
							fold.renameTo(fnew);
						}
						
						// start a new save
						File fsave = new File( saveDir, "0" ); 
						
						PluginInfo pli = PluginManager.getInstance().getPluginFromID("PAS.zippedsnapshot.write");
						Export dsi = (Export)pli.getInstance();
						
						System.out.println("Autosave: Saving "+DataSetManager.singleInstance.getNumberOfObjects()+" datasets");
						
						dsi.exportTo(DataSetManager.singleInstance.getDataSets(), fsave.getAbsolutePath(), true);
						
						Mayday.sharedInstance.setTitle(lastTitle);
					} else {
						System.out.println("Autosave: Deferring while tasks are active");
					}
					
				}
				dup.trigger();
			}
		
			@Override
			protected boolean needsUpdating() {
				return true;
			}
		};
		dup.trigger();
	}
	
	public static File getSaveDir() {
		File plugDir = new File(MaydayDefaults.Prefs.getPluginDirectory());
		File saveDir = new File(plugDir, "autosave");
		saveDir.mkdirs();
		return saveDir;
	}

	@Override
	public void stateChanged(SettingChangeEvent e) {
		run();		
	}

}
