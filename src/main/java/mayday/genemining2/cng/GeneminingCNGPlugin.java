package mayday.genemining2.cng;

import java.util.List;

import javax.swing.JFrame;

import mayday.clustering.hierarchical.HierarchicalClusterSettings;
import mayday.core.ClassSelectionModel;
import mayday.core.MasterTable;
import mayday.core.ProbeList;
import mayday.core.math.scoring.ScoringResult;
import mayday.core.math.scoring.TestPlugin;
import mayday.core.math.stattest.StatTestResult;
import mayday.core.meta.MIGroup;
import mayday.core.meta.MIManager;
import mayday.core.pluma.PluginInfo;
import mayday.core.pluma.prototypes.ProbelistPlugin;
import mayday.core.probelistmanager.ProbeListManager;
import mayday.core.settings.Settings;
import mayday.core.settings.SettingsDialog;
import mayday.genemining2.Genemining;
import mayday.genemining2.GeneminingPlugin;
import mayday.genemining2.GeneminingSettings;

/**
 * @author G\u00FCnter J\u00E4ger
 *
 */
public class GeneminingCNGPlugin /*extends AbstractPlugin*/ implements ProbelistPlugin {
	private static GeneminingSettings gmSettings;
	private static MasterTable masterTable;
	private static List<ProbeList> probeLists;
	private static String[] classSelections;
	
//	@Override
//	public void init() {}
//
//	@SuppressWarnings("unchecked")
//	@Override
//	public PluginInfo register() throws PluginManagerException {
//		PluginInfo pi = new PluginInfo((Class) this.getClass(),
//				"PAS.geneminingCNG", new String[0], Constants.MC_PROBELIST,
//				(HashMap<String, Object>) null, "G\u00FCnter J\u00E4ger",
//				"jaeger@informatik.uni-tuebingen.de", "Gene Mining",
//				"Gene Mining - Compare Number Of Genes");
//		pi.addCategory(MaydayDefaults.Plugins.CATEGORY_DATAMINING);
//		return pi;
//	}
	
	/**
	 * @param probeLists
	 * @param masterTable
	 * @param classSelectionModel
	 * @param clusterSettings
	 * @return List<ProbeList>
	 */
	public List<ProbeList> run(List<ProbeList> probeLists, MasterTable masterTable, ClassSelectionModel classSelectionModel, 
			HierarchicalClusterSettings clusterSettings) {
		gmSettings = new GeneminingSettings("Genemining - Compare Number Of Genes", masterTable, classSelectionModel, true);
		GeneminingCNGPlugin.masterTable = masterTable;
		GeneminingCNGPlugin.probeLists = probeLists;
		
		Settings s = new Settings(gmSettings, PluginInfo
				.getPreferences("PAS.genemining2CNG"));
		SettingsDialog sd = new SettingsDialog(null, "Genemining - Compare Number Of Genes", s);
		sd.setModal(true);
		sd.setVisible(true);
		
		if (sd.canceled())
			return null;
		
		ScoringResult result = runSingleGenemining(gmSettings.getNumberOfGenes());
		
		if (result!=null) {
		
			CompareDialog compareDialog = new CompareDialog(gmSettings, clusterSettings);
			compareDialog.setCurrentScoringResult(result);
			compareDialog.setDefaultCloseOperation( JFrame.DISPOSE_ON_CLOSE );
			compareDialog.setVisible(true);
			compareDialog.run();
		}
        
		return null;
	}

	@Override
	public List<ProbeList> run(List<ProbeList> probeLists,
			MasterTable masterTable) {
		return this.run(probeLists, masterTable, null, null);
	}
	
	/**
	 * @param numberOfGenes
	 * @return ScoringResult
	 */
	public static ScoringResult runSingleGenemining(int numberOfGenes) {
		gmSettings.setNumberOfGenes(numberOfGenes);
		
		Genemining genemining = new Genemining();
		genemining.permTest = gmSettings.performPermTest();
		genemining.permTestHeuristic = gmSettings.performPermTestHeuristic();
		
		//no method selected, nothing to do!
		if(gmSettings.getSelectedMethods().size() == 0) {
			return null;
		}
		
		TestPlugin<ScoringResult> method = gmSettings.getSelectedMethods().get(0);
		genemining.registerMethod(method);
		
		genemining.run(probeLists, masterTable, gmSettings.getClassSelectionModel());
		
		if (genemining.getResults().size()==0)
			return null;
				
		List<ProbeList> topGenes = genemining.getTopGenes(masterTable, numberOfGenes, Double.POSITIVE_INFINITY);
		
		ProbeListManager probeListManager = (masterTable.getDataSet().getProbeListManager());
		
		for(ProbeList pl: topGenes) {
			probeListManager.addObjectAtTop(pl);
		}
		
		createMIOGroups(masterTable, genemining.getResults(), null, genemining.getClassIdentifier(), numberOfGenes);
		
		return genemining.getResults().get(0).get(0);
	}
	
	/**
	 * @param masterTable
	 * @param results
	 * @param consensus
	 * @param classIndices
	 */
	private static void createMIOGroups(MasterTable masterTable,
			List<List<ScoringResult>> results, ScoringResult consensus, List<String> classIndices, int numGenes) {
		MIManager mim = masterTable.getDataSet().getMIManager();
		
		if(classSelections == null) {
			classSelections = GeneminingPlugin.generateClassSelectionLabels(mim, classIndices, gmSettings.getProjectName());
		}
		
		List<TestPlugin<ScoringResult>> ls = gmSettings.getSelectedMethods();
		
		for (int i = 0; i < results.size(); i++) {
			String methodName = ls.get(i).toString();
			int j = 0;
			for (ScoringResult res : results.get(i)) {
				
				String path =  classSelections[j] +"/" ;
				
				String mName = methodName + " with " + numGenes + " genes";
				String methodPath = path + mName;;
				
				MIGroup rawScore = res.getRawScore();
				if (rawScore!=null) {
					mim.addGroup(rawScore, path);
					rawScore.setName( mName );
				} 
				
				if(res instanceof StatTestResult){
					mim.addGroup(((StatTestResult)res).getPValues(), methodPath);
				}

				for (MIGroup mg : res.getAdditionalValues()) {
					mim.addGroup(mg, methodPath);
				}
				j++;
			}
		}
	}
	
}
