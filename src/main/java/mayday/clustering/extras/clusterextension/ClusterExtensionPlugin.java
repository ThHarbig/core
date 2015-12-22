package mayday.clustering.extras.clusterextension;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;

import mayday.clustering.ClusterPlugin;
import mayday.core.DataSet;
import mayday.core.MasterTable;
import mayday.core.MaydayDefaults;
import mayday.core.ProbeList;
import mayday.core.pluma.Constants;
import mayday.core.pluma.PluginInfo;
import mayday.core.pluma.PluginManagerException;
import mayday.core.pluma.prototypes.ApplicableFunction;
import mayday.core.pluma.prototypes.ProbelistPlugin;
import mayday.core.settings.SettingDialog;
import mayday.core.tasks.AbstractTask;

public class ClusterExtensionPlugin extends ClusterPlugin implements ProbelistPlugin, ApplicableFunction {

	public ClusterExtensionPlugin() {}

	@Override
	public List<ProbeList> run(final List<ProbeList> probeLists, final MasterTable masterTable) {
		
		final LinkedList<ProbeList> cpls = new LinkedList<ProbeList>();
		
		
		DataSet ds = masterTable.getDataSet();
		
		// setting
		final ClusterExtensionSetting settings = new ClusterExtensionSetting(ds);
		
		// setting not canceled
		SettingDialog sd = new SettingDialog(null, settings.getName(), settings);  
		sd.showAsInputDialog();	
		
		if (sd.canceled()) {
			return null;
		}

		AbstractTask cTask = new AbstractTask("Cluster Extension") {

			protected void initialize() {}

			protected void doWork() throws Exception {
				ClusterExtension extend = new ClusterExtension();
				extend.setClusterTask(this);
				cpls.addAll(extend.extendCluster(masterTable, probeLists, settings));
			}
		};
		cTask.start();
		cTask.waitFor();
		
		return cpls;
	}
	
	@Override
	public PluginInfo register() throws PluginManagerException {
		PluginInfo pli = new PluginInfo(
				this.getClass(),
				"IT.clustering.ClusterExtension",
				new String[0],
				Constants.MC_PROBELIST,
				new HashMap<String, Object>(),
				"Jennifer Lange",
				"langej@informatik.uni-tuebingen.de",
				"Extends existing clustering with Probes from the selected Probelist",
				"Add probes to clustering"
		);
		pli.addCategory(MaydayDefaults.Plugins.CATEGORY_CLUSTERING+"/"+MaydayDefaults.Plugins.SUBCATEGORY_CLUSTERINGEXTRAS);
		return pli;
	}

	@Override
	public void init() {}

	@Override
	public boolean isApplicable(Object... o) {
		return o.length > 0;
	}
}
