/*
 * File BatchSOMClustering.java
 * Created on 09.02.2004
 *As part of package clustering.som
 *By Janko Dietzsch
 *
 */
package mayday.clustering.som;

import java.util.LinkedList;
import java.util.List;

import mayday.clustering.ClusterAlgorithms;
import mayday.clustering.som.kernel.ISomKernel;
import mayday.clustering.som.kernel.LinearDecreasingRadiusKernel;
import mayday.core.math.clusterinitializer.IClusterInitializer;
import mayday.core.math.distance.DistanceMeasurePlugin;
import mayday.core.math.functions.IRotationalKernelFunction;
import mayday.core.structures.linalg.matrix.PermutableMatrix;
import mayday.core.structures.linalg.vector.DoubleVector;



/**
 * @author  Janko Dietzsch
 * @version 0.1
 * 
 * 
 */
public class BatchSOMClustering extends ClusterAlgorithms {
	
	// Helper class to store the sum of one voronoi set and the count of the 
	// contained data vectors 
	private class VoronoiSetsClass {
		private double[][] sumOfSets; // contains the sum of all data vectors inside a voronoi set associated to the unit [unit][weights]
		private int[] countOfContainedDataVectors; // count of data vectors inside a voronoi set [unit]
		private int[] markedVoronoiSets; // number of the associated Best Matching Unit for this data vector [data vector] 
		
		public VoronoiSetsClass(int CountOfDataVectors, int DataDimension, int MapUnits) {
			this.sumOfSets = new double[MapUnits][DataDimension];
			this.markedVoronoiSets = new int[CountOfDataVectors];
			for (int x = 0; x < this.markedVoronoiSets.length; x++) this.markedVoronoiSets[x] = -1;
			this.countOfContainedDataVectors = new int[MapUnits];			
		}
		
		public int getCountOfContainedDataVectors(int UnitID) {
			return this.countOfContainedDataVectors[UnitID];
		}
		
		public double[] getSumOfVoronoiSetOfUnit(int UnitID) {
			return this.sumOfSets[UnitID];
		}
		
		@SuppressWarnings({ "unchecked", "unused" })
		public List<Integer>[] getAllVoronoiSets(){
			LinkedList<Integer>[] ListOfAllVoronoiSets = new LinkedList[this.countOfContainedDataVectors.length];
			for (int x = 0; x < this.markedVoronoiSets.length; x++) 
				if (this.markedVoronoiSets[x] != -1) ListOfAllVoronoiSets[this.markedVoronoiSets[x]].addLast(new Integer(x)); 
			return (List<Integer>[]) ListOfAllVoronoiSets;
		}
        
        /**
         * This function returns the mapping between data vectors and according between best matching unit (BMU - SOM-neuron).
         * It returns an independent copy of the internal mapping vector of the queried instance object.
         * 
         * @return One dimensional array containing the index of the associated cluster for each data vector.
         */
        public int[] getCopyOfBMUIndices() {
            int[] BMUIndices = new int[this.markedVoronoiSets.length];
            for (int i = 0; i < this.markedVoronoiSets.length; i++) BMUIndices[i] = this.markedVoronoiSets[i];
            return BMUIndices;
        }
		
		public void add(int BestMatchingUnit, int NumberOfDataVector, double[] DataVector) {
			//if ( SumOfSet.length != DataVector.length) throw new IllegalArgumentException("Lenght of sum vector is not equal the length of data vectors!!!");
			this.markedVoronoiSets[NumberOfDataVector] = BestMatchingUnit;
			this.countOfContainedDataVectors[BestMatchingUnit]++;
			for (int x = 0; x < this.sumOfSets[BestMatchingUnit].length; x++) sumOfSets[BestMatchingUnit][x] += DataVector[x];
		}
		
		public void resetAll() {
			int x;
			for ( x = 0; x < this.markedVoronoiSets.length; x++) this.markedVoronoiSets[x] = -1;
			for ( x = 0; x < this.countOfContainedDataVectors.length; x++) this.countOfContainedDataVectors[x] = 0;
			for ( x = 0; x < this.sumOfSets.length; x++) 
				for(int y = 0; y < this.sumOfSets[x].length; y++) this.sumOfSets[x][y] = 0.0;
		}
		
		
	}
	
	/**
	 *  @serial used map of the units 	
	 */
	private UnitMap mapOfUnits = null;
	
