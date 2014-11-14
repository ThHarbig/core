package mayday.core.plugins.dataset;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.TreeSet;

import mayday.core.DataSet;
import mayday.core.MasterTable;
import mayday.core.Probe;
import mayday.core.pluma.AbstractPlugin;
import mayday.core.pluma.Constants;
import mayday.core.pluma.PluginInfo;
import mayday.core.pluma.PluginManagerException;
import mayday.core.pluma.prototypes.DatasetPlugin;
import mayday.core.structures.maps.MultiHashMap;
import mayday.core.tasks.AbstractTask;

public class DatasetMergeCBind extends AbstractPlugin implements DatasetPlugin {

	public void init() {
	}

	@SuppressWarnings("unchecked")
	public PluginInfo register() throws PluginManagerException {
		PluginInfo pli = new PluginInfo(
				(Class)this.getClass(),
				"PAS.alignedDS.DatasetMergeC",
				new String[0],
				Constants.MC_DATASET,
				new HashMap<String, Object>(),
				"Florian Battke",
				"battke@informatik.uni-tuebingen.de",
				"Merges two datasets",
				"Merge column wise"
		);
		pli.addCategory("Merge & Split");
		return pli;
	}

	public List<DataSet> run(final List<DataSet> datasets) {
        
		MergeTaskCBind atask = new MergeTaskCBind(datasets);
		
		atask.start();
		
		atask.waitFor();
		
		return atask.getResult();
	}

	
	public static class MergeTaskCBind extends AbstractTask {

		private List< DataSet > datasets;
		private List< DataSet > resultsets = new LinkedList<DataSet>();
		
		public MergeTaskCBind( List<DataSet> datasets2 ) {
			super("Merging DataSets");
			this.datasets=datasets2;
		}

		protected void doWork() throws Exception {
		
			// 1 - create new dataset
			
			String name = datasets.get(0).getName();
			TreeSet<String> probes = new TreeSet<String>(datasets.get(0).getMasterTable().getProbes().keySet());
			MultiHashMap<String, Integer> clashing = new MultiHashMap<String, Integer>();
			for (String en : datasets.get(0).getMasterTable().getExperimentNames())
				clashing.put(en, 0);


			for(int i=1; i<datasets.size(); ++i) {
				name+=" + "+datasets.get(i).getName();
				probes.retainAll(datasets.get(i).getMasterTable().getProbes().keySet());
				for (String en : datasets.get(i).getMasterTable().getExperimentNames())
					clashing.put(en, i);
			}
			
			int noe = 0;
			for (DataSet ds : datasets) {
				noe+=ds.getMasterTable().getNumberOfExperiments();
			}
			
			DataSet ds = new DataSet(name);		
			ds.getMasterTable().setNumberOfExperiments(noe);
			resultsets.add(ds);
			
			MasterTable mata = ds.getMasterTable();
			for (String pname : probes) {
				Probe pbX = new Probe(mata);
				double[] newVals = new double[noe];
				int pos=0;
				for (DataSet dsx : datasets) {
					Probe pb = dsx.getMasterTable().getProbe(pname);
					int ne = pb.getValues().length;
					System.arraycopy(pb.getValues(), 0, newVals, pos, ne);
					pos+=ne;
				}
				pbX.setValues(newVals);
				pbX.setName(pname);
				mata.addProbe(pbX);
			}

			// set experiment names and check for experiment name clashes
			int pos=0;
			for (DataSet dsX : datasets) {
				for (String exname : dsX.getMasterTable().getExperimentNames()) {
					if (clashing.get(exname).size()>1)
						exname += " |"+dsX.getName();
					mata.setExperimentName(pos, exname);
					++pos;
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
