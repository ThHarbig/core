/*
 * File RandomDataPointInitializer.java
 * Created on 09.03.2004
 * As part of package MathObjects.Initializer
 * By Janko Dietzsch
 *
 */
package mayday.core.math.clusterinitializer;

import java.util.HashSet;
import java.util.Set;

import mayday.core.math.distance.DistanceMeasurePlugin;
import mayday.core.structures.linalg.matrix.AbstractMatrix;

/**
 * The RandomDataPointInitializer initializes the cluser centers by vectors 
 * randomly choosen out of the collection of the clustering data vectors.
 * @author  Janko Dietzsch
 * @version 0.1
 * 
 * 
 */
public class RandomDataPointInitializer implements IClusterInitializer {
	
	private AbstractMatrix clusteringData;
	
	
	/* (non-Javadoc)
	 * @see MathObjects.Initializer.IClusterInitializer#makeClusteringDataAvailable(double[][])
	 */
	public void makeClusteringDataAvailable(AbstractMatrix ClusteringData) {
		if (ClusteringData.nrow()==0)
			throw new IllegalArgumentException("The given clustering data has no elements!");
		if (ClusteringData.ncol() == 0 ) 
			throw new IllegalArgumentException("The given clustering data vector has dimension 0!");
		this.clusteringData = ClusteringData;
	}
	
	/* (non-Javadoc)
	 * @see MathObjects.Initializer.IClusterInitializer#getInitialDataVectors(int)
	 */
	public double[][] getInitialDataVectors(int CountOfClusters, DistanceMeasurePlugin distanceMeasure) {
		double[][] ClusterCenters = new double[CountOfClusters][this.clusteringData.ncol()];
		int randInd=0;
		int i;
		int ClusterIndex = 0;
		Set<Integer> choosenSet = new HashSet<Integer>(); // hold the choosen data vectors
		do { // choose the cluster initialization vectors randomly from the collection of data vectors
			randInd = (int) Math.floor( Math.random()*this.clusteringData.nrow() );
			if ( choosenSet.add(new Integer(randInd)) )  {
				for(i = 0; i < this.clusteringData.ncol(); i++) 
					ClusterCenters[ClusterIndex][i] = this.clusteringData.getValue(randInd, i);
				ClusterIndex++;
			};
		} while (choosenSet.size() != CountOfClusters);
		return ClusterCenters;
	}
}