	/**
	 *  @serial weights of the data vector elements 
	 */
	@SuppressWarnings("unused")
	private double [] dataMask = null;
	
	/**
	 *  @serial structure to save the Voronoi-set of each SOM-Unit 
	 */
	private VoronoiSetsClass voronoiSets = null;	
	
	/**
	 * @serial object to define the used distance measure
	 */
	private DistanceMeasurePlugin usedDistance = null;
	
	/**
	 * @serial object to define the used kernel function 
	 */
	private ISomKernel usedKernel;
    
    /**
     * @serial Initializer for the map units.
     */
	private IClusterInitializer ClusterInitializer;
	
	
//	private int k;
//	private double[] [] Centers; 
//	private int [] ClusterIndicator;
//	private int Cycles;
//	private double ErrorThreshold;
	
	/** 
	 * Default constructor of the BatchSOMClustering-class 
	 */
	public BatchSOMClustering(PermutableMatrix Data, GridTopology GridTopologyOfMap, int RowsOfMap, int ColsOfMap, 
            int Cycles, double initialKernelRadius, double finalKernelRadius, IRotationalKernelFunction kernelFunction) {
		this(Data, null, GridTopologyOfMap, RowsOfMap, ColsOfMap, Cycles, initialKernelRadius, finalKernelRadius, kernelFunction); 
	}
	
	/**
	 *  Constructor with data mask
	 */
	public BatchSOMClustering(PermutableMatrix Data,double [] DataMask, GridTopology GridTopologyOfMap, int RowsOfMap, int ColsOfMap, 
            int Cycles, double initialKernelRadius, double finalKernelRadius, IRotationalKernelFunction kernelFunction) throws IllegalArgumentException {
		super(Data);
		if (DataMask != null) {
			if (DataMask.length != this.cols_ClusterData) throw new IllegalArgumentException("Lenght of dataMask is not equal the length of data vectors!!!");
			else this.dataMask = DataMask;
		} else {
			DataMask = new double[this.cols_ClusterData];
			for (int i = 0; i < this.cols_ClusterData; i++) DataMask[i] = 1.0;
		};
		// Init UnitMap
		this.mapOfUnits = new UnitMap(RowsOfMap, ColsOfMap, this.cols_ClusterData, GridTopologyOfMap);
		this.voronoiSets = new VoronoiSetsClass(this.rows_ClusterData, this.cols_ClusterData, this.mapOfUnits.getAmountOfUnits());
     
        // Init the usedKernel
        this.usedKernel = new LinearDecreasingRadiusKernel(initialKernelRadius, finalKernelRadius, Cycles, 
                                                           this.mapOfUnits.getMaximalDistance(), kernelFunction);
	}
	
	/**
	 * Sets the used distance measure to find the best matching unit (BMU) and define this way 
	 * the voronoi sets of each map unit.
	 * @param DistObj is the used distance measure object and must implement the interface IDistanceMeasure
	 */
	public void setDistanceMeasure(DistanceMeasurePlugin DistObj) {
		this.usedDistance = DistObj;
	}

	/**
	 * @return Returns the clusterInitializer object.
	 */
	public IClusterInitializer getClusterInitializer() {
		return ClusterInitializer;
	}
	

	/**
	 * Sets the used algorithm to initialize the weights of the map units (neurons).  
	 * @param clusterInitializer is the used cluster initializer object and must implement the interface IClusterInitializer.
	 */
	public void setClusterInitializer(IClusterInitializer clusterInitializer) {
		ClusterInitializer = clusterInitializer;
	}
    
    /**
     * @return Returns the used Kernel-Function object.
     */
    public ISomKernel getUsedKernel() {
        return this.usedKernel;
    }
    

    /**
     * Sets the used kernel function for the calculation of the map unit (SOM-neuron) weights.
     * This kernel (one dimensional function of distance -> "rotationally symmetrical neighborhood") 
     * defines the exact influence of neighboring map units.  
     * @param usedKernel The usedKernel to set.
     */
    public void setUsedKernel(ISomKernel usedKernel) {
        this.usedKernel = usedKernel;
    }
    
    /**
     * Normalizes all data vectors to row-mean = 0 and row-sd = 1
     * Change the values of the data provided - source will be altered!!!  
     */
    public void normalizeDataMatrix() {
        // Changes the values of the data providing array!!! 
        this.ClusterData.normalizeRowWise();
    }
	
