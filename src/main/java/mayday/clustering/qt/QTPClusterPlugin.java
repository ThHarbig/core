package mayday.clustering.qt;

import java.util.HashMap;
import java.util.List;

import mayday.clustering.qt.algorithm.QTPPluginBase;
import mayday.clustering.qt.algorithm.QTPSettings;
import mayday.core.MasterTable;
import mayday.core.ProbeList;
import mayday.core.pluma.Constants;
import mayday.core.pluma.PluginInfo;
import mayday.core.pluma.PluginManagerException;
import mayday.core.pluma.prototypes.ProbelistPlugin;
import mayday.core.settings.SettingsDialog;

/**
 * @author Sebastian Nagel
 * @version 0.1
 */
public class QTPClusterPlugin extends QTPPluginBase implements ProbelistPlugin {

	PluginInfo pli;
	private QTPSettings settings;
	
	@SuppressWarnings({ "unchecked", "rawtypes" })
	public PluginInfo register() throws PluginManagerException {
		pli= new PluginInfo(
				(Class)this.getClass(),
				"IT.clustering.qt",
				new String[]{},
				Constants.MC_PROBELIST,
				(HashMap<String,Object>)null,
				"G\u00FCnter J\u00E4ger",
				"no e-mail provided",
				"QT-Clustering is a quality-based cluster technique " +
				"specially designed to identify co-expressed genes.",
		"Quality-based (QT-Clustering)");
		pli.addCategory(CATEGORY);
		return pli;
	}

	public List<ProbeList> run(List<ProbeList> probeLists, MasterTable masterTable) throws RuntimeException {
		if (settings == null)
			settings = new QTPSettings();

		SettingsDialog setupDialog = settings.getDialog();
		setupDialog.setModal(true);
		setupDialog.setVisible(true);
		if (setupDialog.canceled())
			return null;

		return runWithSettings(probeLists, masterTable, settings);
	}
	
	/**
	 * @param settings
	 */
	public void setSetting(QTPSettings settings) {
		this.settings = settings;
	}
}

