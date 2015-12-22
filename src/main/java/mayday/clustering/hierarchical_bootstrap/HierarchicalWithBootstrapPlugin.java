package mayday.clustering.hierarchical_bootstrap;

import java.awt.Component;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Random;

import mayday.clustering.ClusterPlugin;
import mayday.clustering.hierarchical.HierarchicalClusteringPAL;
import mayday.clustering.hierarchical.TreeInfo;
import mayday.clustering.hierarchical.TreeMIO;
import mayday.core.MasterTable;
import mayday.core.ProbeList;
import mayday.core.math.distance.DistanceMeasurePlugin;
import mayday.core.meta.MIGroup;
import mayday.core.meta.MIGroupSelection;
import mayday.core.meta.MIType;
import mayday.core.pluma.Constants;
import mayday.core.pluma.PluginInfo;
import mayday.core.pluma.PluginManager;
import mayday.core.pluma.PluginManagerException;
import mayday.core.pluma.prototypes.ProbelistPlugin;
import mayday.core.settings.SettingsDialog;
import mayday.core.structures.linalg.matrix.DoubleMatrix;
import mayday.core.structures.linalg.matrix.PermutableMatrix;
import mayday.core.structures.trees.tree.Edge;
import mayday.core.structures.trees.tree.Node;
import mayday.core.tasks.AbstractTask;
import mayday.vis3.PlotPlugin;
import mayday.vis3.gui.PlotWindow;
import mayday.vis3.model.Visualizer;

public class HierarchicalWithBootstrapPlugin extends ClusterPlugin implements ProbelistPlugin {

	@Override
	public void init() {
	}

	public PluginInfo register() throws PluginManagerException {
		PluginInfo pli= new PluginInfo(
				this.getClass(),
				"PAS.clustering.hierarchical.bootstrap",
				new String[]{"PAS.clustering.hierarchical"}, 
				Constants.MC_PROBELIST,
				(HashMap<String,Object>)null,
				"Florian Battke",
				"battke@informatik.uni-tuebingen.de",
				"Hierarchical clustering with bootstrap analysis",
				"Hierarchical (with bootstrapping)");
		pli.addCategory(CATEGORY);
		return pli;
	}

	public List<ProbeList> run(List<ProbeList> probeLists, MasterTable masterTable) {

		// Show Dialog to let user select tree settings
		HierarchicalClusterWithBootstrapSettings settings = new HierarchicalClusterWithBootstrapSettings();
		SettingsDialog sd = new SettingsDialog(null, "Hierarchical Clustering", settings);
		sd.showAsInputDialog();
		if (!sd.closedWithOK()) 
			return null;
		
		return runWithSettings(probeLists, masterTable, settings);					
	}

