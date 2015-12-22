package mayday.core.structures.trees.layout;


import java.awt.Color;
import java.awt.Font;
import java.util.Set;

import mayday.core.pluma.PluginInfo;
import mayday.core.pluma.PluginManager;
import mayday.core.structures.trees.layouter.TopDownDendrogram;
import mayday.core.structures.trees.layouter.TreeLayoutPlugin;
import mayday.core.structures.trees.painter.IEdgePainter;
import mayday.core.structures.trees.painter.INodePainter;
import mayday.core.structures.trees.painter.edge.DirectEdgePainter;
import mayday.core.structures.trees.painter.node.LabelBelow;
import mayday.core.structures.trees.tree.Edge;
import mayday.core.structures.trees.tree.ITreePart;
import mayday.core.structures.trees.tree.Node;

/**
 * The Layout representation of a tree. Contains positions of all Nodes and information about
 * the look of every Node and Edge
 * @author Andreas Friedrich, Michael Borner
 * @see @LayoutMap
 */
public class Layout {
	
	protected Node root;
	protected LayoutMap<Node, NodeLayout> nodemap;
	protected LayoutMap<Edge, EdgeLayout> edgemap;
	protected LayoutMap<Node, Coordinate> pointmap;
	
	protected ObjectMapper mpr;
	
	// serialized elements
	protected int scaling;
	
	public final static int SCALING_NONE = 0;      // 00000
	public final static int SCALING_WIDTH = 1;     // 00001
	public final static int SCALING_HEIGHT = 2;    // 00010
	public final static int SCALING_BOTH = 3;      // 00011 = width and height
	public final static int SCALING_BOTH_ASPECT =7;// 00111 = BOTH and Aspect
	
	protected TreeLayoutPlugin layouter;
	
	/**
	 * Creates a Layout from LayoutMaps and a tree
	 * @param root root of the tree contained in the new Layout
	 * @param nmap LayoutMap containing NodeLayouts
	 * @param emap LayoutMap containing EdgeLayouts
	 * @param pmap LayoutMap containing the coordinates of every Node
	 * @see @LayoutMap @NodeLayout @EdgeLayout
	 */
	public Layout(Node root, LayoutMap<Node, NodeLayout> nmap, LayoutMap<Edge, EdgeLayout> emap, LayoutMap<Node, Coordinate> pmap) {
		this.root = root;
		this.nodemap = nmap;
		this.edgemap = emap;
		this.pointmap = pmap;
		this.scaling = SCALING_BOTH;
	}
	
	/**
	 * Creates an empty Layout. Edges and Nodes will use a default Layout.
	 * @see @LayoutMap @NodeLayout @EdgeLayout
	 */
	public Layout() {
		this(new Node(null,null,null,null));
	}
	
	public Layout(Node root) {
		this(root, 
			new LayoutMap<Node, NodeLayout>(new NodeLayout(new Color(0,0,0), 6, 6, new Font("Serif",10,10), new LabelBelow())),
			new LayoutMap<Edge, EdgeLayout>(new EdgeLayout(new Color(0,0,0), 1, new DirectEdgePainter())),
			new LayoutMap<Node, Coordinate>(null));
	}
	
	public void parse(String s) {
		String[] parts = s.split(";");
		scaling = Integer.parseInt(parts[0]);
		PluginInfo pli = PluginManager.getInstance().getPluginFromID(parts[1]);		
		if (pli!=null)
			layouter = (TreeLayoutPlugin)pli.newInstance();
		else
			layouter = new TopDownDendrogram();
	}
	
	@SuppressWarnings("unchecked")
	public String serialize() {
		return scaling+";"+PluginManager.getInstance().getPluginFromClass((Class)layouter.getClass()).getIdentifier();
	}
	
	
	
	public Node getRoot() {
		return this.root;
	}
	
	public void setRoot(Node n) {
		this.root = n;
	}
	
	public LayoutMap<Node, NodeLayout> getNodeLayouts() {
		return this.nodemap;
	}
	
	public LayoutMap<Edge, EdgeLayout> getEdgeLayouts() {
		return this.edgemap;
	}

	/**
	 * Returns a Coord object from this Layouts pointmap
	 * @param n a Node of the tree
	 * @return Coord of this Node
	 * @see @Coord @LayoutMap
	 */
	public Coordinate getCoordinate(Node n) {
		return this.pointmap.get(n);
	}
	
	/**
	 * Sets a new Nodes coordinates or changes an old Nodes coordinates object
	 * @param n a Node of the tree
	 * @param c new Coord of this Node
	 * @see @Coord @LayoutMap
	 */
	public void setCoordinate(Node n, Coordinate c) {
		this.pointmap.put(n, c);
	}

