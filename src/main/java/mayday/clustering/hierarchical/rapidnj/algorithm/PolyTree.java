package mayday.clustering.hierarchical.rapidnj.algorithm;

import java.io.Serializable;

import mayday.core.structures.trees.io.PlainNewick;

/*
 * equals polytree.cpp in the original implementation
 */
/**
 * @author Kirsten Heitmann & Günter Jäger
 * @version 0.1
 *
 */
public class PolyTree implements Serializable
{
	/**
	 * serial
	 */
	private static final long serialVersionUID = -8290706293532081587L;
	private double[] distances;
	private int[] left_indexes;
	private int[] right_indexes;
	private String[] leaf_names;
	private int size;
	private int current_index;
	private int leaf_index;
	
	/**
	 * Constructor
	 * @param size
	 */
	public PolyTree(int size)
	{
		distances = new double[size*2-1];
		left_indexes = new int[size-1];
		right_indexes = new int[size-1];
		leaf_names = new String[size];
		this.size = size;
		current_index = 0;
		leaf_index = 0;
	}
	
	/**
	 * Add a leaf to the tree
	 * @param name
	 */
	public void addLeaf(String name)
	{
		leaf_names[leaf_index] = name;
		leaf_index++;
	}
	
	/**
	 * Add an internal node to the tree
	 * @param left_dist
	 * @param right_dist
	 * @param left_index
	 * @param right_index
	 */
	public void addInternalNode(double left_dist, double right_dist, int left_index, int right_index)
	{
		left_indexes[current_index] = left_index;
		right_indexes[current_index] = right_index;
		distances[left_index] = left_dist;
		distances[right_index] = right_dist;
		current_index++;
	}
	
	/**
	 * Used to create a newick string representation of the tree
	 * @param left_index
	 * @param right_index
	 * @param distance
	 * @return newick string
	 */
	public String serializeTree(int left_index, int right_index, double distance)
	{
		String s = "(";
		if(left_index < size)
		{
			s += "\""+PlainNewick.escapeSpecials(leaf_names[left_index])+"\"";
			s += ":";
			s += Double.toString(Math.round(distance*10000.)/10000.);
		}
		else
		{
			if(right_index >= size)
			{
				s += "(";
			}
			s += serializeNode(left_indexes[left_index-size], right_indexes[left_index-size], left_index);
			if(right_index >= size)
			{
				s += "):";
				s += Double.toString(Math.round(distance*10000.)/10000.);
			}
		}
		s += ",";
		if(right_index < size)
		{
			s += "\""+PlainNewick.escapeSpecials(leaf_names[right_index])+"\"";
			s += ":";
			s += Double.toString(Math.round(distance*10000.)/10000.);
		}
		else
		{
			s += serializeNode(left_indexes[right_index-size], right_indexes[right_index-size], right_index);
		}
		s += ");";
		return s;
	}

	/**
	 * Create a newick string for each node
	 * @param left_index
	 * @param right_index
	 * @param index
	 * @return newick string
	 */
	public String serializeNode(int left_index, int right_index, int index) 
	{
		String s = "";
		if(left_index < size)
		{
			//leaf node
			s += "\""+PlainNewick.escapeSpecials(leaf_names[left_index])+"\"";
			s += ":";
			s += Double.toString(Math.round(distances[left_index]*10000.)/10000.);
		}
		else
		{
			//serialize the left tree recursively
			s += "(";
			s += serializeNode(left_indexes[left_index-size], right_indexes[left_index-size], left_index);
			s += "):";
			s += Double.toString(Math.round(distances[left_index]*10000.)/10000.);
		}
		s += ",";
		if(right_index < size)
		{
			//leaf node
			s += "\""+PlainNewick.escapeSpecials(leaf_names[right_index])+"\"";
			s += ":";
			s += Double.toString(Math.round(distances[right_index]*10000.)/10000.);
		}
		else
		{
			s += "(";
			s += serializeNode(left_indexes[right_index-size], right_indexes[right_index-size], right_index);
			s += "):";
			s += Double.toString(Math.round(distances[right_index]*10000.)/10000.);
		}
		return s;
	}
	
	public PolyTree clone()
	{
		PolyTree tree = new PolyTree(this.size);
		tree.distances = this.distances.clone();
		tree.left_indexes = this.left_indexes.clone();
		tree.right_indexes = this.right_indexes.clone();
		tree.leaf_names = this.leaf_names.clone();
		tree.size = this.size;
		tree.current_index = this.current_index;
		tree.leaf_index = this.leaf_index;
		return tree;
	}
	
}
