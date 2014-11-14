package mayday.core.structures.trees.painter.node;

import java.awt.Color;
import java.awt.geom.Rectangle2D;

import mayday.core.structures.trees.layout.Coordinate;
import mayday.core.structures.trees.layout.NodeLayout;
import mayday.core.structures.trees.painter.INodePainter;
import mayday.core.structures.trees.painter.NodeShape;
import mayday.core.structures.trees.screen.ScreenLayout;
import mayday.core.structures.trees.tree.Node;

public abstract class AbstractNodePainter implements INodePainter {

	public double distance(Node n, ScreenLayout sl, int x, int y) {
		return getNodeShape(n, sl).distance(x,y);
	}
	
	public Rectangle2D getNodeBounds(Node n, ScreenLayout l) {
		Rectangle2D b =  getNodeShape(n, l).getBounds2D();
		Coordinate c = l.getCoordinate(n);
		b.setRect( b.getX()-c.x, b.getY()-c.y, b.getWidth(), b.getHeight());
		return b;
	}
	
	protected Color getColor(Node n, NodeLayout nl) {
		Color c = Color.black;
		if (nl!=null)
			c = nl.getColor(); 
		return c;
	}
	

	protected NodeShape getNodeShape(Node n, ScreenLayout l) {
		double angle = l.getIncomingAngle(n);
		NodeLayout nlayout = l.getLayout(n);
		double x = l.getCoordinate(n).getX();
		double y = l.getCoordinate(n).getY();
		return getNodeShape(x,y,n,nlayout,l,angle);
	}
	
	protected abstract NodeShape getNodeShape(double x, double y, Node n, NodeLayout nl, ScreenLayout sl, double angle);
	

}