	/**
	 * Returns a NodeLayout object from this Layouts nodemap
	 * @param n a Node of the tree
	 * @return NodeLayout of this Node
	 * @see @NodeLayout @LayoutMap
	 */
	public NodeLayout getLayout(Node n) {
		return this.nodemap.get(n);
	}

	/**
	 * Sets a new NodeLayout or changes an old Nodes NodeLayout
	 * @param n a Node of the tree
	 * @param nl new NodeLayout of this Node
	 * @see @NodeLayout @LayoutMap
	 */
	public void setLayout(Node n, NodeLayout nl) {
		this.nodemap.put(n, nl);
	}
	

	/**
	 * 
	 * @param i A part of this Tree, Edge or Node
	 * @return True if the parameter Node or Edge has a default Layout, false if not
	 */
	public boolean hasDefaultLayout(ITreePart i) {
		if(i instanceof Edge)
			return this.edgemap.get(i)==this.edgemap.getDefaultLayout();
		else
			return this.nodemap.get(i)==this.nodemap.getDefaultLayout();
	}
	
	
	
	public ILayoutValue getLayout(ITreePart i) {
		if(i instanceof Edge)
			return getLayout((Edge)i);
		else
			return getLayout((Node)i);
	}
	
	
	
	public void setLayout(ITreePart i, ILayoutValue ilv) {
		if(i instanceof Edge && ilv instanceof EdgeLayout)
			setLayout((Edge)i, (EdgeLayout)ilv);
		else
			setLayout((Node)i, (NodeLayout)ilv);
	}
	
	/**
	 * Returns a EdgeLayout object from this Layouts edgemap
	 * @param e a Edge of the tree
	 * @return EdgeLayout of this Edge
	 * @see @EdgeLayout @LayoutMap
	 */
	public EdgeLayout getLayout(Edge e) {
		return this.edgemap.get(e);
	}

	/**
	 * Sets a new EdgeLayout or changes an old Edges EdgeLayout
	 * @param e a Edge of the tree
	 * @param el new EdgeLayout of this Edge
	 * @see @EdgeLayout @LayoutMap
	 */
	public void setLayout(Edge e, EdgeLayout el) {
		this.edgemap.put(e, el);
	}
	/**
	 * Returns a String representation of the trees EdgeLayouts ('!' for each Edge with default EdgeLayout)
	 * @param n root of this tree
	 * @return String representation of all EdgeLayouts
	 */
	
	
	public Set<ITreePart> getNodeSet() {
		return this.pointmap.keySet();
	}

	/**
	 * Sets the default Layouts of this Layout object.
	 * @param nodeLayout the new default NodeLayout
	 * @param edgeLayout the new default EdgeLayout
	 */
	public void setDefaultLayouts(NodeLayout nodeLayout, EdgeLayout edgeLayout) {
		if (nodeLayout!=null)
			this.nodemap.SetDefaultLayout(nodeLayout);
		if (edgeLayout!=null)
			this.edgemap.SetDefaultLayout(edgeLayout);
	}
	
	/**
	 * Sets the default Painters for this Layout object
	 * @param nodePainter the new default NodePainter
	 * @param edgePainter the new default EdgePainter
	 */
	public void setDefaultPainters(INodePainter nodePainter, IEdgePainter edgePainter) {
		if (nodePainter!=null)
			this.nodemap.getDefaultLayout().setPainter(nodePainter);
		if (edgePainter!=null)
			this.edgemap.getDefaultLayout().setPainter(edgePainter);
	}
	
	public void setDefaultNodePainter(INodePainter nodePainter) {
		setDefaultPainters(nodePainter, null);
	}
	
	public void setDefaultEdgePainter(IEdgePainter edgePainter) {
		setDefaultPainters(null, edgePainter);
	}


	public int getScaling() {
		return scaling;
	}
	
	public void setScaling(int Scaling) {
		scaling = Scaling;
	}
	
	public void setLayouter(TreeLayoutPlugin layouter) {
		this.layouter = layouter;
	}
	
	public TreeLayoutPlugin getLayouter() {
		return layouter;
	}
	
	public LayoutMap<Node, Coordinate> getCoordinates() {
		return pointmap;
	}
	
	public Node getNode(Object o) {
		if (mpr!=null)
			return mpr.getNode(o);
		return null;
	}
	
	public Object getObject(Node n) {
		if (mpr!=null)
			return mpr.getObject(n);
		return null;
	}

	public void setObjectMapper(ObjectMapper m) {
		mpr = m;
	}
	
	public String getLabel(Node n) {
		if (mpr!=null)
			return mpr.getLabel(n);
		return n.getLabel();
	}
	
	public ObjectMapper getObjectMapper() {
		return mpr;
	}

	
}
