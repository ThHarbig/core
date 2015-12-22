package mayday.core.plugins.dataset;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;

import mayday.core.DataSet;
import mayday.core.Experiment;
import mayday.core.Probe;
import mayday.core.pluma.AbstractPlugin;
import mayday.core.pluma.Constants;
import mayday.core.pluma.PluginInfo;
import mayday.core.pluma.PluginManagerException;
import mayday.core.pluma.prototypes.DatasetPlugin;
import mayday.core.settings.SettingDialog;
import mayday.core.settings.generic.HierarchicalSetting;
import mayday.core.settings.typed.BooleanSetting;
import mayday.core.settings.typed.ExperimentSetting;
import mayday.core.settings.typed.StringSetting;
import mayday.core.tasks.AbstractTask;

public class DataSetReferenceColumn extends AbstractPlugin implements DatasetPlugin {

	public void init() {
	}

	public PluginInfo register() throws PluginManagerException {
		PluginInfo pli = new PluginInfo(
				this.getClass(),
				"CEGAT.dataset.ReferenceColumn",
				new String[0],
				Constants.MC_DATASET,
				new HashMap<String, Object>(),
				"Florian Battke",
				"florian.battke@cegat.de",
				"Computes relative expression of each column relative to one reference column",
				"Compute relative expression (fold-change)"
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
			super("Computing Fold-Changes");
			this.datasets=datasets2;
		}

		protected void doWork() throws Exception {
		
			if (datasets.size()!=1)
				throw new RuntimeException("This plugin can only work on one dataset at a time.");		

			DataSet d1 = datasets.get(0);
			
			ExperimentSetting es = new ExperimentSetting("Reference column","The column containing reference measurements",d1.getMasterTable().getExperiment(0));
			BooleanSetting bs = new BooleanSetting("Use log transformation", null, true);
			StringSetting ns = new StringSetting("New Dataset name",null,d1.getName()+" - FC");
			HierarchicalSetting hs = new HierarchicalSetting("DataSet FC computation").addSetting(ns).addSetting(bs).addSetting(es);
			
			SettingDialog sd = new SettingDialog(null, "DataSet FC transformation", hs);
			sd.showAsInputDialog();

			
			if (sd.closedWithOK()) {
				boolean logged = bs.getBooleanValue();
				Experiment exx = es.getExperiment();
				int exxi = exx.getIndex();
				

				DataSet ds = new DataSet(ns.getStringValue());
				resultsets.add(ds);			
				ds.getMasterTable().setNumberOfExperiments(d1.getMasterTable().getNumberOfExperiments()-1);
				int ex=0;
				for (Experiment e : d1.getMasterTable().getExperiments()) {
					if (exx!=e)
						ds.getMasterTable().setExperimentName(ex++, e.getName());
				}

				for (Probe pb : d1.getMasterTable().getProbes().values()) {
					Probe pneu = new Probe(ds.getMasterTable());
					pneu.setName(pb.getName());
					double[] vals = new double[ds.getMasterTable().getNumberOfExperiments()];
					int delta=0;
					int delta2=0;
					for (Experiment e : d1.getMasterTable().getExperiments()) {
						if (es.getExperiment()!=e) {
							vals[delta++]=logged?
									Math.log(pb.getValue(delta2))/Math.log(2.0)-Math.log(pb.getValue(exxi))/Math.log(2.0)
							: 
								pb.getValue(delta2)/pb.getValue(exxi);
						}
						delta2++;
					}
					pneu.setValues(vals);
					ds.getMasterTable().addProbe(pneu);
				}
			}
			
			// we could also clone probe lists etc. 
		}
		
		protected void initialize() {
		}
		
		public List<DataSet> getResult() {
			return resultsets;
		}

	}

	
}
