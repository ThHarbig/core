/*
 * File IClusterInitializer.java
 * Created on 09.03.2004
 *As part of package MathObjects.Initializer
 *By Janko Dietzsch
 *
 */
package mayday.core.math.clusterinitializer;

import mayday.core.math.distance.DistanceMeasurePlugin;
import mayday.core.structures.linalg.matrix.AbstractMatrix;

/**
 * @author  Janko Dietzsch
 * @version 0.1
 * 
 * 
 */
public interface IClusterInitializer {
	/**
	 * This method makes the data that will be clustered available to the initializer object.
	 * @param ClusteringData
	 */
	public void makeClusteringDataAvailable(AbstractMatrix ClusteringData);
	/**
	 * This method returns initial data vectors for the number of requested cluster centers. 
	 * @param CountOfClusters
	 * @return field of inital cluster center vectors
	 */
	public double[][] getInitialDataVectors(int CountOfClusters, DistanceMeasurePlugin distanceMeasure); 
}

