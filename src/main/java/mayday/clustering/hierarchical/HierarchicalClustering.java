/*
 *  Created on Aug 27, 2004
 *
 */
package mayday.clustering.hierarchical;

import java.util.List;

import mayday.clustering.ClusterPlugin;
import mayday.core.MasterTable;
import mayday.core.ProbeList;
import mayday.core.math.distance.DistanceMeasurePlugin;
import mayday.core.structures.linalg.matrix.PermutableMatrix;
import mayday.core.structures.trees.tree.Node;

/**
 * Hierarchical Cluster Plugin<p>
 * 
 * 
 * @author Markus Riester
 * @version 0.4
 */
public class HierarchicalClustering
{

	public enum CLUSTER_METHOD {
    	UPGMA("UPGMA"),
    	WPGMA("WPGMA"),
    	SL("Single Linkage"),
    	CL("Complete Linkage"),
    	RNJ("Rapid Neighbor Joining")
    	;
    	
    	CLUSTER_METHOD( String name ) {
    		Name = name;
    	}
    	
    	public boolean isPALMethod() {
    		return this.ordinal()<RNJ.ordinal();
    	}

    	private String Name;
    	public String toString() {
    		return Name;
    	}
    }
	
    private HierarchicalClusterSettings l_settings;
	private PermutableMatrix l_matrix;
	private DistanceMeasurePlugin dm;
	
    /**
     * 
     */
    public HierarchicalClustering() {
        super();
    }
    
    // TL 2007-11-29
    public void runWithSettings(List<ProbeList> probeLists, MasterTable masterTable,
    		HierarchicalClusterSettings settings) {
    	this.l_settings = settings;
    	this.constructDistanceMeasureObject(probeLists, masterTable);
    	this.runClusteringViewer(probeLists, masterTable);
    }
    
    // TL 2007-11-29
    public Node getTreeOutOfSettings(List<ProbeList> probeLists, MasterTable masterTable,
    		HierarchicalClusterSettings settings) {
    	this.l_settings = settings;
    	constructDistanceMeasureObject(probeLists, masterTable);
    	HierarchicalClusteringPAL pal = new HierarchicalClusteringPAL(l_matrix, dm, this.l_settings);
    	long t1 = System.currentTimeMillis();
    	Node n = pal.clusterTree();
    	long t2 = System.currentTimeMillis();
		System.out.println("Clustering finished in "+(t2-t1) + " ms");
		return n;
    }
    
    private void runClusteringViewer(List<ProbeList> probeLists, MasterTable masterTable) {    	
    	new HierarchicalClusteringPlugin().runWithSettings(probeLists, masterTable, l_settings);
    }
    
    private void constructDistanceMeasureObject(List<ProbeList> probeLists, MasterTable masterTable) {
//    	AbstractTask at = new AbstractTask()
        /*
         * construct the distance measure object
         */     	
        l_matrix = ClusterPlugin.getClusterData(probeLists, masterTable);
        
		// TL
        if ( this.l_settings.isMatrixTransposed() ) {
        	l_matrix.transpose();
        }
        // end TL
          
        dm = this.l_settings.getDistanceMeasure();
  		
    }
    
    
}
