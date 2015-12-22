package mayday.core.structures.natives.mmap;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;

import mayday.core.pluma.AbstractPlugin;
import mayday.core.pluma.Constants;
import mayday.core.pluma.PluginInfo;
import mayday.core.pluma.PluginManagerException;
import mayday.core.pluma.prototypes.CorePlugin;
import mayday.core.settings.Setting;
import mayday.core.settings.generic.HierarchicalSetting;
import mayday.core.settings.typed.BooleanSetting;
import mayday.core.settings.typed.PathSetting;

public class ScratchDiskPreferences extends AbstractPlugin implements CorePlugin {

	protected static HierarchicalSetting sett;
	protected static PathSetting tempPath;
	protected static BooleanSetting useMMap;
	
	public void init() {
		/* Nice to have: clear out the scratch directories that were used last time, if mayday wasn't terminated correctly */		
	}

	@SuppressWarnings("unchecked")
	public PluginInfo register() throws PluginManagerException {
		return new PluginInfo(
				(Class)this.getClass(),
				"PAS.core.ScratchdiskSettings",
				new String[]{},
				Constants.MC_CORE,
				new HashMap<String, Object>(),
				"Florian Battke",
				"battke@informatik.uni-tuebingen.de",
				"Manages Mayday's scratch disk preferences",
				"Memory management"
		);
	}

	public static PathSetting getPathSetting() {
		initSetting();
		return tempPath;
	}
	
	public static void initSetting() {
		if (sett == null) {
			tempPath = new PathSetting("Temporary directory",
					"Mayday will store temporary files to this directory.\n" +
					"It should offer lots of free space and be fast (i.e. not on a network share).\n" +
					"If left empty, Mayday will use the system temp folder.\n" +
					"Changes are implemented after a restart of Mayday.","",true,true,true
					);
			useMMap = new BooleanSetting("Enable memory mapping",
					"Memory mapping is a technique that allows Mayday to efficiently\n" +
					"use temporary files to expand it's working memory. Generally,\n" +
					"memory mapping is faster than using swap space and it does not\n" +
					"require to start Mayday with very large heap sizes to process\n" +
					"large datasets (e.g. using SeaSight).\n" +
					"Changes are implemented after a restart of Mayday.\n" +
					"It is recommended to activate this setting.", true
					);
			sett = new HierarchicalSetting("Memory Management").addSetting(useMMap).addSetting(tempPath);
			PluginInfo.loadDefaultSettings(sett, "PAS.core.ScratchdiskSettings");
		}
	}
	
	public Setting getSetting() {		
		initSetting();
		return sett;
	}
	
	public void run() {
		getSetting();
	}
	
	public static boolean useMMap() {
		initSetting();
		return useMMap.getBooleanValue();		
	}
	
	
	public static File getScratchDir() {
		initSetting();
		String path = getPathSetting().getStringValue();
		File f;
		if (path!=null) {
			f = new File(path);
			if (path.length()>0 && f.exists() && f.isDirectory())
				return f;
		}
		f = new File(System.getProperty("java.io.tmpdir"));
		return f;
		
	}
	
	/** returns a new subdirectory of the scratch directory 
	 * @throws IOException */
	public static File getScratchDir(String prefix) throws IOException {
		File scratch = getScratchDir();
		File ret = File.createTempFile(prefix, "", scratch);
		ret.delete();
		ret.mkdirs();
		ret.deleteOnExit();
		return ret;
	}

}
