/**
 *  File KMeansClustering.java 
 *  Created on 23.07.2003
 *  As part of the package clustering.kmeans
 *  By Janko Dietzsch
 *  
 */


package mayday.clustering.kmeans;

import mayday.clustering.ClusterAlgorithms;
import mayday.core.math.clusterinitializer.IClusterInitializer;
import mayday.core.math.distance.DistanceMeasurePlugin;
import mayday.core.structures.linalg.matrix.PermutableMatrix;


/**
 * This class implements the well known k-means algorithm.
 * 
 * @author Janko Dietzsch
 * @version 0.1
 */
public class KMeansClustering extends ClusterAlgorithms {
	private int k;
	
	private double[] [] Centers; 
	
	private int [] ClusterIndicator;
	
	private int cycles;
	
	private double errorThreshold;
	
	
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

	public KMeansClustering(PermutableMatrix data,int k,int cycles,double ErrorThreshold, DistanceMeasurePlugin distance, IClusterInitializer initCluster) {
		super(data);
		this.k = k;
		this.cycles = cycles;
		this.errorThreshold = ErrorThreshold;
		this.usedDistance = distance;
		this.clusterInitializer = initCluster;
		
		//this.Centers=new double[k][this.cols_ClusterData];
		this.ClusterIndicator=new int[rows_ClusterData];
	}
	
//	public void initWithRandomSamplePoints() {
//		int randInd=0;
//	
////		for (int i=0;i<this.k;i++) {
////			randInd=(int)Math.floor(Math.random()*this.rows_ClusterData);
////			this.Centers[i]=this.ClusterData.get_row(randInd);
////		}		
//				
//		int ClusterIndex = 0;
//		Set<Integer> choosenSet = new HashSet<Integer>();
//		do {
//			randInd = (int) Math.floor( Math.random()*this.rows_ClusterData );
//			if ( choosenSet.add(new Integer(randInd)) )  {
//				this.Centers[ClusterIndex] = this.ClusterData.get_row(randInd);
//				ClusterIndex++;
//			};
//		} while (choosenSet.size() != this.k);
//	}
	
	
	
//	public void initWithRandomPoints() {
//		double min=0,max=0;
//		int i,j;
//		for (i=0;i<rows_ClusterData;i++) 
//					for(j=0;j<cols_ClusterData;j++) {
//						if (this.ClusterData.getValue(i,j)<min) min=this.ClusterData.getValue(i,j);
//						if (this.ClusterData.getValue(i,j)>max) max=this.ClusterData.getValue(i,j); 
//					};
//		double range=max-min;
//		double middle=(min+max)/2;
//		for (i=0;i<this.k;i++) 
//			for(j=0;j<this.cols_ClusterData;j++) {
//				this.Centers[i][j]=(Math.random()-0.5)*range+middle;
//			};
//	}
	
	
	
//	public double computeDistance(double [] a, double [] b) {
//		// if (a.length!=b.length) throw ...
//		double sum=0;
//		for (int i=0;i<a.length;i++) sum+=Math.pow((a[i]-b[i]),2.0);
//		return Math.sqrt(sum); 
//	}
	
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
		int i,j;
		int Cyc=0,Cluster=0;
		double Dist,tempDist;
		int [] ClusterSize=new int[this.k];
		boolean ClusteringIsStable=true;
		boolean canceled = false;
		
		//		 Initialization of the cluster centroids --> random data points, random points in the range of data, PCA, ...
		this.clusterInitializer.makeClusteringDataAvailable(ClusterData);
		this.Centers = this.clusterInitializer.getInitialDataVectors( this.k, this.usedDistance);
		
		do {
			Cyc++;
			
			ClusteringIsStable=true;
			// Compute the indicator function for the actual cluster centers
			for (i=0;i<this.rows_ClusterData;i++) {
				Cluster=0;
				Dist=Double.MAX_VALUE;
				for(j=0;j<this.k;j++) {
					tempDist=this.usedDistance.getDistance(this.ClusterData.getRow(i),this.Centers[j]);
					if  (tempDist<Dist) {
						Cluster=j;
						Dist=tempDist;
					} 
				}
				if (this.ClusterIndicator[i]!=Cluster) {
					this.ClusterIndicator[i]=Cluster;
					ClusteringIsStable=false;
				} 
			}
			// Are clusters stable - leave the loop
			if (ClusteringIsStable) break;
			
			// Update the cluster centers
			// fast and hardcoded way for arithmetic mean:
			for (i=0;i<this.k;i++) ClusterSize[i]=0;
			for (i=0;i<this.k;i++) for (j=0;j<this.cols_ClusterData;j++) this.Centers[i][j]=0;
			for (i=0;i<this.rows_ClusterData;i++) {
				Cluster=this.ClusterIndicator[i];
				ClusterSize[Cluster]++;
				for (j=0;j<this.cols_ClusterData;j++) this.Centers[Cluster][j]+=this.ClusterData.getValue(i,j);
			}
			for (i=0;i<this.k;i++) 
				if (ClusterSize[i]>0) 
					for (j=0;j<this.cols_ClusterData;j++) 
						this.Centers[i][j]=this.Centers[i][j]/ClusterSize[i];
			
			if (this.ProgressHook != null) { // report progress status if necessary
				canceled = this.ProgressHook.reportCurrentFractionalProgressStatus((double) Cyc/this.cycles);
			}
		} while ((intraVarianceSumOfClustering() > this.errorThreshold)&&(Cyc<this.cycles) && (!canceled));			

//		for (i=0;i<rows_ClusterData;i++) {
//			for (j=0;j<cols_ClusterData;j++) {						
//				System.out.print(this.ClusterData.getValue(i,j));
//				System.out.print(' ');
//			}; 
//			System.out.print('\n');			
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
