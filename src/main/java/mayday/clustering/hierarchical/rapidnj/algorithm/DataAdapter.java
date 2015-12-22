package mayday.clustering.hierarchical.rapidnj.algorithm;

import mayday.clustering.hierarchical.pal.IdGroup;
import mayday.clustering.hierarchical.pal.Identifier;
import mayday.core.structures.linalg.matrix.PermutableMatrix;


/**
 * @author Kirsten Heitmann & Günter Jäeger
 * @version 0.1
 * 
 */
public class DataAdapter 
{
	private PermutableMatrix matrix;
	private int matrixSize = -1;
	private PolyTree myTree;
	private Node[] nodes;
	
	public DataAdapter(PermutableMatrix dists, IdGroup idg) 
	{
		this.matrixSize = dists.nrow();
		this.createDataStructures();
		this.matrix = dists;
			
		//parse data for algorithms
		for(int i = 0; i < idg.getIdCount(); i++) {
			Identifier id = idg.getIdentifier(i);
			String nodeName = id.getName();
			this.parseData(nodeName, i);
		}
	}

	private void createDataStructures()
	{
		this.myTree = new PolyTree(this.matrixSize);
		this.nodes = new Node[this.matrixSize];
	}
	
	private void parseData(String nodeName, int i)
	{
		this.myTree.addLeaf(nodeName);
		this.nodes[i] = new Node(nodeName);
	}
	
	/**
	 * @return size of the matrix
	 */
	public int getSize()
	{
		return this.matrixSize;
	}
	
	/**
	 * @return nodes
	 */
	public Node[] getNodes()
	{
		return this.nodes;
	}
	
	/**
	 * @return distance matrix
	 */
	public PermutableMatrix getMatrix()
	{
		return this.matrix;
	}
	
	/**
	 * @return PolyTree
	 */
	public PolyTree getTree()
	{
		return this.myTree;
	}
}
