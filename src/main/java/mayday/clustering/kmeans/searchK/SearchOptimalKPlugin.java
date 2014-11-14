package mayday.clustering.kmeans.searchK;

import java.util.HashMap;
import java.util.List;

import mayday.clustering.ClusterPlugin;
import mayday.core.MasterTable;
import mayday.core.Mayday;
import mayday.core.ProbeList;
import mayday.core.pluma.Constants;
import mayday.core.pluma.PluginInfo;
import mayday.core.pluma.PluginManagerException;
import mayday.core.pluma.prototypes.ProbelistPlugin;
import mayday.core.settings.SettingDialog;

public class SearchOptimalKPlugin extends ClusterPlugin implements ProbelistPlugin {

	@Override
	public List<ProbeList> run(List<ProbeList> probeLists,
			MasterTable masterTable) {
		
		SearchKSetting setting = new SearchKSetting();
		
		SettingDialog sd = new SettingDialog(Mayday.sharedInstance, "Search k for k-Means Setting", setting);
		sd.showAsInputDialog();
		
		if(!sd.closedWithOK()) {
			return null;
		}
		
		SearchKPlot plot = new SearchKPlot(setting, probeLists, masterTable);
		plot.setVisible(true);
		plot.calculate();

		return null;
	}

	@SuppressWarnings({ "unchecked", "rawtypes" })
	@Override
	public PluginInfo register() throws PluginManagerException {
		PluginInfo pli= new PluginInfo(
				(Class)this.getClass(),
				"PAS.clustering.kmeans.searchK",
				new String[]{"PAS.clustering.kmeans"}, 
				Constants.MC_PROBELIST,
				(HashMap<String,Object>)null,
				"Günter Jäger",
				"jaeger@informatik.uni-tuebingen.de",
				"Investigation of the data inherent number of clusters with a series of k-Mean clusterings.",
				"Partitioning (k-Means, find optimal k)");
		pli.addCategory(CATEGORY);
		return pli;
	}

	@Override
	public void init() {}
}
