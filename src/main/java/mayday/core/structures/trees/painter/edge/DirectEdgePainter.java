package mayday.core.structures.trees.painter.edge;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.geom.Line2D;

import mayday.core.structures.trees.layout.Coordinate;
import mayday.core.structures.trees.layout.EdgeLayout;
import mayday.core.structures.trees.painter.IEdgePainter;
import mayday.core.structures.trees.painter.node.LabelWithAngle;
import mayday.core.structures.trees.screen.ScreenLayout;
import mayday.core.structures.trees.tree.Edge;



public class DirectEdgePainter implements IEdgePainter {

	public void paint(Edge e, Graphics g, ScreenLayout l, boolean selected) {
		EdgeLayout elayout = l.getLayout(e);
		Coordinate parent = l.getCoordinate(e.getNode(0));
		Coordinate child = l.getCoordinate(e.getNode(1));
		g.setColor(elayout.getColor());
		if(selected) {
			g.setColor(Color.cyan);
			if(elayout.getColor().equals(Color.cyan))
				g.setColor(Color.black);
		}
        int startX = (int) parent.getX();
        int startY = (int) parent.getY();
        int endX = (int) child.getX();
        int endY = (int) child.getY();
		g.drawLine(startX, startY, endX, endY);
				
		double a = Math.abs(startY - endY);
		double c = Point.distance(startX, startY, endX, endY);
		double angle = Math.acos(a/c)*180/Math.PI;
		if (Double.isNaN(angle))
			angle=0d;
		
		if (startX<endX) {
			if (startY<endY) {
				angle = 360-angle; // bottom right child
			} else {
				angle+=180; // top right child
			}
		} else {
			if (startY<endY) {
				angle = angle-180; // bottom left child
			} else { 
				angle = -angle; // top left child
			}
		}
		if (angle==0d && startY>endY)
			angle = -360; //right-align if edge goes straight upwards
		l.setIncomingAngle(e.getNode(1), angle);
		
		if (elayout.labelVisible() && e.getLabel()!=null) {
			LabelWithAngle.AngledLabelNodeShape ns = new LabelWithAngle.AngledLabelNodeShape(
					(startX+endX)/2, (startY+endY)/2, e.getLabel(), l.getNodeLayouts().get(e.getNode(0)), angle);
			ns.setFillBox(true);
			((Graphics2D)g).setBackground(new Color(255,255,255,200));
			ns.paintNodeShape((Graphics2D)g,false);
			((Graphics2D)g).setBackground(new Color(255,255,255,50));
		}
	}

	public double distance(Edge e, ScreenLayout slayout, int x, int y) {
		Coordinate parent = slayout.getCoordinate(e.getNode(0));
		Coordinate child = slayout.getCoordinate(e.getNode(1));
		double x1 = parent.getX();
		double x2 = child.getX();
		double y1 = parent.getY();
		double y2 = child.getY();
		return Line2D.ptSegDist(x1, y1, x2, y2, x, y);
	}
	

	public String toString() {
		return "Direct Edges";
	}

}