	public List<ProbeList> runWithSettings(List<ProbeList> probeLists,
			MasterTable masterTable, final HierarchicalClusterWithBootstrapSettings settings) {

		final DistanceMeasurePlugin dm = settings.getDistanceMeasure();
		
    	final PermutableMatrix originalMatrix = constructDistanceMeasureObject(probeLists, settings.isMatrixTransposed(), masterTable);
    	HierarchicalClusteringPAL pal = new HierarchicalClusteringPAL(originalMatrix, dm, settings);
		final Node ct = pal.clusterTree();
		
		if (ct==null)
			return null;

		String name = "Hierarchical Clustering ("+(settings.isMatrixTransposed()?"transposed, ":"")+
				settings.getClustering_method()+", "+
				settings.getDistanceMeasure()+", "+
				settings.getNumberOfResamplings()+" resamplings)";
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
		
		if (settings.getNumberOfResamplings()>0) {
		// AND NOW WE START THE Bootstrapping process
			AbstractTask bootStrapper = new AbstractTask("Bootstrapping") {

				protected void initialize() { }

				@Override
				protected void doWork() throws Exception {
					int NUMBER_OF_RESAMPLINGS=settings.getNumberOfResamplings();
					Random r = new Random();

					HashSet<String> allOriginalTaxa = new HashSet<String>();
					for (Node n : ct.getLeaves(null))
						allOriginalTaxa.add(n.getLabel()); 

					//				System.out.println("Splits in original tree");

					HashMap<CountingSplit, CountingSplit> originalSplits = new HashMap<CountingSplit, CountingSplit>();
					for (Edge e : ct.postorderEdgeList()) {
						CountingSplit cs = new CountingSplit(ct, e, allOriginalTaxa);
						if (!cs.isLeafSplit())	// ignore leaf splits because they will always get 100%
							originalSplits.put(cs,cs);
						//					System.out.println(cs);
					}

					//				System.out.println("Starting resampling");

					if (settings.isMatrixTransposed()) //untranspose matrix
						originalMatrix.transpose();

					setProgress(0);

					for (int i=0; i!=NUMBER_OF_RESAMPLINGS; ++i) {
						// create a matrix by pseudo-sampling -- each column is an element to cluster, each row contains a vector of measurements
						DoubleMatrix permMatrix = originalMatrix.deepClone();
						permMatrix.pseudoSampleRows(r);

						//					System.out.println("RESAMPLE "+i);

						if (settings.isMatrixTransposed())
							permMatrix.transpose();

						// now cluster the new matrix
						HierarchicalClusteringPAL pal2 = new HierarchicalClusteringPAL(permMatrix, dm, settings);			    				    
						Node ct2 = pal2.clusterTree();			

						List<Edge> edges = ct2.postorderEdgeList();

						//					System.out.println("Splits in resampling tree");

						for (Edge e : edges) {
							//						System.out.println("Edge: "+e);
							ResamplingSplit bp = new ResamplingSplit(ct2, e,allOriginalTaxa); 						
							CountingSplit ics = originalSplits.get(bp);
							//						System.out.println("Split: "+bp);
							if (ics!=null) {
								//							System.out.println("- Matched:   "+ics);
								ics.increase();
							} else {
								//							System.out.println("- Unmatched.");
							}
						}			
						setProgress((10000/NUMBER_OF_RESAMPLINGS)*i);
					}

					//				System.out.println("Assigning weights:");

					boolean overridelengths = settings.getOverrideLengths();
					
					for (CountingSplit cs : originalSplits.keySet()) {
						double support = cs.getCount();
						double perc = (double)support/(double)NUMBER_OF_RESAMPLINGS;
						//					System.out.println(perc+" due to "+cs);
						double bs = ((int)(perc*100*NUMBER_OF_RESAMPLINGS) / (double)NUMBER_OF_RESAMPLINGS ) ;
						String lbl = Double.toString(bs);
						if (overridelengths)
							cs.getEdge().setLength(bs);
						else
							cs.getEdge().setLabel(lbl);
					}
					
					if (overridelengths) {
						for (Edge e : ct.postorderEdgeList()) {
							if (e.getNode(0).isLeaf() || e.getNode(1).isLeaf())
								e.setLength(100);
						}
					}
				}

			};
			bootStrapper.start();
			bootStrapper.waitFor();
		}

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

    private PermutableMatrix constructDistanceMeasureObject(List<ProbeList> probeLists, boolean transpose, MasterTable masterTable) {

    	PermutableMatrix l_matrix = ClusterPlugin.getClusterData(probeLists, masterTable);
        if ( transpose ) 
        	l_matrix.transpose();
        
        return l_matrix;
  		
    }
    
    public static class ResamplingSplit {
    	
    	private Edge e;
    	protected HashSet<String> s1, s2;
    	
    	public ResamplingSplit(Node tree, Edge splitEdge, HashSet<String> allOriginalTaxaInResampledTree) {
    		
    		this.e=splitEdge;
    		Node partitioner = splitEdge.getNode(1);

    		s1 = new HashSet<String>(allOriginalTaxaInResampledTree);
    		s2 = new HashSet<String>();
    		
    		Collection<Node> nn = partitioner.getLeaves(splitEdge);
    		
    		for (Node n : nn) {
    			String taxon = n.getLabel();
   				s2.add(taxon);
    		}
    		
    		s1.removeAll(s2);
    		
    		if (s1.size()<s2.size()) {
    			HashSet<String> tmp = s1;
    			s1 = s2;
    			s2 = tmp;
    		}
    			
    	}
    	
    	public Edge getEdge() {
    		return e;
    	}

    	public int hashCode() {
    		return s1.hashCode()+s2.hashCode();
    	}
    	
    	public boolean equals(Object rs2o) {
    		if (rs2o instanceof ResamplingSplit) {
    			ResamplingSplit rs2 = (ResamplingSplit)rs2o;
    			return (rs2.s1.equals(s1) && rs2.s2.equals(s2)) || (rs2.s1.equals(s2) && rs2.s2.equals(s1));	
    		}
    		return false;    		
    	}
    	
    	public String toString() {
    		return "("+s1.size()+":"+s2.size()+") "+s1+" ####### "+s2+"  {"+hashCode()+"}";
    	}
    	
    	public boolean isLeafSplit() {
    		return s2.size()==1;
    	}
    	
    }
    
    public interface ICountingSplit {
      	void increase();
    }
    
	public static class CountingSplit extends ResamplingSplit implements ICountingSplit{
    	private int count=0;
    	
    	public CountingSplit(Node tree, Edge splitEdge, HashSet<String> allOriginalTaxaInResampledTree) {
    		super(tree, splitEdge, allOriginalTaxaInResampledTree);
    	}
    	public void increase() {
    		count++;
//    		if (s1.size()==1 || s2.size()==1) {
//    			System.out.println();
//    		}
    	}
    	public int getCount() {
    		return count;
    	}
    	    	
    	public String toString() {
    		return "<"+count+"> "+super.toString();
    	}
    }
        
}
