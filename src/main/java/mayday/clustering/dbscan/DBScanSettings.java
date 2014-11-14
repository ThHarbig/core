/**
 *  File KMeansSettings.java 
 *  Created on 05.07.2005
 *  As part of the package clustering.kmeans
 *  By Janko Dietzsch and Nils Gehlenborg
 *  
 */

package mayday.clustering.dbscan;

import mayday.core.math.distance.DistanceMeasureManager;
import mayday.core.math.distance.DistanceMeasurePlugin;

public class DBScanSettings {
	private int MinPts;
    private double Eps;
    
    private DistanceMeasurePlugin distanceMeasure;
    
    private String clusterIdentifierPrefix;
	
	/**
     * Constructor of the type KmeansSettings 
     * Creates an instance with default settings
     */
    public DBScanSettings() {
        this.MinPts = 4;
    	this.Eps = 0.5;
        this.distanceMeasure = DistanceMeasureManager.get("Euclidean");
        this.clusterIdentifierPrefix = "DBScan";
    }

    /**
     * This method writes the content of settings in a string
     * @return Returns the content of settings as string
     */
    public String toString() {
        StringBuilder content = new StringBuilder();
        content.append("Minimal number of points per cluster: "); content.append(MinPts); content.append("\n");
        content.append("Epsilon: ");content.append(this.Eps);content.append("\n");
        content.append("Used distance measure: "); content.append(this.distanceMeasure.toString()); content.append("\n");
        content.append("Prefix of the cluster identifier: "); content.append(this.clusterIdentifierPrefix); content.append("\n");       
        return content.toString();
    }
    
	public String getClusterIdentifierPrefix() {
		return clusterIdentifierPrefix;
	}

	public void setClusterIdentifierPrefix(String clusterIdentifierPrefix) {
		this.clusterIdentifierPrefix = clusterIdentifierPrefix;
	}

	public DistanceMeasurePlugin getDistanceMeasure() {
		return distanceMeasure;
	}

	public void setDistanceMeasure(DistanceMeasurePlugin distanceMeasure) {
		this.distanceMeasure = distanceMeasure;
	}

	public int getMinPts() {
		return MinPts;
	}

	public void setMinPts(int MinPts) {
		this.MinPts = MinPts;
	}

	public double getEps() {
		return Eps;
	}

	public void setEps(double Eps) {
		this.Eps = Eps;
	}
}
