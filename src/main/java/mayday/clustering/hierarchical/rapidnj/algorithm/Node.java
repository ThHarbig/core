package mayday.clustering.hierarchical.rapidnj.algorithm;

import java.io.Serializable;

/*
 * equals node.cpp in the original implementation
 */
/**
 * This class represents a node i in the final unrooted tree
 */
public class Node implements Serializable
{
	/**
	 * serial
	 */
	private static final long serialVersionUID = 6786916138991470787L;
	private String name;
	private double[] edgeDist;
	private Node[] edges;
	private int edgeCount;
	
	/**
	 * creates an internal node
	 */
	public Node()
	{
		this.edgeDist = new double[3];
		this.edges = new Node[3];
		this.edgeCount = 0;
	}
	/**
	 * Creates a named leaf node
	 * @param name
	 */
	public Node(String name)
	{
		this.edgeDist = new double[1];
		this.edges = new Node[1];
		this.name = name;
		this.edgeCount = 0;
	}
	
	/**
	 * @return name
	 */
	public String getName()
	{
		return this.name;
	}
	
	/**
	 * Connect a node n to this node
	 * @param n
	 * @param distance
	 */
	public void addEdge(Node n, double distance)
	{
		if(n.equals(this))
		{
			throw new RuntimeException("Cannot add Node to self!");
		}
		
		edgeDist[edgeCount] = distance;
		edges[edgeCount] = n;
		edgeCount++;
	}
	
	/**
	 * Serialize the tree to newick format
	 * @return newick representation
	 */
	public String serializeTree()
	{
		if(edgeCount == 3)
		{
			//internal node
			return serializeNode(null) + ";";
		}
		else
		{
			if(edgeCount == 0)
			{
				//only one node, this one.
				return name + ";";
			}
			else if(edgeCount == 1 && edges[0].edgeCount == 1)
			{
				//only two nodes
				return "(" + name + "," + edges[0].name + ");";
			}
			else
			{
				//leaf, use the connected internal node to start the recursion
				return edges[0].serializeTree();
			}
		}
		//return "Error - could not serialize the tree";
	}
	
	/**
	 * Recursive helper method for serializing the tree
	 * @param n , the Node from which to serialize
	 * @return newick representation at the node
	 */
	public String serializeNode(Node n) 
	{
		if(edgeCount == 2)
		{
			System.out.println("ERROR - only two edges found");
		}

		if(edgeCount == 3)
		{
			//internal node
			String s = "(";
			for(int i = 0; i < edgeCount; i++)
			{
				Node edge = edges[i];
				if(!edge.equals(n))
				{
					s += edge.serializeNode(this);
					s += ":";
					s += Double.toString(Math.round(edgeDist[i]*10000.)/10000.);
					s += ",";
				}
			}
			StringBuffer sb = new StringBuffer(s);
			sb.replace(s.length()-1, s.length(), ")");
			s = sb.toString();
			return s;
		}
		else
		{
			return name; //"'" + name + "'";
		}
	}
}
