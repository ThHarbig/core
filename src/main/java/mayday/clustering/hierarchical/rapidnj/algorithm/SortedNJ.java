package mayday.clustering.hierarchical.rapidnj.algorithm;

import java.util.Arrays;

import mayday.core.structures.linalg.matrix.PermutableMatrix;
import mayday.core.structures.linalg.vector.AbstractVector;

/*
 * euqals sortedNJ.cpp in the original implementation
 */
/**
 * @author Kirsten Heitmann & Günter Jäger
 * @version 0.1
 *
 */
public class SortedNJ 
{
	private PermutableMatrix matrix;
	private PolyTree mytree;
	private int matrixSize;
	private double [] separationsums;
	private double [] separations;
	private int clusterCount;
	private int min1;
	private int min2;
	private ClusterPair [][] clusterData;
	private ClusterPair minPair;
	private int currentId;
	private int [] activeClusterIds;
	private double maxSeparation;
	private double globalMin;
	private short [] garbageFlags;
	private int minRowCache;
	private int [] rowLengths;
	

	/**
	 * Constructor
	 * @param matrixSize
	 * @param matrix
	 * @param nodes
	 * @param tree
	 */
	public SortedNJ(int matrixSize, PermutableMatrix matrix, Node[] nodes, PolyTree tree)
	{
		this.matrixSize = matrixSize;
		this.matrix = matrix;
		this.mytree = tree;
		this.separationsums = new double[matrixSize];
		this.separations = new double[matrixSize];
		this.rowLengths = new int[matrixSize];
		this.activeClusterIds = new int[matrixSize];
		this.garbageFlags = new short[matrixSize];
		this.clusterCount = matrixSize;
		this.currentId = 0;
		this.minRowCache = 0;
		this.maxSeparation = Double.MIN_VALUE;
		this.min1 = 0;
		this.min2 = 0;
	}

	/**
	 * Start the algorithm
	 * @return newick , the newick string representation of the NJ-tree
	 */
	public String run()
	{
		initialize();
		while(clusterCount > 2)
		{
			findMin(); //O(n²)
			mergeMinNodes();
			clusterCount--;
			updateData();
		}
		//finish by joining the two remaining clusters
		int index1 = -1;
		int index2 = -1;
		//find the last node
		for (int i = 0; i < matrixSize; i++)
		{
			if (activeClusterIds[i] != -1)
			{
				if(index1 == -1)
				{
					index1 = i;
				}
				else
				{
					index2 = i;
					break;
				}
			}
		}
		double distance = matrix.getValue(index1,index2);
		return mytree.serializeTree(activeClusterIds[index1], activeClusterIds[index2], distance);
	}

	/* updates datastructures after 2 clusters have been joined */
	private void updateData()
	{
		double newSeparationsum = 0;
		double mutualDistance = minPair.distance;
		AbstractVector row1 = matrix.getRow(min1);
		AbstractVector row2 = matrix.getRow(min2);
		maxSeparation = Double.MIN_VALUE;
		
		for (int i = 0; i < matrixSize; i++)
		{
			if (i == min1 || i == min2 || activeClusterIds[i] == -1)
			{
				row1.set(i,0);
			}
			else 
			{
				double val1 = row1.get(i);
				double val2 = row2.get(i);
				double dist = (val1 + val2 - mutualDistance) / 2.0;
				newSeparationsum += dist;
				//update the separationsum of cluster i
				separationsums[i] += (dist - val1 - val2);
				double separation = separationsums[i] / (clusterCount - 2);
				separations[i] = separation;
				//update the maximum separation
				if (separation > maxSeparation)
				{
					maxSeparation = separation;
				}
				row1.set(i,dist);
				matrix.setValue(i,min1,dist);
			}
		}
		//delete obsolete data
		activeClusterIds[min2] = -1;
		separationsums[min1] = newSeparationsum;
		separationsums[min2] = 0;
		rowLengths[min2] = 0;
		separations[min1] = newSeparationsum / (clusterCount - 2);
		//crate new cluster pair
		ClusterPair [] newData = clusterData[min1];
		rowLengths[min1] = clusterCount -1;
		activeClusterIds[min1] = nextId();
		int idx = 0;
		for (int i = 0; i < matrixSize; i++)
		{
			if (i != min1 && activeClusterIds[i] != -1)
			{
				newData[idx].index = i;
				newData[idx].distance = matrix.getValue(min1,i);
				newData[idx].id = activeClusterIds[i];
				idx++;
			}
		}
		Arrays.sort(newData, 0, clusterCount - 1);
		clusterData[min1] = newData;
	}
	
	/*merge two clusters */
	private void mergeMinNodes()
	{
		//calculate distances
		if (minPair==null) {
			throw new RuntimeException("Cannot find minimal pair. \nPerhaps some probe pairs have NaN distances. \n" +
					"A possible cause are absolutely invariant probes when clustering with Pearson Correlation distance.");
		}
		double dist = minPair.distance;
		double sep1 = separationsums[min1] / (clusterCount - 2);
		double sep2 = separationsums[min2] / (clusterCount - 2);
		double dist1 = (0.5 * dist) + (0.5 * (sep1 - sep2));
		double dist2 = (0.5 * dist) + (0.5 * (sep2 - sep1));
		//update tree
		mytree.addInternalNode(dist1, dist2, activeClusterIds[min1], activeClusterIds[min2]);
	}
	
