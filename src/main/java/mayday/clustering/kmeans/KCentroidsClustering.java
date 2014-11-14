/**
 *  File KCentroidsClustering.java 
 *  Created on 10.07.2003
 *  As part of the package clustering.kmeans
 *  By Janko Dietzsch
 *  
 */


package mayday.clustering.kmeans;

import mayday.clustering.ClusterAlgorithms;
import mayday.core.math.average.IAverage;
import mayday.core.math.clusterinitializer.IClusterInitializer;
import mayday.core.math.distance.DistanceMeasurePlugin;
import mayday.core.structures.linalg.matrix.PermutableMatrix;
import mayday.core.structures.maps.MultiHashMap;

/**
 * This class implements a k-centroid algorithm. The kind of determintation 
 * of the cluster centroids can be selected.
 * 
 * @author Janko Dietzsch
 * @version 0.1
 */
public class KCentroidsClustering extends ClusterAlgorithms {
	
	private int k;
	
	private double[] [] Centers; 
	
	private int [] ClusterIndicator;
	
	private int cycles;
	
	private double errorThreshold;
	
	private IAverage usedCentroidAverage;
	
	/**
	 * @serial object to define the used distance measure
	 */
	private DistanceMeasurePlugin usedDistance = null;
    
    /**
     * @serial Initializer for the map units.
     */
	private IClusterInitializer clusterInitializer;
	
	
	/** 
	 * Default constructor of the KMeansClustering-class 
	 */

	public KCentroidsClustering(PermutableMatrix data,int k,int cycles,double ErrorThreshold, IAverage usedCentroidAverage, DistanceMeasurePlugin distance, IClusterInitializer initCluster) {
		super(data);
		this.k = k;
		this.cycles = cycles;
		this.errorThreshold = ErrorThreshold;
		this.usedCentroidAverage = usedCentroidAverage;
		this.usedDistance = distance;
		this.clusterInitializer = initCluster;
		
		//this.Centers=new double[k][this.cols_ClusterData];
		this.ClusterIndicator=new int[rows_ClusterData];
	}
	
	/**
     * Normalizes all data vectors to row-mean = 0 and row-sd = 1
     * Change the values of the data provided - source will be altered!!!  
     */
    public void normalizeDataMatrix() {
        // Changes the values of the data providing array!!! 
        this.ClusterData.normalizeRowWise();
    }
	
	
	/* Extended version of the intravariance sum 
	 
	public double intraVarianceSumOfClustering() {
		double [] ClusterVariances=new double[this.k];
		int [] ClusterSize=new int[this.k];
		double intraVarSum=0;
		int i;
		for (i=0;i<this.k;i++) {
			ClusterVariances[i]=0;
			ClusterSize[i]=0;
		}
		for (i=0;i<this.rows_ClusterData;i++) { 
			ClusterVariances[this.ClusterIndicator[i]]+=computeDistance(this.ClusterData[i],this.Centers[this.ClusterIndicator[i]]);
			ClusterSize[this.ClusterIndicator[i]]++;
		}
		for (i=0;i<this.k;i++) intraVarSum+=ClusterVariances[i]/ClusterSize[i];
		return intraVarSum;
	} */
	
	
	public double intraVarianceSumOfClustering() {
		double intraVarSum=0;
		for (int i=0;i<this.rows_ClusterData;i++) 
		 	intraVarSum+=this.usedDistance.getDistance(this.ClusterData.getRow(i),this.Centers[this.ClusterIndicator[i]]);
		return intraVarSum;
	} 
	
	
	
