package mayday.clustering.hierarchical.rapidnj.algorithm;

import mayday.core.structures.linalg.matrix.PermutableMatrix;
import mayday.core.structures.linalg.vector.AbstractVector;

/*
 * euqlas simpleNJ.cpp in the original implementation
 */
/**
 * @author Kirsten Heitmann & Günter Jäger
 * @version 0.1
 *
 */
public class SimpleNJ 
{
	private PermutableMatrix matrix;
	private Node[] nodes;
	private int matrixSize;
	private double[] separationsums;
	private double[] separations;
	private int clusterCount;
	private int min1;
	private int min2;
	
	int countersim = 0;
	
	/**
	 * Constructor
	 * @param matrixSize
	 * @param matrix
	 * @param nodes
	 */
	public SimpleNJ(int matrixSize, PermutableMatrix matrix, Node[] nodes)
	{
		this.matrixSize = matrixSize;
		this.matrix = matrix;
		this.nodes = nodes;
		this.separationsums = new double[matrixSize];
		this.separations = new double[matrixSize];
		this.clusterCount = matrixSize;
	}
	
	/**
	 * Start the rapidNJ algorithm
	 * @return Node , the imaginary root of the tree
	 */
	public Node run()
	{
		initialize();
		while(clusterCount > 2)
		{
			findMin();
			mergeMinNodes();
			updateMatrix();
		}
		
		//join the two remaining clusters
		Node node1 = null;
		Node node2 = null;
		int index1 = -1;
		int index2 = -1;
		
		//find the last nodes
		for(int i = 0; i < matrixSize; i++)
		{
			Node node = nodes[i];
			if(node != null)
			{
				if(index1 == -1)
				{
					node1 = node;
					index1 = i;
				}
				else
				{
					node2 = node;
					index2 = i;
					break;
				}
			}
		}
		
		double distance = matrix.getValue(index1,index2);
		if (node1!=null)
			node1.addEdge(node2, distance);
		if (node2!=null)
			node2.addEdge(node1, distance);
		return node1;
	}
	
	private void findMin()
	{
		min1 = -1;
		min2 = -1;
		double min = Double.MAX_VALUE;
		
		for(int i = 0; i < matrixSize; i++)
		{
			if(nodes[i] != null)
			{
				AbstractVector row = matrix.getRow(i);
				double sep1 = separations[i];
				
				for(int j = 0; j < matrixSize; j++)
				{
					if(nodes[j] != null && i != j)
					{
						double sep2 = separations[j];
						double val = row.get(j) -sep1 -sep2;
						
						if(val < min)
						{
							//new minimum
							min1 = i;
							min2 = j;
							min = val;
						}
					}
				}
			}
		}
	}
	
	private void initialize()
	{
		//calculates initial separation rows
		for(int i = 0; i < matrixSize; i++)
		{
			double sum = 0;
			for(int j = 0; j < matrixSize; j++)
			{
				sum += matrix.getValue(i,j);
			}
			separationsums[i] = sum;
			separations[i] = sum / (clusterCount - 2);
		}
	}
	
	private void mergeMinNodes()
	{
		Node node1 = nodes[min1];
		Node node2 = nodes[min2];
		Node newNode = new Node();
		double dist = matrix.getValue(min1,min2);
		double sep1 = separations[min1];
		double sep2 = separations[min2];
		
		double dist1 = (0.5 * dist) + (0.5 * (sep1 - sep2));
		double dist2 = (0.5 * dist) + (0.5 * (sep2 - sep1));
		
		//update tree
		newNode.addEdge(node1, dist1);
		node1.addEdge(newNode, dist1);
		newNode.addEdge(node2, dist2);
		node2.addEdge(newNode, dist2);
		
		//update data
		nodes[min1] = newNode;
		nodes[min2] = null;
		clusterCount--;
	}
	
	private void updateMatrix()
	{
		double newSeparationsum = 0;
		double mutualDistance = matrix.getValue(min1,min2);
		AbstractVector row1 = matrix.getRow(min1);
		AbstractVector row2 = matrix.getRow(min2);
		
		for(int i = 0; i < matrixSize; i++)
		{
			if(i == min1 || i == min2 || nodes[i] == null)
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
				separationsums[i] += (dist -val1 - val2);
				separations[i] = separationsums[i] / (clusterCount - 2);
				row1.set(i,dist);
				matrix.setValue(i,min1,dist);
			}
		}
		separationsums[min1] = newSeparationsum;
		separations[min1] = newSeparationsum / (clusterCount - 2);
		separationsums[min2] = 0;
	}
}
