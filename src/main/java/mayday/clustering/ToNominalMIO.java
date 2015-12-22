package mayday.clustering;

import java.util.HashMap;
import java.util.List;
import java.util.Set;

import mayday.core.DataSet;
import mayday.core.Probe;
import mayday.core.ProbeList;
import mayday.core.io.gude.GUDEConstants;
import mayday.core.io.gude.prototypes.ProbelistExportPlugin;
import mayday.core.meta.MIGroup;
import mayday.core.meta.types.StringMIO;
import mayday.core.pluma.AbstractPlugin;
import mayday.core.pluma.Constants;
import mayday.core.pluma.PluginInfo;
import mayday.core.pluma.PluginManagerException;
import mayday.core.tasks.AbstractTask;

public class ToNominalMIO extends AbstractPlugin implements ProbelistExportPlugin {

	public void init() {
	}

	@SuppressWarnings("unchecked")
	public PluginInfo register() throws PluginManagerException {
		PluginInfo pli = new PluginInfo(
				(Class)this.getClass(),
				"PAS.clustering.toNominalMIO",
				new String[0],
				Constants.MC_PROBELIST_EXPORT,
				new HashMap<String, Object>(),
				"Florian Battke",
				"battke@informatik.uni-tuebingen.de",
				"Converts a partitioning clustering to a nominal mio group",
				"to nominal MIO"
				);
		pli.getProperties().put(GUDEConstants.EXPORTER_TYPE, GUDEConstants.EXPORTERTYPE_OTHER);
		return pli;
	}

	public void run(final List<ProbeList> probelists) {
		if (probelists.size()==0)
			return;
		
		AbstractTask at = new AbstractTask("to nominal MIO") {

			protected void doWork() throws Exception {
				// 1 - check for violation of partition
				for (int i=1; i!=probelists.size(); ++i) {
					for (int j=0; j!=i; ++j) {
						Set<Probe> h = probelists.get(i).getAllProbes();
						h.retainAll(probelists.get(j).getAllProbes());
						if (h.size()>0) 
							throw new RuntimeException("ProbeLists overlap --> this is no partition!");
					}				
				}
				
				// 2 - create migroup
				String groupName = probelists.get(0).getName();
				int pCount=probelists.get(0).getNumberOfProbes();
				for (int i=1; i!=probelists.size(); ++i) {
					groupName+=", "+probelists.get(i).getName();
					pCount +=probelists.get(i).getNumberOfProbes();
				}

				if (groupName.length()>30)
					groupName = groupName.substring(0,30)+"...";

				
				DataSet ds = probelists.get(0).getDataSet();
				MIGroup mg = ds.getMIManager().newGroup(
						"PAS.MIO.String", 
						groupName, 
						"/Partitions");

				int i=0;
				
				// 3 - create mios
				for (ProbeList pl : probelists) {
					String nextName = pl.getName();		

					StringMIO mio = new StringMIO(nextName);
					for (Object opb : pl.toArray()) {
						mg.add(opb, mio);
						setProgress((10000*i)/pCount);
						++i;
					}
				}
			}

			protected void initialize() {
			}
			
		};
		at.start();
		
	}



}
