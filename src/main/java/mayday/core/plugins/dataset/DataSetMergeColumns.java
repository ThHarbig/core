package mayday.core.plugins.dataset;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;

import mayday.core.ClassSelectionModel;
import mayday.core.DataSet;
import mayday.core.Probe;
import mayday.core.gui.classes.ClassSelectionDialog;
import mayday.core.math.Statistics;
import mayday.core.pluma.AbstractPlugin;
import mayday.core.pluma.Constants;
import mayday.core.pluma.PluginInfo;
import mayday.core.pluma.PluginManagerException;
import mayday.core.pluma.prototypes.DatasetPlugin;
import mayday.core.tasks.AbstractTask;

public class DataSetMergeColumns extends AbstractPlugin implements DatasetPlugin {

	public void init() {
	}

	@SuppressWarnings("unchecked")
	public PluginInfo register() throws PluginManagerException {
		PluginInfo pli = new PluginInfo(
				(Class)this.getClass(),
				"PAS.alignedDS.DatasetMergeColumns",
				new String[0],
				Constants.MC_DATASET,
				new HashMap<String, Object>(),
				"Florian Battke",
				"battke@informatik.uni-tuebingen.de",
				"Merges data set columns according to a class selection using the mean of experiments for each class",
				"Combine columns"
		);
		pli.addCategory("Merge & Split");
		return pli;
	}

	public List<DataSet> run(final List<DataSet> datasets) {
        
		
		
		CombineC atask = new CombineC(datasets);
		
		atask.start();
		
		atask.waitFor();
		
		return atask.getResult();
	}

	public static class CombineC extends AbstractTask {

		private List< DataSet > datasets;
		private List< DataSet > resultsets = new LinkedList<DataSet>();
		
		public CombineC( List<DataSet> datasets2 ) {
			super("Combining Columns");
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
			
			DataSet ds = new DataSet(d1.getName()+" - combined columns");
			resultsets.add(ds);			
			ds.getMasterTable().setNumberOfExperiments(csm.getNumClasses());
			int ex=0;
			for (String cn : csm.getClassNames()) {
				ds.getMasterTable().setExperimentName(ex++, cn);
			}
			
			for (Probe pb : d1.getMasterTable().getProbes().values()) {
				Probe pneu = new Probe(ds.getMasterTable());
				pneu.setName(pb.getName());
				double[] vals = new double[csm.getNumClasses()];
				int delta=0;
				for (String cn : csm.getClassNames()) {
					List<Integer> li = csm.toIndexList(cn);
					vals[delta++] = getMean(pb.getValues(), li);
				}
				pneu.setValues(vals);
				ds.getMasterTable().addProbe(pneu);
			}
			
			// we could also clone probe lists etc. 
		}
		
		protected static double getMean(double[] arr, List<Integer> idx) {
			ArrayList<Double> ld = new ArrayList<Double>();
			for (int i : idx)
				ld.add(arr[i]);
			return Statistics.mean(ld);
		}
		
		protected void initialize() {
		}
		
		public List<DataSet> getResult() {
			return resultsets;
		}

	}

	
}
