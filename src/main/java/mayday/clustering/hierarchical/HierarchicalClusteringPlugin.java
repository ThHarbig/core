package mayday.clustering.hierarchical;

import java.awt.Component;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;

import mayday.clustering.ClusterPlugin;
import mayday.core.MasterTable;
import mayday.core.ProbeList;
import mayday.core.meta.MIGroup;
import mayday.core.meta.MIGroupSelection;
import mayday.core.meta.MIType;
import mayday.core.pluma.Constants;
import mayday.core.pluma.PluginInfo;
import mayday.core.pluma.PluginManager;
import mayday.core.pluma.PluginManagerException;
import mayday.core.pluma.prototypes.ProbelistPlugin;
import mayday.core.settings.SettingsDialog;
import mayday.core.structures.trees.tree.Node;
import mayday.vis3.PlotPlugin;
import mayday.vis3.gui.PlotWindow;
import mayday.vis3.model.Visualizer;

public class HierarchicalClusteringPlugin extends ClusterPlugin implements ProbelistPlugin {

	@Override
	public void init() {
	}

	@SuppressWarnings("unchecked")
	public PluginInfo register() throws PluginManagerException {
		//System.out.println("PL1: Register");		
		PluginInfo pli= new PluginInfo(
				(Class)this.getClass(),
				"PAS.clustering.hierarchical",
				new String[]{"LIB.JUNIT","PAS.genemining","PAS.clustering.TreeVisualizer2"}, 
				Constants.MC_PROBELIST,
				(HashMap<String,Object>)null,
				"Janko Dietzsch, Florian Battke, Günter Jäger and Kirsten Heitmann",
				"dietzsch@informatik.uni-tuebingen.de",
				"Hierarchical clustering algorithms are a common clustering technique in phylogeny. \n" + 
				"<br>References:<br><br>Cluster analysis and display of genome-wide expression patterns.<br> Michael B. Eisen, Paul T. Spellman, Patrick O. Brown, and David Botstein (1998)",
		"Hierarchical");
		pli.addCategory(CATEGORY);
		return pli;
	}

	public List<ProbeList> run(List<ProbeList> probeLists, MasterTable masterTable) {

		// Show Dialog to let user select tree settings
		HierarchicalClusterSettings settings = new HierarchicalClusterSettings();
		SettingsDialog sd = new SettingsDialog(null, "Hierarchical Clustering", settings);
		sd.showAsInputDialog();
		if (!sd.closedWithOK()) 
			return null;
		
		return runWithSettings(probeLists, masterTable, settings);					
	}

	public List<ProbeList> runWithSettings(List<ProbeList> probeLists,
			MasterTable masterTable, HierarchicalClusterSettings settings) {

		HierarchicalClustering treeGenerator = new HierarchicalClustering();
		Node ct = treeGenerator.getTreeOutOfSettings(probeLists, masterTable, settings);
		
		if (ct==null)
			return null;

		String name = "Hierarchical Clustering ("+(settings.isMatrixTransposed()?"transposed, ":"")+settings.getClustering_method()+", "+settings.getDistanceMeasure()+")";
		ProbeList pl = ProbeList.createUniqueProbeList(probeLists);
		pl.setSticky(true);		
		pl.setName(name);

		TreeMIO tm = new TreeMIO(new TreeInfo(ct,settings));

		MIGroupSelection<MIType> mgs = pl.getDataSet().getMIManager().getGroupsForName("Hierarchical Clustering");
		MIGroup mg = null;
		if (mgs.size()>0)
			mg = mgs.get(0);
		if (mg == null)
			mg = pl.getDataSet().getMIManager().newGroup(tm.getType(), "Hierarchical Clustering");
		mg.add(pl, tm);                	

		List<ProbeList> result = new LinkedList<ProbeList>();
		result.add(pl);

		// Open a Tree Visualizer on the new tree
		Visualizer vis = new Visualizer(masterTable.getDataSet(),result);
		Component pc = ((PlotPlugin)PluginManager.getInstance().getPluginFromID("PAS.clustering.TreeVisualizer2").newInstance()).getComponent();
		if (pc!=null) {
			PlotWindow pw = new PlotWindow(pc, vis);
			pw.setVisible(true);
			vis.addPlot(pw);
		}

		return result;
	}

}
