package mayday.core.structures.graph.algorithm;

import java.util.Comparator;

import mayday.core.structures.graph.Node;

/**
 * Comparator class for comparing nodes by degree. Different types of comparisons are possible.
 * The type can either be
 * <ul>
 * <li>DEGREE to compare the overall degree </li>
 * <li>INDEGREE to compare by incoming edges</li>
 * <li>OUTDEGREE to compare by outgoing edges </li>
 * </ul>
 * @author Stephan Symons 
 *
 */
public class NodeDegreeComparator implements Comparator<Node>
{
	public static final int INDEGREE=1;
	public static final int OUTDEGREE=2;
	public static final int DEGREE=0;
	
	private int type=DEGREE;
	
	public NodeDegreeComparator() 
	{
		
	}
	
	public NodeDegreeComparator(int type) 
	{
		this.type=type;
	}
	
	public int compare(Node o1, Node o2) 
	{
		switch (type) 
		{
		case INDEGREE:
			return new Integer(o1.getInDegree()).compareTo(o2.getInDegree());
		case OUTDEGREE:
			return new Integer(o1.getOutDegree()).compareTo(o2.getOutDegree());
		case DEGREE:
			return new Integer(o1.getDegree()).compareTo(o2.getDegree());
		default:
			throw new IllegalArgumentException("Unknown comparison type");
		}
		
	}

	
}
