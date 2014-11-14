package mayday.core.plugins.dataset;

import java.util.HashMap;
import java.util.List;

import mayday.core.DataSet;
import mayday.core.ProbeList;
import mayday.core.pluma.AbstractPlugin;
import mayday.core.pluma.Constants;
import mayday.core.pluma.PluginInfo;
import mayday.core.pluma.PluginManagerException;
import mayday.core.pluma.prototypes.DatasetPlugin;
import mayday.dynamicpl.DynamicProbeList;

public class RevalidateProbeLists extends AbstractPlugin implements DatasetPlugin {

	@SuppressWarnings({ "unchecked", "rawtypes" })
	@Override
	public PluginInfo register() throws PluginManagerException {
		PluginInfo pli = new PluginInfo(
				(Class)this.getClass(),
				"PAS.dataset.revalidateProbeLists",
				new String[0],
				Constants.MC_DATASET,
				new HashMap<String, Object>(),
				"Günter Jäger",
				"jaeger@informatik.uni-tuebingen.de",
				"Revalidates all dynamic probe lists in the selected datasets",
				"Revalidate DynamicProbeLists"
		);
		pli.addCategory("Transform");
		return pli;
	}

	@Override
	public void init() {}
	
	private void revalidateDPLs(List<DataSet> datasets) {
		for(DataSet ds : datasets) {
			List<ProbeList> pls = ds.getProbeListManager().getProbeLists();
			for(ProbeList pl : pls) {
				if(pl instanceof DynamicProbeList) {
					DynamicProbeList dpl = (DynamicProbeList)pl;
					if(dpl.getNumberOfProbes() == 0) {
						DynamicProbeList clonedDpl = new DynamicProbeList(dpl.getDataSet(), "Temporary clone of \""+dpl.getName()+"\"");
						clonedDpl.getRuleSet().fromStorageNode(dpl.getRuleSet().toStorageNode());
						
						dpl.getRuleSet().clear();
						dpl.setIgnoreChanges(false);
						dpl.getRuleSet().fromStorageNode(clonedDpl.getRuleSet().toStorageNode());
						
						clonedDpl.propagateClosing();
						clonedDpl = null;
					}
				}
			}
		}
	}

	@Override
	public List<DataSet> run(List<DataSet> datasets) {
		revalidateDPLs(datasets);
		return null;
	}
}
