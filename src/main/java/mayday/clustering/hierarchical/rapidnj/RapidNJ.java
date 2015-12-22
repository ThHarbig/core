package mayday.clustering.hierarchical.rapidnj;

import java.io.Serializable;

import mayday.clustering.hierarchical.pal.IdGroup;
import mayday.clustering.hierarchical.rapidnj.algorithm.DataAdapter;
import mayday.clustering.hierarchical.rapidnj.algorithm.Node;
import mayday.clustering.hierarchical.rapidnj.algorithm.PolyTree;
import mayday.clustering.hierarchical.rapidnj.algorithm.SimpleNJ;
import mayday.clustering.hierarchical.rapidnj.algorithm.SortedNJ;
import mayday.core.structures.linalg.matrix.PermutableMatrix;
import mayday.core.structures.trees.io.PlainNewick;


/**
 * @author Kirsten Heitmann & Günter Jäger
 * @version 0.1
 *
 */
public class RapidNJ implements Serializable
{
	/**
	 * serial
	 */
	private static final long serialVersionUID = 6397016857685022136L;
	
	private static final String SIMPLE = "Neighbor Joining";
	private static final String SORTED = "Rapid Neighbor Joining";
	private static final String THREADED = "Threaded";

	private mayday.core.structures.trees.tree.Node tree;
	private PolyTree polyTree;
	private String newick;
	private Node[] nodes;
	private PermutableMatrix matrix;
	private int matrixSize;
	private String method;
	
	private String[] nodeNames;
	
	/**
	 * Used to start the simple version of the rapidNJ algorithm
	 */
	public static final int SIMPLE_NJ = 0;
	/**
	 * Used to start the sorted version of the rapidNJ algorithm
	 */
	public static final int SORTED_NJ = 1;
	/**
	 * Used to start the threaded version of the rapidNJ algorithm
	 */
	public static final int THREADED_NJ = 2;

	
	public static mayday.core.structures.trees.tree.Node runRapidNJ(PermutableMatrix dists, IdGroup idg, int method) {
		return new RapidNJ(dists, idg, method).getTree();
	}
	
	/**
	 * Constructor, reads in distance matrix from a file
	 * @param filename , the absolute path to the file
	 */
	public RapidNJ(PermutableMatrix dists, IdGroup idg, int method) { 
		DataAdapter dataReader = new DataAdapter(dists, idg);
		this.matrix = dataReader.getMatrix();
		this.matrixSize = dataReader.getSize();
		this.nodes = dataReader.getNodes();
		this.polyTree = dataReader.getTree();
		this.nodeNames = new String[this.nodes.length];
		for(int i = 0; i < nodeNames.length; i++) nodeNames[i] = nodes[i].getName();
		createTree(method);
	}
	
	/**
	 * Calculate a new tree using the specified method
	 * The method can be Simple = 0, Sorted = 1 or Threaded = 2
	 * @param method
	 */
	public void createTree(int method)
	{
		switch(method)
		{
			case SIMPLE_NJ:
				updateSimpleNJ();
				this.method = SIMPLE;
				//System.out.println("\nNewick-String:\n" + this.newick);
				break;
			case SORTED_NJ: 
				updateSortedNJ();
				this.method = SORTED;
				//System.out.println("\nNewick-String:\n" + this.newick);
				break;
			case THREADED_NJ: 
				updateThreadedNJ();
				this.method = THREADED;
				//System.out.println("\nNewick-String:\n" + this.newick);
				break;
			default:
				throw new RuntimeException("Invalid NJ method id "+method);
		}
	}

	private void updateThreadedNJ() 
	{
//		ThreadedNJ threadedNJ = new ThreadedNJ(matrixSize, matrix, nodes);
//		Node root = threadedNJ.run();
//		newick = root.serializeTree();
//		newick = newick.replaceAll(" ", "_");
//
//		this.gui.updateStatusln("\nMethod: THREADED");
//		
//		//visualize
//		try 
//		{
//			this.tree = TreeTool.readTree(new StringReader(this.newick));
//		} 
//		catch (IOException e) 
//		{
//			e.printStackTrace();
//		}
//		
	}

	private void updateSortedNJ()  
	{
		SortedNJ sortedNJ = new SortedNJ(matrixSize, matrix, nodes, polyTree);
		String ptree = sortedNJ.run();
		tree = new PlainNewick().parse(ptree);
	}

	private void updateSimpleNJ() 
	{
		SimpleNJ sNJ = new SimpleNJ(matrixSize, matrix, nodes);
		Node root = sNJ.run();
		tree = new PlainNewick().parse(root.serializeTree());
	}

	/*
	 * Define getter and setter
	 */
	
	/**
	 * @return tree , the SimpleTree representation of the NJ-Tree
	 */
	public mayday.core.structures.trees.tree.Node getTree()
	{
		return this.tree;
	}
	
	/**
	 * @return newick , the Newick-String representation of the NJ-Tree 
	 */
	public String getNewick()
	{
		return this.newick;
	}
	
	/**
	 * @return matrix , the distance matrix
	 */
	public PermutableMatrix getMatrix()
	{
		return this.matrix;
	}


	/**
	 * @return method
	 */
	public String getMethod() 
	{
		if(this.method != null) return this.method;
		return "no method used";
	}
}
