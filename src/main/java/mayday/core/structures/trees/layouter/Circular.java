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
import mayday.core.structures.trees.painter.edge.RadialEdgePainter;
import mayday.core.structures.trees.painter.node.LabelWithAngle;
import mayday.core.structures.trees.tree.Edge;
import mayday.core.structures.trees.tree.Node;

/**
 * Creates a radial Layout of a given tree (root)
 * @author Andreas Friedrich
 *
 */
public class Circular extends TreeLayoutPlugin {

	private HashMap<Node, Double> anglemap = new HashMap<Node, Double>();
	
	public Layout doLayout(Node root) {
		Collection<Node> leaves = root.getLeaves(null); // assume rooted layout
		int i=0; 
		for(Node leaf : leaves) {
			anglemap.put(leaf, 360.0*i/leaves.size());
			++i;
		}
		double radiusfactor;
		if (ignoreEdgeLengths)
			radiusfactor= 0.5d / (double)root.edgesToGround(null);  // Assume rooted tree always
		else
			radiusfactor= 0.5d / root.distanceToGround(null);  // Assume rooted tree always

		Layout l = new Layout();
		doLayoutRecursive(root, null, 0, l, radiusfactor);
		l.setRoot(root);
		NodeLayout nodeLayout = new NodeLayout(new Color(0,0,0), 6, 6, new Font("Sans",10,10), new LabelWithAngle());
		EdgeLayout edgeLayout = new EdgeLayout(new Color(255,0,0), 1, new RadialEdgePainter());
		l.setDefaultLayouts(nodeLayout, edgeLayout);
		l.setLayouter(this);
		l.setScaling(Layout.SCALING_BOTH_ASPECT);
		anglemap.clear();
		return l;
	}
	

	private double doLayoutRecursive(Node startNode, Edge incomingEdge, double radius, Layout layout, double rfactor) {
		Double angle = anglemap.get(startNode);
		if (angle==null) {
			double meanAngle = 0;
			int meanCount = 0; 
			
			for (Edge e : startNode.getEdges()) {
				if (e!=incomingEdge) {
					Node target = e.getOtherNode(startNode);
					double newradius = radius;
					if (ignoreEdgeLengths)
						newradius+=1;
					else 
						newradius+=e.getLength();
					double subAngle = doLayoutRecursive(target, e, newradius, layout, rfactor);
					meanAngle += subAngle;
					++meanCount;
				}
			}
			angle = meanAngle / (double)meanCount;
		}
		layout.setCoordinate(startNode, (trigoTransform(radius*rfactor,angle)));
		return angle;
	}
	
	private Coordinate trigoTransform(double radius, double angle) {
		double x;
		double y;
		double halfcircle = 0.5;
		if(angle <= 90.0) {
			x = halfcircle + Math.sin(Math.toRadians(angle))*radius;
			y = halfcircle - Math.cos(Math.toRadians(angle))*radius;
		} else
			if(angle <= 180.0) {
				x = halfcircle + Math.cos(Math.toRadians(angle-90.0))*radius;
				y = halfcircle + Math.sin(Math.toRadians(angle-90.0))*radius;
			} else
				if(angle <= 270.0) {
					x = halfcircle - Math.cos(Math.toRadians(270.0 - angle))*radius;
					y = halfcircle + Math.sin(Math.toRadians(270.0 - angle))*radius;
				} else {
					x = halfcircle - Math.cos(Math.toRadians(angle - 270.0))*radius;
					y = halfcircle - Math.sin(Math.toRadians(angle - 270.0))*radius;
				}
		return new Coordinate(x,y);
	}
	
	public String toString() {
		return "Circular";
	}


	public void init() {
	}


	public PluginInfo register() throws PluginManagerException {
		PluginInfo pli = new PluginInfo(
				this.getClass(),
				"PAS.treelayout.circular",
				new String[0],
				MC,
				new HashMap<String, Object>(),
				"Florian Battke",
				"battke@informatik.uni-tuebingen.de",
				"Circular layout",
				"Circular"
		);
		return pli;	
	}
	
}
