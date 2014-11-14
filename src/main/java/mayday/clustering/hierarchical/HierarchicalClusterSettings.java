/*
 *  Created on Aug 27, 2004
 *
 */
package mayday.clustering.hierarchical;

import mayday.clustering.hierarchical.HierarchicalClustering.CLUSTER_METHOD;
import mayday.core.math.distance.DistanceMeasureManager;
import mayday.core.math.distance.DistanceMeasurePlugin;
import mayday.core.math.distance.measures.PearsonCorrelationDistance;
import mayday.core.pluma.PluginInfo;
import mayday.core.settings.Settings;
import mayday.core.settings.generic.HierarchicalSetting;
import mayday.core.settings.generic.ObjectSelectionSetting;
import mayday.core.settings.methods.DistanceMeasureSetting;
import mayday.core.settings.typed.BooleanSetting;
import mayday.genemining2.cng.Bipartition;

/**
 * The settings of the Hierarchical Cluster Plugin for MAYDAY
 * 
 * @author Markus Riester
 * @version 0.1	
 */
public class HierarchicalClusterSettings extends Settings {

	private ObjectSelectionSetting<CLUSTER_METHOD> clustering_method;	
    private DistanceMeasureSetting distance_measure;    
    private BooleanSetting matrixTransposed;
    
    private Bipartition bipartition = new Bipartition();
//    private boolean start = false;
   
    public HierarchicalClusterSettings() {
    	super(new HierarchicalSetting("Hierarchical Clustering"), PluginInfo.getPreferences("PAS.clustering.hierarchical"));
    	clustering_method = new ObjectSelectionSetting<CLUSTER_METHOD>("Method", null, 4, CLUSTER_METHOD.values());
    	distance_measure = new DistanceMeasureSetting("Distance Measure", null, new PearsonCorrelationDistance());
    	matrixTransposed = new BooleanSetting("Transpose Matrix", "If selected, clustering will be done on the experiments instead of the probes", false);
    	root.addSetting(clustering_method).addSetting(distance_measure).addSetting(matrixTransposed);
    	PluginInfo.loadDefaultSettings(root, "PAS.clustering.hierarchical");
    }

    // Using legacy serialization to keep old MIO values working
    public String serialize() {
    	return "true\n"+  // legacy: always write true here 
    		   clustering_method.getObjectValue().ordinal()+"\n"+
    		   distance_measure.getPluginInfo().getName()+"\n"+
    		   matrixTransposed.getBooleanValue()
    		   ;
    }
    
    // Using legacy serialization to keep old MIO values working
    public void deserialize(String s) {
    	String[] ess = s.split("\n");
//    	start = Boolean.parseBoolean(ess[0]);  // legacy: ignore 
    	try {
    		clustering_method.setObjectValue(CLUSTER_METHOD.values()[Integer.parseInt(ess[1])]);
    	} catch (ArrayIndexOutOfBoundsException ae) {
    		System.err.println("Clustering method not found: "+ess[1]+", using default");
    	}
    	distance_measure.setInstance( DistanceMeasureManager.get(ess[2]) );
    	matrixTransposed.setBooleanValue(Boolean.parseBoolean(ess[3]));
    }
    
    /**
     * @return Returns the clustering_method.
     */
    public CLUSTER_METHOD getClustering_method() {
        return clustering_method.getObjectValue();
    }
    
    /**
     * @param clustering_method The clustering_method to set.
     */
    public void setClustering_method(CLUSTER_METHOD clustering_method) {
        this.clustering_method.setObjectValue(clustering_method);
    }
    
    /**
     * @return Returns the start.
     */
    public boolean getStart() {
        return true;  // legacy: always return true here 
    }
    /**
     * @param start The start to set.
     */
//    public void setStart(boolean start) {
//        this.start = start;  // legacy: ignore 
//    }
    /**
     * @return Returns the distance measure.
     */
    public DistanceMeasurePlugin getDistanceMeasure() {
        return distance_measure.getInstance();
    }
    /**
     * @param distance_measure The distance measure to set.
     */
    public void setDistanceMeasure(DistanceMeasurePlugin distance_measure) {
        this.distance_measure.setInstance(distance_measure);
    }
    
    public void dumpSettings() {
//        System.out.println("Everything fine :" + start);
        System.out.println("Cluster method  :" + clustering_method.getObjectValue());
        System.out.println("Distance method :" + distance_measure.getInstance());
        System.out.println("Matrix transpose:" + matrixTransposed.getBooleanValue());
    }
    
//  TL
	public void setMatrixTransposed(boolean matrixTransposed) {
		this.matrixTransposed.setBooleanValue(matrixTransposed);
	}

	public boolean isMatrixTransposed() {
		return matrixTransposed.getBooleanValue();
	}

	public void setBipartition(Bipartition bipartition) {
		this.bipartition = bipartition;
	}

	public Bipartition getBipartition() {
		return bipartition;
	}

//	end TL
	
}
