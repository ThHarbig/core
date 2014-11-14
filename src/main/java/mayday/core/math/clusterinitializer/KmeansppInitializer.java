package mayday.core.math.clusterinitializer;

import java.util.Random;

import mayday.core.math.distance.DistanceMeasurePlugin;
import mayday.core.structures.linalg.matrix.AbstractMatrix;

public class KmeansppInitializer implements IClusterInitializer {

	private AbstractMatrix clusteringData;
	
	@Override
	public void makeClusteringDataAvailable(AbstractMatrix ClusteringData) {
		if (ClusteringData.nrow()==0)
			throw new IllegalArgumentException("The given clustering data has no elements!");
		if (ClusteringData.ncol() == 0 ) 
			throw new IllegalArgumentException("The given clustering data vector has dimension 0!");
		this.clusteringData = ClusteringData;
	}

	@Override
	public double[][] getInitialDataVectors(int countOfClusters, DistanceMeasurePlugin distanceMeasure) {
		int numCenters = countOfClusters;
		int numProbes = this.clusteringData.nrow();
		
		// 2 + log k tries to find the best k-1 centers
		int numLocalTries = 2 + (int)Math.rint(Math.log(numCenters));
		
		double currentPot = 0.0;
		double[][] clusterCenters = new double[countOfClusters][this.clusteringData.ncol()];
		double[] closestDistSq = new double[numProbes];
		
		Random r = new Random();
		//choose one random center and set the closest distance value
		int randomIndex = r.nextInt(numProbes);
		
		double[] clusterCenter = this.clusteringData.getRow(randomIndex).toArray();
		clusterCenters[0] = clusterCenter;
		
		for(int i = 0; i < numProbes; i++) {
			double[] pb = this.clusteringData.getRow(i).toArray();
			double dist = distanceMeasure.getDistance(pb, clusterCenter);
			closestDistSq[i] = dist;
			currentPot += dist;
		}
		
		for(int i = 1; i < numCenters; i++) {
			double bestNewPot = -1.;
			int bestNewIndex = -1;
			
			for(int localTrial = 0; localTrial < numLocalTries; localTrial++){
				// choose the center
				// we have to be slightly careful to return a valid answer even accounting
				// for possible rounding errors
				double randVal = r.nextDouble() * currentPot;
				
				//choose the center
				int index = 0;
				for(int j = 0; j < numProbes-1; j++) {
					index = j;
					if(randVal <= closestDistSq[j]){
						break;
					}else{
						randVal -= closestDistSq[j];
					}
				}
				
				//compute the new potential
				double newPot = 0.0;
				for(int j = 0; j < numProbes; j++) {
					double[] pb = this.clusteringData.getRow(j).toArray();
					double[] pb2 = this.clusteringData.getRow(index).toArray();
					newPot += Math.min(distanceMeasure.getDistance(pb, pb2), closestDistSq[j]);
				}
				//store the best result
				if(bestNewPot < 0 || newPot < bestNewPot){
					bestNewPot = newPot;
					bestNewIndex = index;
				}
			}// end for numLocalTries
			
			//set the appropriate center
			double[] centerCandidate = this.clusteringData.getRow(bestNewIndex).toArray();
			clusterCenters[i] = centerCandidate;
			
			currentPot = bestNewPot;
			
			double[] pb2 = this.clusteringData.getRow(bestNewIndex).toArray();
			for(int j = 0; j < numProbes; j++) {
				double[] pb1 = this.clusteringData.getRow(j).toArray();
				closestDistSq[j] = Math.min(distanceMeasure.getDistance(pb1, pb2), closestDistSq[j]);
			}
		}
		
		return clusterCenters;
	}
}
