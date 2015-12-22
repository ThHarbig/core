package mayday.clustering;

import java.util.HashMap;
import java.util.List;

import mayday.core.DataSet;
import mayday.core.ProbeList;
import mayday.core.io.gude.GUDEConstants;
import mayday.core.io.gude.prototypes.ProbelistExportPlugin;
import mayday.core.meta.MIGroup;
import mayday.core.meta.types.StringListMIO;
import mayday.core.pluma.AbstractPlugin;
import mayday.core.pluma.Constants;
import mayday.core.pluma.PluginInfo;
import mayday.core.pluma.PluginManagerException;
import mayday.core.tasks.AbstractTask;

public class ToStringListMIO extends AbstractPlugin implements ProbelistExportPlugin {

	public void init() {
	}

	@SuppressWarnings("unchecked")
	public PluginInfo register() throws PluginManagerException {
		PluginInfo pli = new PluginInfo(
				(Class)this.getClass(),
				"PAS.clustering.toStringListMIO",
				new String[0],
				Constants.MC_PROBELIST_EXPORT,
				new HashMap<String, Object>(),
				"Florian Battke",
				"battke@informatik.uni-tuebingen.de",
				"Converts a probelist selection to a string list mio, labelling each probe with its probelists",
				"to string list MIO"
				);
		pli.getProperties().put(GUDEConstants.EXPORTER_TYPE, GUDEConstants.EXPORTERTYPE_OTHER);
		return pli;
	}

	public void run(final List<ProbeList> probelists) {
		if (probelists.size()==0)
			return;
		
		AbstractTask at = new AbstractTask("to String List MIO") {

			protected void doWork() throws Exception {

				// 1 - create migroup
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
						"PAS.MIO.StringList", 
						groupName ,
						"/Membership");

				int i=0;
				
				// 2 - create mios
				for (ProbeList pl : probelists) {
					String nextName = pl.getName();		
					
					for (Object opb : pl.toArray()) {
						StringListMIO mio = (StringListMIO)mg.getMIO(opb);
						if (mio==null) {
							mio = (StringListMIO)mg.add(opb);
						} 
						mio.getValue().add(nextName);
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
