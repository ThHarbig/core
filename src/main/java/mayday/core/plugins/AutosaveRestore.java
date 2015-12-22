package mayday.core.plugins;

import java.io.File;
import java.text.DateFormat;
import java.util.Arrays;
import java.util.Comparator;
import java.util.HashMap;
import java.util.LinkedList;

import javax.swing.text.DateFormatter;

import mayday.core.DataSet;
import mayday.core.Mayday;
import mayday.core.datasetmanager.gui.DataSetManagerView;
import mayday.core.io.gudi.prototypes.DatasetFileImportPlugin;
import mayday.core.pluma.AbstractPlugin;
import mayday.core.pluma.Constants;
import mayday.core.pluma.PluginInfo;
import mayday.core.pluma.PluginManager;
import mayday.core.pluma.PluginManagerException;
import mayday.core.pluma.prototypes.GenericPlugin;
import mayday.core.settings.Setting;
import mayday.core.settings.SettingDialog;
import mayday.core.settings.generic.HierarchicalSetting;
import mayday.core.settings.generic.ObjectSelectionSetting;
import mayday.core.settings.typed.RestrictedStringSetting;

public class AutosaveRestore extends AbstractPlugin implements GenericPlugin {

	
	protected int current=0;
	
	public void init() {
	}

	@SuppressWarnings("unchecked")
	public PluginInfo register() throws PluginManagerException {
		return new PluginInfo(
				(Class)this.getClass(),
				"PAS.core.AutosaveRestore",
				new String[0],
				Constants.MC_FILE,
				new HashMap<String, Object>(),
				"Florian Battke",
				"battke@informatik.uni-tuebingen.de",
				"Restores autosaved workspaces",
				"Restore auto-saved workspace"
		);

	}
	
	public void run() {		
		
		Autosave.suppress = true;
		
		try {
			
			File saveDir = Autosave.getSaveDir();
			
			File[] children = saveDir.listFiles();
			
			String[] predef = new String[children.length];
			
			Arrays.sort(children, new Comparator<File>() {

				public int compare(File o1, File o2) {
					return o1.getName().compareTo(o2.getName());
				}
				
			});
			
			int i=0;
			DateFormatter df = new DateFormatter(DateFormat.getDateTimeInstance());
			for (File f : children) {
				predef[i++] = df.valueToString(f.lastModified());
			}

			RestrictedStringSetting rss = new RestrictedStringSetting("File to restore",null,0,predef)
			.setLayoutStyle(ObjectSelectionSetting.LayoutStyle.LIST);
			
			Setting s = Autosave.getSettingStatic();
			
			HierarchicalSetting total = new HierarchicalSetting("Autosave/Restore")
			.addSetting(rss)
			.addSetting(s)
			.setLayoutStyle(HierarchicalSetting.LayoutStyle.TABBED);

			SettingDialog sd = new SettingDialog(Mayday.sharedInstance, "Restore auto-saved workspace", total);
			if (sd.showAsInputDialog().closedWithOK()) {
				File f = children[rss.getSelectedIndex()];
				PluginInfo pli = PluginManager.getInstance().getPluginFromID("PAS.zippedsnapshot.read");
				DatasetFileImportPlugin dsi = (DatasetFileImportPlugin)pli.getInstance();
				LinkedList<String> lf = new LinkedList<String>();
				lf.add(f.getAbsolutePath());
				for(DataSet ds : dsi.importFrom(lf)) {
					DataSetManagerView.getInstance().addDataSet(ds);
				}
			}
			
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			Autosave.suppress = false;
		}
		
	}

}
