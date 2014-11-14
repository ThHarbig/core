package mayday.genemining2;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import mayday.core.ClassSelectionModel;
import mayday.core.MasterTable;
import mayday.core.MaydayDefaults;
import mayday.core.ProbeList;
import mayday.core.math.scoring.ScoringResult;
import mayday.core.math.scoring.TestPlugin;
import mayday.core.math.stattest.StatTestResult;
import mayday.core.meta.MIGroup;
import mayday.core.meta.MIManager;
import mayday.core.pluma.AbstractPlugin;
import mayday.core.pluma.Constants;
import mayday.core.pluma.PluginInfo;
import mayday.core.pluma.PluginManagerException;
import mayday.core.pluma.prototypes.ProbelistPlugin;
import mayday.core.settings.Settings;
import mayday.core.settings.SettingsDialog;
import mayday.core.tasks.AbstractTask;

/**
 * @author G\u00FCnter J\u00E4ger
 *
 */
public class GeneminingPlugin extends AbstractPlugin implements ProbelistPlugin {
	
	private GeneminingSettings statSetting;
	
	@Override
	public void init() {
		// nothing to do!
	}

	@Override
	public PluginInfo register() throws PluginManagerException {
		PluginInfo pi = new PluginInfo(this.getClass(),
				"PAS.genemining", new String[0], Constants.MC_PROBELIST,
				(HashMap<String, Object>) null, "G\u00FCnter J\u00E4ger",
				"jaeger@informatik.uni-tuebingen.de", "Gene Mining",
				"Gene Mining");
		pi.addCategory(MaydayDefaults.Plugins.CATEGORY_DATAMINING);
		return pi;
	}
	
	/**
	 * @param probeLists
	 * @param masterTable
	 * @param classSelectionModel
	 * @return List<ProbeList>
	 */
	public List<ProbeList> run(List<ProbeList> probeLists, final MasterTable masterTable, ClassSelectionModel classSelectionModel) {
		statSetting = new GeneminingSettings("Genemining", masterTable, classSelectionModel, false);

		Settings s = new Settings(statSetting, PluginInfo
				.getPreferences("PAS.genemining2"));
		SettingsDialog sd = new SettingsDialog(null, "Genemining", s);
		sd.setModal(true);
		sd.setVisible(true);

		if (sd.canceled())
			return null;

		final Genemining genemining = new Genemining();
		/*
		 * register selected methods
		 */
		for (TestPlugin<ScoringResult> stp : statSetting.getSelectedMethods()) {
			genemining.registerMethod(stp);
		}

		/*
		 * perform permutation test?
		 */
		genemining.permTest = statSetting.performPermTest();
		genemining.permTestHeuristic = statSetting.performPermTestHeuristic();

		genemining.run(probeLists, masterTable, statSetting.getClassSelectionModel());

		final List<List<ScoringResult>> results = genemining.getResults();
		
		if (results.size() == 0)
			return null;
		
		final List<ProbeList> topGenes = new ArrayList<ProbeList>();

		
		AbstractTask at = new AbstractTask("GeneMining") {
			protected void initialize() {
			}

			@Override
			protected void doWork() throws Exception {

				ScoringResult consensus = null;
				// With cross validation?
				if (statSetting.performCrossValidation()) {
					setProgress(-1, "Performing cross validation");
					consensus = genemining.getConsensus();
				}
				/*
				 * should be called before the creation of MIOGroups for the output,
				 * because probes with low rank are removed in this method, too!
				 */

				setProgress(-1, "Computing top genes");

				List<ProbeList> topGenes_ = genemining.getTopGenes(masterTable, statSetting.getNumberOfGenes(), statSetting.getMaxPValue());

				topGenes.addAll(topGenes_);
				/*
				 * create MIOGroups from results
				 */
				setProgress(-1, "Adding meta information");
				createMIOGroups(masterTable, results, consensus, genemining.getClassIdentifier());

			}

		};
		at.start();
		at.waitFor();	
		
		return topGenes;
	}

	@Override
	public List<ProbeList> run(List<ProbeList> probeLists,
			MasterTable masterTable) {
		return this.run(probeLists, masterTable, null);
	}

	/**
	 * @param masterTable
	 * @param results
	 * @param consensus
	 * @param classIndices
	 */
	public void createMIOGroups(MasterTable masterTable,
			List<List<ScoringResult>> results, ScoringResult consensus, List<String> classIndices) {
		MIManager mim = masterTable.getDataSet().getMIManager();
		String projName = statSetting.getProjectName();
		
		String[] classSelections = generateClassSelectionLabels(mim, classIndices, projName);
		
		List<TestPlugin<ScoringResult>> ls = statSetting.getSelectedMethods();
		
		for (int i = 0; i < results.size(); i++) {
			String methodName = ls.get(i).toString();
			int j = 0;
			for (ScoringResult res : results.get(i)) {
				
				String path =  classSelections[j] +"/" ;
				
				String mName = methodName;
//				String methodPath = path + mName;
				
				MIGroup rawScore = res.getRawScore();
				if (rawScore!=null) {
					mim.addGroup(rawScore, path);
					rawScore.setName( mName );
				} 
				
				if(res instanceof StatTestResult){
					if (rawScore==null) {
						MIGroup pval = ((StatTestResult)res).getPValues();
						mim.addGroup(pval, path);
						pval.setName(mName);						
						rawScore = pval; // use this group as parent for later groups
					}						
					else
						mim.addGroupBelow(((StatTestResult)res).getPValues(), rawScore);
				}

				for (MIGroup mg : res.getAdditionalValues()) {
					if (rawScore==null)
						mim.addGroup(mg, path+methodName+"/");
					else
						mim.addGroupBelow(mg, rawScore);
				}
				j++;
			}
		}
		
		if (consensus != null) {
			consensus.getRawScore().setName("Consensus Ranks");
			mim.addGroup(consensus.getRawScore(), projName);			
		}
	}

	public static String[] generateClassSelectionLabels(MIManager mim, List<String> classIndices, String projGroup) {
		String[] res = new String[classIndices.size()];
		for(int i = 0; i < classIndices.size(); i++) {
			res[i] = projGroup+"/"+classIndices.get(i);
		}
		return res;
	}
	
	/**
	 * @return GeneminingSettings
	 */
	public GeneminingSettings getSettings() {
		return statSetting;
	}
}
