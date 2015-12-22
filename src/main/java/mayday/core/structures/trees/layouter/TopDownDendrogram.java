package mayday.core.structures.trees.layouter;


import java.awt.Color;
import java.awt.Font;
import java.util.Collection;
import java.util.HashMap;

import mayday.core.pluma.PluginInfo;
import mayday.core.pluma.PluginManagerException;
import mayday.core.structures.trees.layout.Coordinate;
import mayday.core.structures.trees.layout.EdgeLayout;
import mayday.core.structures.trees.layout.Layout;
import mayday.core.structures.trees.layout.NodeLayout;
import mayday.core.structures.trees.painter.edge.DendrogramEdges;
import mayday.core.structures.trees.painter.node.LabelWithAngle;
import mayday.core.structures.trees.tree.Edge;
import mayday.core.structures.trees.tree.Node;


/**
 * Computes relative positions of all Nodes and creates a Layout of them starting at
 * the root and dividing the 
 * @param scalingFactor when calculating the relative coordinates this factor is used as
 * height and width of the window
 * @author Michael Borner, Andreas Friedrich
 * @author Florian Battke - rewritten code
 *
 */
public class TopDownDendrogram extends TreeLayoutPlugin {

	protected boolean topDown = true;
	
	private HashMap<Node, Double> leafmap = new HashMap<Node, Double>();
	
	public Layout doLayout(Node root) {
		Collection<Node> leaves = root.getLeaves(null);
		double spacePerLeaf = 1.0d / ((double)leaves.size()-1);
		for(Node leaf : leaves) {
			leafmap.put ( leaf, spacePerLeaf*(double)leafmap.size());
		}
		Layout l = new Layout(root);		
		doLayoutInternal(l);
		return l;
	}
	
	/** produce a dendrogram layout but do not change the leaf positions */
	public Layout doLayout(Node root, HashMap<Node, Double> leafPositions) {
		leafmap = leafPositions;
		Layout l = new Layout(root);		
		if (topDown)
			l.setScaling(Layout.SCALING_HEIGHT);
		else
			l.setScaling(Layout.SCALING_WIDTH);
		doLayoutInternal(l);
		return l;	
	}
	
	protected void doLayoutInternal(Layout l) {
		Node root = l.getRoot();
		double edgefactor;
		if (ignoreEdgeLengths)
			edgefactor= 1.0d / (double)root.edgesToGround(null);  // Assume rooted tree always
		else
			edgefactor= 1.0d / root.distanceToGround(null);  // Assume rooted tree always
		
		doLayoutRecursive(root, null, 0, l, edgefactor);
		
		NodeLayout nodeLayout = new NodeLayout(new Color(0,0,0), 6, 6, new Font("Sans",10,10), new LabelWithAngle());
		EdgeLayout edgeLayout = new EdgeLayout(new Color(255,0,0), 1, new DendrogramEdges());
		l.setDefaultLayouts(nodeLayout, edgeLayout);
		l.setLayouter(this);
		leafmap.clear();
	}

	private double doLayoutRecursive(Node startNode, Edge incomingEdge, double height, Layout layout, double edgefactor) {
		Double nodePosition = leafmap.get(startNode);
		if (nodePosition==null) {
			double meanPosition = 0;
			int meanCount = 0; 
			
			for (Edge e : startNode.getEdges()) {
				if (e!=incomingEdge) {
					Node target = e.getOtherNode(startNode);
					double newHeight = height;
					if (ignoreEdgeLengths)
						newHeight+=1;
					else 
						newHeight+=e.getLength();
					double subPosition = doLayoutRecursive(target, e, newHeight, layout, edgefactor);
					meanPosition += subPosition;
					++meanCount;
				}
			}
			nodePosition = meanPosition / (double)meanCount;
		}
		if (topDown)
			layout.setCoordinate(startNode, new Coordinate(nodePosition, height*edgefactor) );
		else 
			layout.setCoordinate(startNode, new Coordinate(height*edgefactor, nodePosition) );
		return nodePosition;
	}

	public boolean isTopDown() {
		return topDown;
	}
	
	public void setTopDown(boolean td) {
		topDown = td;
	}
	
	public String toString() {
		return "Dendrogram (top-down)";
	}

	public PluginInfo register() throws PluginManagerException {
		PluginInfo pli = new PluginInfo(
				this.getClass(),
				"PAS.treelayout.TopDownDendrogram",
				new String[0],
				MC,
				new HashMap<String, Object>(),
				"Florian Battke",
				"battke@informatik.uni-tuebingen.de",
				"Dendrogram (top-down)",
				"Dendrogram (top-down)"
		);
		return pli;	
	}

	public void init() {
	}

}
