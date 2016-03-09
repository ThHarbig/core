package mayday.core.plugins.dataset;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;

import jdk.nashorn.internal.scripts.JO;
import mayday.core.DataSet;
import mayday.core.Probe;
import mayday.core.math.average.IAverage;
import mayday.core.pluma.AbstractPlugin;
import mayday.core.pluma.Constants;
import mayday.core.pluma.PluginInfo;
import mayday.core.pluma.PluginManagerException;
import mayday.core.pluma.prototypes.DatasetPlugin;
import mayday.core.settings.SettingDialog;
import mayday.core.settings.generic.HierarchicalSetting;
import mayday.core.settings.typed.AveragingSetting;
import mayday.core.settings.typed.BooleanSetting;
import mayday.core.settings.typed.StringSetting;
import mayday.core.structures.maps.MultiHashMap;
import mayday.core.tasks.AbstractTask;

import javax.swing.*;

public class DataSetSummarizeOnDisplayName extends AbstractPlugin implements DatasetPlugin {

	public void init() {
	}

	@SuppressWarnings("unchecked")
	public PluginInfo register() throws PluginManagerException {
		PluginInfo pli = new PluginInfo(
				(Class)this.getClass(),
				"PAS.dataset.summarizeondisplayname",
				new String[0],
				Constants.MC_DATASET,
				new HashMap<String, Object>(),
				"Florian Battke",
				"battke@informatik.uni-tuebingen.de",
				"Merges data set rows according to common display names and a summarization method",
				"Summarize on Display Names"
		);
		pli.addCategory("Merge & Split");
		return pli;
	}

	public List<DataSet> run(final List<DataSet> datasets) {
		// setting for summarization method
		AveragingSetting as = new AveragingSetting();
		// setting for excluding ambiguous mappings
		BooleanSetting exclude = new BooleanSetting("Exclude Ambiguous Mappings?",
				null, true);
		// setting for splitting display names
		BooleanSetting split = new BooleanSetting("Split display names?",
				"", true);
		StringSetting separator = new StringSetting("Separator",
				"The string that separates entries with the display name",
				" /// ", true);

		// Pack all settings together
		HierarchicalSetting allOptions =  new HierarchicalSetting("Summarization");
		allOptions.addSetting(as)
				.addSetting(exclude)
				.addSetting(split)
				.addSetting(separator);

		// Display Settings
		SettingDialog sd = new SettingDialog(null, "Select summarization method",
				allOptions);
		if (sd.canceled()) {
			return null;
		}


		SummarizeC atask = new SummarizeC(datasets, as.getSummaryFunction(),
					split.getBooleanValue() ? separator.getStringValue() : null,
					exclude.getBooleanValue());
		// Run Summarization
		atask.start();
		atask.waitFor();
		return atask.getResult();
	}

	public static class SummarizeC extends AbstractTask {

		private List< DataSet > datasets;
		private List< DataSet > resultsets = new LinkedList<DataSet>();
		private IAverage summary;

		private String separator;
		private boolean exclusion;
		
		public SummarizeC( List<DataSet> datasets2, IAverage summary,
						   String separator, boolean exclusion) {
			super("Summarizing");
			this.summary = summary;
			this.datasets = datasets2;
			this.separator = separator;
			this.exclusion = exclusion;
		}

		protected void doWork() throws Exception {
	
			for (DataSet d1 : datasets) {
				
				MultiHashMap<String,Probe> byDisplayName = new MultiHashMap<String, Probe>();
				// map the split display names to the probe
				for (Probe pb : d1.getMasterTable().getProbes().values()) {
					String fullName = pb.getDisplayName(exclusion);
					if (fullName == null) {
						// ignore this probe
						continue;
					}
					if (separator == null) {
						// continue w/o splitting
						byDisplayName.put(fullName, pb);
					} else {
						// continue with splitting
						String[] names = fullName.split(separator);
						for (String n : names) {
							byDisplayName.put(n, pb);
						}
					}
				}
				DataSet ds;
				try {
					ds = new DataSet(d1.getName() + " - summarized (" + d1.getProbeDisplayNames().getName() + ", " + summary + ")");
				} catch (NullPointerException e) {
					JOptionPane.showMessageDialog(null, "You should set display names!");
					return;
				}
				resultsets.add(ds);			
				int noe = d1.getMasterTable().getNumberOfExperiments(); 
				ds.getMasterTable().setNumberOfExperiments(noe);
				ds.getMasterTable().setExperimentNames(d1.getMasterTable().getExperimentNames());

				
				for (String dName : byDisplayName.keySet()) {
					Probe pneu = new Probe(ds.getMasterTable());
					pneu.setName(dName);
					double[] values = new double[noe];
					List<Probe> cp = byDisplayName.get(dName);
					double[] vals = new double[cp.size()];
					for (int i=0; i!=values.length; ++i) {
						for (int j=0; j!=cp.size(); ++j)
							vals[j] = cp.get(j).getValue(i);
						values[i] = summary.getAverage(vals);
					}
					pneu.setValues(values);
					ds.getMasterTable().addProbe(pneu);
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