	/* (non-Javadoc)
	 * @see clustering.ClusterAlgorithms#runClustering()
	 */
	@SuppressWarnings("unchecked")
	public int[] runClustering() {
		boolean canceled = false;
		
		// Initialization of the mapOfUnits --> random data points, random points in the range of data, PCA, ...
		this.ClusterInitializer.makeClusteringDataAvailable(ClusterData);
		double[][] initialWeights = this.ClusterInitializer.getInitialDataVectors( this.mapOfUnits.getAmountOfUnits(), this.usedDistance);
		int data_count = 0;
		for (int x = 0; x < this.mapOfUnits.getRows(); x++) 
			for (int y = 0; y < this.mapOfUnits.getCols(); y++) {
				this.mapOfUnits.setWeightsOfUnit(initialWeights[data_count], x, y);
				data_count++;
			}
				
        // Iterative adaption of the unit (SOM-neuron) weights
        int x, dist;
        double KernelValue = 0.0;
        DoubleVector tempSumVec = new DoubleVector(cols_ClusterData);
        double tempSum = 0.0;
        DoubleVector VectorOfVoronoiSum = new DoubleVector(cols_ClusterData);
		do {
            this.usedKernel.tick();
			// Find the voronoi sets of the map units
			for (x = 0; x < this.rows_ClusterData; x++) 
				this.voronoiSets.add(this.mapOfUnits.getBestMatchingUnit(this.ClusterData.getRow(x), usedDistance), x, this.ClusterData.getRow(x).toArray());
		
			// Calculate new weights for all map units
			for (x = 0; x < this.mapOfUnits.getAmountOfUnits(); x++) {
				// Get neighbors of the actual unit with their distances
				List<List<Integer>> Neighbors = this.mapOfUnits.getNeighborhoodOfUnit(x);
				dist = 0;
				tempSumVec.setAllToValue(0.0);
				tempSum = 0.0;
				
				// Sum up over all different neighboring distances of the specified unit 
				for (List<Integer> UnitsOfThisDistance : Neighbors) {
						// get the value of the kernel for the given distance
						KernelValue = this.usedKernel.getValueOfDistance(dist);
						// Sum up over all neighbors laying in the given distance of the specified unit
                        for (Integer NeighborsInThisDistance : UnitsOfThisDistance ) {				
									// Collect all weighted added-vectors in this distance
									VectorOfVoronoiSum.set( this.voronoiSets.getSumOfVoronoiSetOfUnit(NeighborsInThisDistance.intValue()) );
									VectorOfVoronoiSum.multiply(KernelValue);
									tempSumVec.add(VectorOfVoronoiSum);
									// Collect the weighted count of data vectors of all voronoi sets in this distance
									tempSum += ( KernelValue * ( this.voronoiSets.getCountOfContainedDataVectors( NeighborsInThisDistance.intValue() ) ) );
						}
						dist++;
				}
				// Calculate the new values of the weights as the average of all weighted voronoi set sums in the distance of their best matching unit
				tempSumVec.divide(tempSum);
                
                // Set the new calculated weights of the unit (SOM-neuron)
				this.mapOfUnits.setWeightsOfUnit(tempSumVec.toArray(), x);
			} 
			if (!this.usedKernel.endReached()) this.voronoiSets.resetAll();
			if (this.ProgressHook != null) { // report progress status if necessary
				canceled = this.ProgressHook.reportCurrentFractionalProgressStatus(this.usedKernel.getCurrentProgress());
			}
		} while ((!this.usedKernel.endReached()) && (!canceled)); // Maximal count of executed iterations reached or canceled -> terminate 
        
        // Extract the the cluster indices for all data vectors and return it
		return this.voronoiSets.getCopyOfBMUIndices();
	}
    
    public double[][] getWeightsOfUnits() {
        double[][] UnitWeights = new double[this.mapOfUnits.getAmountOfUnits()][this.mapOfUnits.getDimensonOfWeights()];
        int[] SequenceOfUnitIDs = this.mapOfUnits.getSequenceOfUnitIDs();
        for (int cluster_i = 0; cluster_i < UnitWeights.length; cluster_i++) {
            double [] weights = this.mapOfUnits.getWeightsOfUnit(SequenceOfUnitIDs[cluster_i]);
            for (int weight_i = 0; weight_i < weights.length; weight_i++) UnitWeights[cluster_i][weight_i]=weights[weight_i];
        };
        return UnitWeights;
    }
    
    
}