	public int [] runClustering() {
		int i,j,l;
		int Cyc=0,Cluster=0;
		double Dist,tempDist;
		//int [] ClusterSize=new int[this.k];
		boolean ClusteringIsStable=true;
		boolean canceled = false;
		
		//		 Initialization of the cluster centroids --> random data points, random points in the range of data, PCA, ...
		this.clusterInitializer.makeClusteringDataAvailable(ClusterData);
		this.Centers = this.clusterInitializer.getInitialDataVectors( this.k, this.usedDistance);
		
//		Cluster2DataMap clMap = new Cluster2DataMap(this.rows_ClusterData, this.k);
		
		MultiHashMap<Integer,Integer> clusterToElements=new MultiHashMap<Integer, Integer>();
		
		do {
			Cyc++;
			
			clusterToElements.clear();			
			ClusteringIsStable=true;
			// Compute the indicator function for the actual cluster centers
			for (i=0;i<this.rows_ClusterData;i++) {
				Cluster = 0;
				Dist = Double.MAX_VALUE;
				for(j = 0; j < this.k; j++) 
				{					
					tempDist = this.usedDistance.getDistance(this.ClusterData.getRow(i),this.Centers[j]);
					if  (tempDist < Dist) 
					{
						Cluster = j;
						Dist = tempDist;						
					} 
				}
				clusterToElements.put(Cluster, i);
//				clMap.incrementSizeOfClusterNumber(Cluster);
				if (this.ClusterIndicator[i] != Cluster) 
				{
					this.ClusterIndicator[i] = Cluster;
					ClusteringIsStable = false;					
				} 
			}
			
			// Are clusters stable - leave the loop
			if (ClusteringIsStable) break;
			
//			// Prepare the mapping from cluster number to data row index
//			try {
//				clMap.organizeMapCluster2Data(this.ClusterIndicator);
//			} catch (DataFormatException exc) {
//				
//			};
			
			for(i=0; i!=k;++i)
				System.out.println(i+":"+clusterToElements.get(i).size());
			
			
			for(i=0; i!= k; ++i)
			{
				if(clusterToElements.containsKey(i) && clusterToElements.get(i).size()!=0)
				{
					double [] temp = new double[clusterToElements.get(i).size()];
					for (l = 0; l < this.cols_ClusterData; l++) 
					{		
						int idx=0;
						for(Integer v:clusterToElements.get(i))
						{
							temp[idx]=this.ClusterData.getValue(v,l);
							++idx;
						}
						this.Centers[i][l] = this.usedCentroidAverage.getAverage(temp);
					}					
				}
			}
			
			
//			
//			// Update the cluster centers
//			for (i=0; i < this.k; i++) {
//				int elements = clMap.getClusterSize(i);
//				if (elements != 0) {
//					double [] temp = new double[elements];
//					for (l = 0; l < this.cols_ClusterData; l++) {
//						clMap.initIteratrorOfCluster(i);
//						for (int ind = 0; ind < elements; ind++ ) {
//							temp[ind] = this.ClusterData.getValue(clMap.getCurrentDataIndexOfCluster(i),l);
//							clMap.nextDataIndexOfCluster(i);
//						}
//						this.Centers[i][l] = this.usedCentroidAverage.getAverage(temp);
//					}
//				}
//			}
			
			
//			clMap.reset();
			
			if (this.ProgressHook != null) { // report progress status if necessary
				canceled = this.ProgressHook.reportCurrentFractionalProgressStatus((double) Cyc/this.cycles);
			}
		} while ((intraVarianceSumOfClustering() > this.errorThreshold)&&(Cyc<this.cycles) && (!canceled));			
		
//		for (i=0;i<rows_ClusterData;i++) {
//					for (j=0;j<cols_ClusterData;j++) {						
//						System.out.print(this.ClusterData.getValue(i,j));
//						System.out.print(' ');
//					}; 
//					System.out.print('\n');			
//		};
//		System.out.println(" Rows: "+rows_ClusterData+" Cols: "+cols_ClusterData);
//		System.out.println("clustering performed");
		
		int [] Clustering=new int[this.rows_ClusterData];
		Clustering=this.ClusterIndicator;

		if (this.ProgressHook != null) { // report progress status if necessary
			this.ProgressHook.reportCurrentFractionalProgressStatus(1.0); // end is reached
		}

		
		return Clustering;
	}
		
}
