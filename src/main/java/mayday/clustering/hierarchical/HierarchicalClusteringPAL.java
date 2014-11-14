/*
 *  Created on Aug 27, 2004
 *
 */
package mayday.clustering.hierarchical;


import mayday.clustering.ClusterAlgorithms;
import mayday.clustering.hierarchical.HierarchicalClustering.CLUSTER_METHOD;
import mayday.clustering.hierarchical.pal.ClusterTree;
import mayday.clustering.hierarchical.pal.DistanceMatrix;
import mayday.clustering.hierarchical.pal.IdGenerator;
import mayday.clustering.hierarchical.pal.IdGroup;
import mayday.clustering.hierarchical.pal.Identifier;
import mayday.clustering.hierarchical.rapidnj.RapidNJ;
import mayday.core.math.distance.DistanceMeasurePlugin;
import mayday.core.structures.linalg.matrix.AbstractMatrix;
import mayday.core.structures.linalg.matrix.DoubleMatrix;
import mayday.core.structures.linalg.matrix.PermutableMatrix;
import mayday.core.structures.linalg.vector.AbstractVector;
import mayday.core.structures.trees.tree.Node;
import mayday.core.tasks.AbstractTask;

/**
 * The Hierarchical Cluster Algorithm, uses PAL
 * <p>
 * Can calculate a distance matrix or can use a distance matrix directly
 * <p>
 * Latest Changes: 2005/05/07 Progress Hint improved
 * 
 * @author Markus Riester
 * @version 0.6
 */
public class HierarchicalClusteringPAL extends ClusterAlgorithms {
    
    private DistanceMeasurePlugin d_measure;
    private boolean launchVisualizer = true;
    private Node clusterTree;
    private IdGroup id_grp;
    private PermutableMatrix distance_matrix;
    
    public static final int UPGMA = 0;
    public static final int WPGMA = 1;
    public static final int SINGLE_LINKAGE = 2;
    public static final int COMPLETE_LINKAGE = 3;
    public static final int NJ = 4;
    
    // TL
    private HierarchicalClusterSettings settings;
    // end TL
    
    /**
     * Computes the distance matrix of a given expression matrix
     * 
     * @param m
     *            the expression matrix of which the distance matrix is to be
     *            calculated.
     * @return the distance matrix
     */
    public DoubleMatrix computeDistanceMatrix(final AbstractMatrix m) {
    	
    	final DoubleMatrix[] res = new DoubleMatrix[1];
    	
    	AbstractTask at = new AbstractTask("Computing distances") {

			@Override
			protected void doWork() throws Exception {
		    	DoubleMatrix result = new DoubleMatrix(m.nrow(), m.nrow());
		    	int rows=m.nrow();
		        for (int i = 0; i != rows; ++i) {
		        	AbstractVector rowI = m.getRow(i);
		            for (int j = i; j != rows; ++j) {
		                if (i != j) {
		                    double d = d_measure
		                    .getDistance(rowI, m.getRow(j));
		                    result.setValue(i, j, d);
		                    result.setValue(j, i, d);
		                }
		            }
		            setProgress((i*10000)/rows);
		        }
		        res[0] = result;

			}

			@Override
			protected void initialize() {
			}
    		
    	};
    	at.start();
    	at.waitFor();
    	
    	return res[0];
    }
    
 
    
    /**
     * Constructes an HCL-object. It will calculate the distance matrix with the
     * specified distance measure
     * 
     * @param Data
     *            the data to be clustered.
     * @param dm
     *            the DistanceMeasure-object
     */
    // TL: added settings here
    public HierarchicalClusteringPAL(PermutableMatrix m, DistanceMeasurePlugin dm,
    		HierarchicalClusterSettings settings) {
        super(m);
        d_measure = dm;
        prepareData(false);
        this.settings = settings;
    }
    
