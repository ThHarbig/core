package mayday.core.plugins.dataset;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;

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
		// get IAverage method
		AveragingSetting as = new AveragingSetting();
		SettingDialog sd = new SettingDialog(null, "Select summarization method", as);
		sd.showAsInputDialog();
		if (sd.canceled()) {
			// cancel
			return null;
		}

		// Get additional settings
		StringSetting regex; // split string
		BooleanSetting exclusion; // exclude unmapped entries?
		HierarchicalSetting additionalSettings = new HierarchicalSetting("Summary settings").
				setLayoutStyle(HierarchicalSetting.LayoutStyle.PANEL_VERTICAL).
				addSetting(regex = new StringSetting("Split regex",
						"A regular expression that describes the sub-string that " +
								"splits entries in your display name",
						" /// ", false)).
				addSetting(exclusion = new BooleanSetting("Exclude Unmapped",
						"Would you like to exclude Probe entries that did not match a new display name?",
						true));
		sd = new SettingDialog(null, "Additional Settings", additionalSettings);
		sd.showAsInputDialog();
		if(sd.canceled()) {
			return null;
		}
		// Run Summarization
		SummarizeC atask = new SummarizeC(datasets, as.getSummaryFunction(),
				regex.getStringValue(), exclusion.getBooleanValue());
		atask.start();
		atask.waitFor();
		return atask.getResult();
	}

	public static class SummarizeC extends AbstractTask {

		private List< DataSet > datasets;
		private List< DataSet > resultsets = new LinkedList<DataSet>();
		private IAverage summary;

		private String regex;
		private boolean exclusion;
		
		public SummarizeC( List<DataSet> datasets2, IAverage summary,
						   String regex, boolean exclusion) {
			super("Summarizing");
			this.summary = summary;
			this.datasets = datasets2;
			this.regex = regex;
			this.exclusion = exclusion;
		}

		protected void doWork() throws Exception {
	
			for (DataSet d1 : datasets) {
				
				MultiHashMap<String,Probe> byDisplayName = new MultiHashMap<String, Probe>();
				for (Probe pb : d1.getMasterTable().getProbes().values())
					byDisplayName.put(pb.getDisplayName(), pb);
				
				DataSet ds = new DataSet(d1.getName()+" - summarized ("+d1.getProbeDisplayNames().getName()+", "+summary+")");
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
