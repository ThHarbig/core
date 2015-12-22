package mayday.genemining2;

import java.util.LinkedList;
import java.util.List;

import mayday.core.ClassSelectionModel;
import mayday.core.MasterTable;
import mayday.core.math.scoring.ScoringPlugin;
import mayday.core.math.scoring.ScoringResult;
import mayday.core.math.scoring.TestPlugin;
import mayday.core.math.stattest.StatTestPlugin;
import mayday.core.settings.generic.HierarchicalSetting;
import mayday.core.settings.generic.PluginInstanceSetting;
import mayday.core.settings.generic.PluginMultiselectListSetting;
import mayday.core.settings.typed.BooleanSetting;
import mayday.core.settings.typed.ClassSelectionSetting;
import mayday.core.settings.typed.DoubleSetting;
import mayday.core.settings.typed.IntSetting;
import mayday.core.settings.typed.RestrictedStringSetting;
import mayday.core.settings.typed.StringSetting;

/**
 * @author G\u00FCnter J\u00E4ger
 *
 */
public class GeneminingSettings extends HierarchicalSetting {

	protected PluginMultiselectListSetting<TestPlugin<ScoringResult>> multiMethods;
	protected PluginInstanceSetting<TestPlugin<ScoringResult>> singleMethods;
 	protected ClassSelectionSetting classes;
	protected IntSetting numGenes;
	protected DoubleSetting pValThreshold;
	protected RestrictedStringSetting permTest;
	protected BooleanSetting crossVal;
	protected StringSetting projectName;
	protected IntSetting compareNumberOfGenes;
	
	private MasterTable masterTable;
	private boolean cng;
	
	protected final static String PERM_NO="disable", PERM_FAST="use heuristic method", PERM_FULL="use full permutation test";
	
	/**
	 * @param Name
	 * @param probeLists 
	 * @param masterTable 
	 * @param classSelectionModel 
	 * @param doCompareNumberOfGenes 
	 * @param hierarchical
	 */
	public GeneminingSettings(String Name, MasterTable masterTable, ClassSelectionModel classSelectionModel, boolean doCompareNumberOfGenes) {
		super(Name);
		
		this.masterTable = masterTable;
		this.cng = doCompareNumberOfGenes;
		
		
		
		setLayoutStyle(HierarchicalSetting.LayoutStyle.PANEL_VERTICAL)
		.addSetting(projectName = new StringSetting("Project Name", "Title of the genemining project, used for the resulting meta information groups.", "Gene Mining"));
		

		if(classSelectionModel == null) {
			classSelectionModel = new ClassSelectionModel(masterTable);
		}
		
		if(doCompareNumberOfGenes) {
			addSetting(classes = new ClassSelectionSetting("Classes",
							null,
							classSelectionModel, 2, 2, masterTable.getDataSet()));
		} else {
			addSetting(classes = new ClassSelectionSetting("Classes",
					null,
					classSelectionModel, 2,
					Math.max(2,masterTable.getNumberOfExperiments() / 2), masterTable.getDataSet()));
		}
		
		if(doCompareNumberOfGenes) {
			addSetting(singleMethods = new PluginInstanceSetting<TestPlugin<ScoringResult>>("Feature Selection Methods", 
					"Select the methods that should be applied to identify significant genes.", 
					ScoringPlugin.MC));
		} else {
			addSetting(multiMethods = new PluginMultiselectListSetting<TestPlugin<ScoringResult>>(
					"Feature Selection Methods",
					"Select the methods that should be applied to identify significant genes.",
					new String[]{ScoringPlugin.MC, StatTestPlugin.MC}));
		}
		
		addSetting(
				permTest = new RestrictedStringSetting(
						"Compute significance",
						"Significance (p-) values can be estimated by a full permutation test (which is slow)\n" +
						"or by a heuristic method. The heuristic method tries to estimate significance with a\n" +
						"smaller number of permutations. It is much faster, but may be less precise.",
						0, new String[]{PERM_NO, PERM_FAST, PERM_FULL}).setLayoutStyle(RestrictedStringSetting.LayoutStyle.RADIOBUTTONS));

		if(doCompareNumberOfGenes) {
			addSetting(compareNumberOfGenes = new IntSetting("Minimal number of genes", null, 10));
		};

		addSetting(
				numGenes = new IntSetting("Maximal number of genes", null, 100));
		
		if (!doCompareNumberOfGenes) {
			addSetting(pValThreshold = new DoubleSetting("p-Value threshold",
					"Only output genes with p<threshold.\n" +
					"This has no effect if significance computation is disabled.\n" +
					"It also doesn't affect the consensus probelist.",0.05));
			
			addSetting(crossVal = new BooleanSetting("Cross-Validation",
					"Compare the applied methods by calculating cumulative rank of common genes."
							+ "\nAdditionally, provide a consensus probe list of significant genes.",
					true));
		}
	}
	
	/**
	 * @return ClassSelectionModel
	 */
	public ClassSelectionModel getClassSelectionModel() {
		return this.classes.getModel();
	}
	
	/**
	 * @return true, if full permutation test should be performed, else false
	 */
	public boolean performPermTest() {
		return this.permTest.getStringValue()==PERM_FULL;
	}
	
	/**
	 * @return true, if fast permutation test should be performed, else false
	 */
	public boolean performPermTestHeuristic() {
		return permTest.getStringValue()==PERM_FAST;
	}
	
	/**
	 * @return true, if cross validation should be performed, else false
	 */
	public boolean performCrossValidation() {
		if(this.crossVal == null) return false;
		return this.crossVal.getBooleanValue();
	}
	
	/**
	 * @return number of genes for output
	 */
	public int getNumberOfGenes() {
		return numGenes.getIntValue();
	}
	
	public double getMaxPValue() {
		return pValThreshold.getDoubleValue();
	}
	
	/**
	 * @return methods, that should be applied
	 */
	public List<TestPlugin<ScoringResult>> getSelectedMethods() {
		if(this.multiMethods != null) {
			return this.multiMethods.getSelectedPlugins();
		}
		List<TestPlugin<ScoringResult>> list = new LinkedList<TestPlugin<ScoringResult>>();
		list.add(this.singleMethods.getInstance());
		return list;
	}
	
	/**
	 * @return name of the genemining project
	 */
	public String getProjectName() {
		return this.projectName.getStringValue();
	}
	
	/**
	 * @param numGenes
	 */
	public void setNumberOfGenes(int numGenes) {
		this.numGenes.setIntValue(numGenes);
	}
	
	/**
	 * @return MasterTable
	 */
	public MasterTable getMasterTable() {
		return this.masterTable;
	}
	
	/**
	 * @return minimal number of genes to compare
	 */
	public int getCNG() {
		if(this.compareNumberOfGenes == null) return 1;
		return this.compareNumberOfGenes.getIntValue();
	}
	
	public GeneminingSettings clone() {
		GeneminingSettings gms = new GeneminingSettings(this.getName(), masterTable, classes.getModel(), cng);
		gms.fromPrefNode(this.toPrefNode());
		return gms;
	}
}