    /**
     * Internal method that is called by all constructors
     * 
     * @param m
     *            The matrix to be clustered
     * @param isDistanceMatrix
     *            true if matrix is a distance matrix; otherwise false
     */
    private void prepareData(boolean isDistanceMatrix) {
        
        /*
         * first generate the taxanames
         */
        
        id_grp = IdGenerator.createIdGroup(ClusterData.nrow());
        for (int i = 0; i != ClusterData.nrow(); ++i) {
            Identifier id = new Identifier(ClusterData.getRowName(i));
            id_grp.setIdentifier(i, id);
         }
        
        AbstractMatrix m = ClusterData;        
        if (!isDistanceMatrix)
        	m = computeDistanceMatrix(m);
        
        if (m!=null) {
        	PermutableMatrix tmp = (PermutableMatrix)m.staticShallowClone();
        	tmp.transpose(); // we need row-major layout        	
        	distance_matrix =  tmp; //tmp.deepClone().getInternalData();
        } else {
        	distance_matrix = null;
        }
    }
    
    /**
     * Constructes an HCL object with the specified linkage method. The distance
     * method to be used is defined in the "linkage method"-object
     * 
     * @param Data
     * @param lm
     */
    public HierarchicalClusteringPAL(PermutableMatrix Data) {
        super(Data);
    }
    
  
    /**
     * @return Returns the cluster settings.
     */
    public HierarchicalClusterSettings getSettings() {
        return this.settings;
    }
    
    /**
     * @return The Dendrogram in Newick Format
     */
    public String getNewickString() {
        return clusterTree.toString();
    }
    
    /*
     * Sets the cluster method. Valid values are:
     * <p>
     * UPGMA (0), WPGMA (1), SINGLE_LINKAGE (2), COMPLETE_LINKAGE (3)
     * <p>
     * See: the constants of this class
     * 
     * @param c_method
     *            The cluster method to set.
     */
    @SuppressWarnings("unused")
	private void setSettings(HierarchicalClusterSettings settings) {
        this.settings = settings;
    }
    
    /**
     * @return Returns true if this plugin will launch the TreeVisualizer; false
     *         otherwise.
     */
    public boolean getLaunchVisualizer() {
        return launchVisualizer;
    }
    
    /**
     * @param True
     *            if this plugin will launch the TreeVisualizer, false
     *            otherwise.
     */
    public void setLaunchVisualizer(boolean launchVisualizer) {
        this.launchVisualizer = launchVisualizer;
    }
    
    public int[] runClustering() {
        // this is not called but still here to satisfy the interface
        return null;
    }
    
    
    public Node clusterTree() {    	
    	
    	if (distance_matrix == null)
    		return null; // cancelled during distance matrix computation
    	
    	final CLUSTER_METHOD method = settings.getClustering_method();    	
    	final Node[] result = new Node[1];
    	
    	AbstractTask at = new AbstractTask(method.toString()) {
			
			protected void initialize() {}
			
			@Override
			protected void doWork() throws Exception {
				if (method.isPALMethod()) {		    		
		    		DistanceMatrix d_matrix = new DistanceMatrix(distance_matrix.deepClone().getInternalData(), id_grp);
		    		switch (method) {
		    		case WPGMA: 
		    			result[0] = new ClusterTree(d_matrix, ClusterTree.WPGMA).getRoot();
		    			break;
		    		case SL:
		    			result[0] = new ClusterTree(d_matrix, ClusterTree.SINGLE_LINKAGE).getRoot();
		    			break;
		    		case CL:
		    			result[0] = new ClusterTree(d_matrix, ClusterTree.COMPLETE_LINKAGE).getRoot();
		    			break;
		    		case UPGMA:
		    			result[0] = new ClusterTree(d_matrix, ClusterTree.UPGMA).getRoot();
		    			break;
		    		}		    		
		    	} else {
		    		result[0] = RapidNJ.runRapidNJ(distance_matrix, id_grp, RapidNJ.SORTED_NJ);
		    	}
			}
		};
		
		at.start();
		at.waitFor();
		
		return result[0];
    	    	
    }
 
}
