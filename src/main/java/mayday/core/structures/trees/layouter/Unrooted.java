package mayday.core.structures.trees.layouter;


import java.awt.Color;
import java.awt.Font;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;

import mayday.core.structures.trees.layout.Coordinate;
import mayday.core.structures.trees.layout.EdgeLayout;
import mayday.core.structures.trees.layout.Layout;
import mayday.core.structures.trees.layout.NodeLayout;
import mayday.core.structures.trees.painter.edge.DirectEdgePainter;
import mayday.core.structures.trees.painter.node.LabelWithAngle;
import mayday.core.structures.trees.tree.Edge;
import mayday.core.structures.trees.tree.ITreePart;
import mayday.core.structures.trees.tree.Node;

/**
 * Creates an unrooted Layout of a given tree
 * @author Andreas Friedrich
 *
 */
public abstract class Unrooted extends TreeLayoutPlugin {

	double leafAngle, current_angle;
	double start_angle;
	
	public Unrooted(double startAngle) {
		start_angle = startAngle;
	}
	
	public Layout doLayout(Node root) {
		
		Layout layout = new Layout();
		layout.setRoot(root);
		
		leafAngle = 2 * Math.PI / root.numberOfDescendantLeaves(null);
		current_angle = start_angle;
        
        layout.setCoordinate(root, new Coordinate(0,0));
        
        doLayoutRecursive(root, null, layout);
        
        NodeLayout nodeLayout = new NodeLayout(new Color(0,0,0), 6, 6, new Font("Sans",10,10), new LabelWithAngle());
		EdgeLayout edgeLayout = new EdgeLayout(new Color(255,0,0), 1, new DirectEdgePainter());
		
		layout.setDefaultLayouts(nodeLayout, edgeLayout);
		layout.setLayouter(this);

		// finishing pass: scale everything to within the layout rectangle
		double minx=Double.POSITIVE_INFINITY, miny=minx;
		double maxx=Double.NEGATIVE_INFINITY, maxy=maxx;
		// gather range
		for (ITreePart itp : layout.getNodeSet()) {
			Coordinate c = layout.getCoordinate((Node)itp);
			minx = Math.min(minx, c.getX());
			maxx = Math.max(maxx, c.getX());
			miny = Math.min(miny, c.getY());
			maxy = Math.max(maxy, c.getY());
		}
		double rangex = maxx-minx;
		double rangey = maxy-miny;		
		// scale
		for (ITreePart itp : layout.getNodeSet()) {
			Coordinate c = layout.getCoordinate((Node)itp);
			c.setLocation((c.getX()-minx)/rangex, (c.getY()-miny)/rangey);
		}
		
		return layout;
	}
	

	private void doLayoutRecursive(Node startNode, Edge incomingEdge, Layout layout) {
		// place this node
		if (incomingEdge!=null) {
			Coordinate startC = layout.getCoordinate(incomingEdge.getOtherNode(startNode));
			double c_angle = current_angle;
            if (startNode.isLeaf()) {
            	current_angle += leafAngle;
            } else {
            	int nod = startNode.numberOfDescendantLeaves(incomingEdge);
                c_angle = current_angle + ((leafAngle * nod) /2);
            }
            
			double elength;
			if (ignoreEdgeLengths)
				elength=1;
			else 
				elength=incomingEdge.getLength();
            
			double nx = startC.getX() + elength * Math.cos(c_angle);
			double ny = startC.getY() + elength * Math.sin(c_angle);
			layout.setCoordinate(startNode, new Coordinate( nx,ny ));			
		}		
		
		
		for (Edge e : getSortedEdges(startNode, incomingEdge)) {
				doLayoutRecursive(e.getOtherNode(startNode), e, layout);
		}
		
	}

	protected Collection<Edge> getSortedEdges(final Node startNode, final Edge incomingEdge) {
        class NodeComparator implements Comparator<Edge> {
            public int compare(Edge e1, Edge e2) {
            	Node n1 = e1.getOtherNode(startNode);
            	Node n2 = e2.getOtherNode(startNode);
                return (n2.numberOfDescendants(e2) - n1.numberOfDescendantLeaves(e1));
            }
        }
        
        ArrayList<Edge> tmp = new ArrayList<Edge>();
        for (Edge e : startNode.getEdges()) {
			if (e!=incomingEdge) {
				tmp.add(e);
			}
        }
        
        Collections.sort(tmp, new NodeComparator());

        return tmp;
    }

	public void init() {
	}
}
