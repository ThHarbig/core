package mayday.core.plugins.dataset;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;

import mayday.core.ClassSelectionModel;
import mayday.core.DataSet;
import mayday.core.Probe;
import mayday.core.gui.classes.ClassSelectionDialog;
import mayday.core.pluma.AbstractPlugin;
import mayday.core.pluma.Constants;
import mayday.core.pluma.PluginInfo;
import mayday.core.pluma.PluginManagerException;
import mayday.core.pluma.prototypes.DatasetPlugin;
import mayday.core.tasks.AbstractTask;

public class DatasetSplitCBind extends AbstractPlugin implements DatasetPlugin {

	public void init() {
	}

	@SuppressWarnings("unchecked")
	public PluginInfo register() throws PluginManagerException {
		PluginInfo pli = new PluginInfo(
				(Class)this.getClass(),
				"PAS.alignedDS.DatasetSplitC",
				new String[0],
				Constants.MC_DATASET,
				new HashMap<String, Object>(),
				"Florian Battke",
				"battke@informatik.uni-tuebingen.de",
				"Splits one dataset into several new ones by a given experiment labelling",
				"Split column wise"
		);
		pli.addCategory("Merge & Split");
		return pli;
	}

	public List<DataSet> run(final List<DataSet> datasets) {
        
		SplitTaskCBind atask = new SplitTaskCBind(datasets);
		
		atask.start();
		
		atask.waitFor();
		
		return atask.getResult();
	}

	public static class SplitTaskCBind extends AbstractTask {

		private List< DataSet > datasets;
		private List< DataSet > resultsets = new LinkedList<DataSet>();
		
		public SplitTaskCBind( List<DataSet> datasets2 ) {
			super("Splitting DataSet");
			this.datasets=datasets2;
		}

		protected void doWork() throws Exception {
		
			if (datasets.size()!=1)
				throw new RuntimeException("This plugin can only split one dataset at a time.");		

			DataSet d1 = datasets.get(0);
			
			ClassSelectionModel csm = new ClassSelectionModel(d1.getMasterTable());
			ClassSelectionDialog csd = new ClassSelectionDialog(csm, d1);
			csd.setModal(true);
			csd.setVisible(true);
			
			for (String cn : csm.getClassNames()) {
				DataSet ds = new DataSet(cn);
				resultsets.add(ds);
				int cf = csm.getClassCount(cn);
				ds.getMasterTable().setNumberOfExperiments(cf);
				List<Integer> li = csm.toIndexList(cn);
				for (int i=0;i!=li.size();++i)
					ds.getMasterTable().setExperimentName(i, d1.getMasterTable().getExperimentName(li.get(i)));
				for (Probe pb : d1.getMasterTable().getProbes().values()) {
					Probe pbx = new Probe(ds.getMasterTable());
					pbx.setName(pb.getName());
					for (int i=0;i!=li.size();++i)
						pbx.setValue(pb.getValue(li.get(i)), i);
					ds.getMasterTable().addProbe(pbx);
				}
			}		
		}
		
		
		protected void initialize() {
		}
		
		public List<DataSet> getResult() {
			return resultsets;
		}

	}

	
}
