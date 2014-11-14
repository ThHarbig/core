/*
 * File RandomInitializer.java
 * Created on 09.03.2004
 * As part of package MathObjects.Initializer
 * By Janko Dietzsch
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
public class RandomInitializer implements IClusterInitializer {
	private double range;
	private double middle;
	private int dimension;
	
	public RandomInitializer() {
		this.range = this.middle = this.dimension = 0;
	}
	
	/* (non-Javadoc)
	 * @see MathObjects.Initializer.IClusterInitializer#makeClusteringDataAvailable(double[][])
	 */
	public void makeClusteringDataAvailable(AbstractMatrix ClusteringData) {
		if (ClusteringData.nrow()==0)
			throw new IllegalArgumentException("The given clustering data has no elements!");
		if (ClusteringData.ncol() == 0 ) 
			throw new IllegalArgumentException("The given clustering data vector has dimension 0!");
		double min=0,max=0;
		min = ClusteringData.getMinValue(false);
		max = ClusteringData.getMaxValue(false);
		this.dimension = ClusteringData.ncol();
		this.range=max-min;
		this.middle=(min+max)/2;		
	}
	
	/* (non-Javadoc)
	 * @see MathObjects.Initializer.IClusterInitializer#getInitialDataVectors(int)
	 */
	public double[][] getInitialDataVectors(int CountOfClusters, DistanceMeasurePlugin distanceMeasure) {
		double[][] ClusterCenters = new double[CountOfClusters][this.dimension];
		int i,j;
		for (i=0; i<CountOfClusters; i++) 
			for(j=0; j < this.dimension; j++) {
				ClusterCenters[i][j]=(Math.random()-0.5)*range+middle;
			};
		return  ClusterCenters;
	}
	
	/**
	 * @return
	 */
	public double getRange() {
		return this.range;
	}

	/**
	 * @return
	 */
	public double getMiddle() {
		return this.middle;
	}
}