	private int nextId()
	{
		return currentId++;
	}
	
	/* Initialize the datastructure which requires some processing */
	private void initialize()
	{
		//calculate initial separation rows
		for (int i = 0; i < matrixSize; i++)
		{
			double sum = 0;
			for (int j = 0; j < matrixSize; j++)
			{
				sum += matrix.getValue(i,j);
			}
			separationsums[i] = sum;
			double separation = sum / (clusterCount - 2);
			separations[i] = separation;
			if(separation > maxSeparation)
			{
				maxSeparation = separation;
			}
		}

		//make a sorted list of clusters for every row
		for (int i = 0; i < matrixSize; i++)
		{
			activeClusterIds[i] = nextId();
			garbageFlags[i] = 0;
		}
		clusterData = new ClusterPair[matrixSize][matrixSize];
		
		for(int i = 0; i < matrixSize; i++) 
		{
		    int rowSize = matrixSize - (i + 1);
		    clusterData[i] = new ClusterPair[matrixSize];
		    for(int m = 0; m < this.clusterData[i].length; m++) this.clusterData[i][m] = new ClusterPair();
		    rowLengths[i] = rowSize;
		    for(int j = i+1, idx = 0; j < matrixSize; j++, idx++ )
		    {
		        clusterData[i][idx].index = j;
		        clusterData[i][idx].distance = matrix.getValue(i,j);			
		        clusterData[i][idx].id = activeClusterIds[j];					 
		    }
		    // sort the row
		    Arrays.sort(clusterData[i], 0, rowSize);
		}
	}
	
	private void findMin()
	{
		globalMin = Double.MAX_VALUE;
		int startidx = minRowCache;
		int i = startidx;
		// start at the next best row found in the previous iteration, and hope the minimum value found in this row
		// is a good approximation for the global minimum. If the row has been joined and deleted, we just take the next row
		do
		{
			if(activeClusterIds[i] == -1)
			{
				//inactive row
			}
			else if(garbageFlags[i] == 1)
			{
				int rowsize = rowLengths[i];
			    findRowMinGarbage(i, rowsize);
			    garbageFlags[i] = 0;
			}
			else
			{
				int rowsize = rowLengths[i];
			    int deadCount = findRowMin(i, rowsize);
			    
			    if(deadCount > 30)
			    {			
			    	// mark row for garbage-collection
			    	garbageFlags[i] = 1;
			    }
			    
			}
			i++;
			i = i % matrixSize;
		} 
		while (i != startidx);
	}
	
	/* Search for the global minimum in row i */
	private int findRowMin(int i, int rowsize)
	{
		int deadCount = 0;
		double rowSep = separations[i];
		double rowMaxSep = rowSep + maxSeparation;
		int j;
		ClusterPair[] row = clusterData[i];
		
		for (j = 0; j < rowsize; j++)
		{
			ClusterPair pair = row[j];
			//check if pair is active
			if (pair.id == activeClusterIds[pair.index])
			{
				//check if we have looked at enough elements
				if(pair.distance - rowMaxSep > globalMin)
				{
					break;
				}
				// calculate the value we're optimizing over
			    double value = pair.distance - separations[pair.index] - rowSep;
			    if (value < globalMin)
			    {
			    	//cache last minimum row
			    	if (i != min1)
			    	{
			    		minRowCache = min1;
			    	}
			    	globalMin = value;
			    	minPair = row[j];
			    	min1 = i;
			    	min2 = minPair.index;
			    }
			}
			else 
			{
				deadCount++;
			}
		}
		return deadCount;
	}
	
	/* search row i for the global minimum and perform garbage collection of dead pairs in the row at the same time by shifting 
	 * pairs which is alive to the left in the arrays, thereby erasing the dead pairs.
	 */
	private void findRowMinGarbage(int i, int rowsize)
	{
		double rowSep = separations[i];
		double rowMaxSep = rowSep + maxSeparation;
		int j;
		int bufIndex = 0;
		ClusterPair[] row = clusterData[i];
		
		for(j = 0; j < rowsize; j++)
		{
			ClusterPair pair = row[j];
		    // check if pair is active
		    if(pair.id == activeClusterIds[pair.index]) 
		    {			
		    	// check if we have looked at enough elements		  	
		    	if(pair.distance - rowMaxSep > globalMin) 
		    	{							
		    		break;
		    	}			
		    	// calculate the value we're optimizing over
		    	double value = pair.distance - separations[pair.index] - rowSep;
		    	if(value < globalMin)
		    	{
		    		if(i != min1)
		    		{
		    			minRowCache = min1;
		    		}									  
		    	globalMin = value;								
		    	minPair = row[j];
		    	min1 = i;
		    	min2 = minPair.index;			  
		    	}					
		    row[bufIndex] = pair;
		    bufIndex++;
		    } 									
		}
		// continue garbage-collecting
		for(;j < rowsize; j++)
		{
			ClusterPair pair = row[j];			
		    if(pair.id == activeClusterIds[pair.index]) 
		    {
		      row[bufIndex] = pair;
		      bufIndex++;
		    }
		 }
		 rowLengths[i] = bufIndex;
	}
}