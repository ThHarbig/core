package mayday.core.structures.trees.painter.edge;
import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Point;

import mayday.core.structures.trees.layout.Coordinate;
import mayday.core.structures.trees.layout.EdgeLayout;
import mayday.core.structures.trees.painter.IEdgePainter;
import mayday.core.structures.trees.painter.node.LabelWithAngle;
import mayday.core.structures.trees.screen.ScreenLayout;
import mayday.core.structures.trees.tree.Edge;
import mayday.core.structures.trees.tree.Node;



/**
 * EdgePainter for radial Layout
 * @author Michael Borner
 *
 */
public class RadialEdgePainter implements IEdgePainter {

	//Winkel zu '3 Uhr'
	private double getAngle(Coordinate node, Coordinate root){
		double rx = root.getX();
		double ry = root.getY();
		double x = node.getX();
		double y = node.getY();
		double angle;
		double dist = root.distance(node);
		if((int) x == (int) rx) {
			if(y < ry)
				return 90;
			else return 270;
		}
		if((int) y == (int) ry) {
			if(x < rx)
				return 180;
			else return 0;
		}
		if(x > rx) {
			//rechts oben
			if(y < ry) {
				angle = Math.toDegrees(Math.acos((x - rx)/dist));
			}//rechts unten
			else {
				angle = 360.0 - Math.toDegrees(Math.acos((x - rx)/dist));
			}
		} else {
			//links oben
			if(y < ry) {
				angle = 90 + Math.toDegrees(Math.acos((ry - y)/dist));
			}
			//links unten
			else {
				angle = 180 + Math.toDegrees(Math.acos((rx - x)/dist));
			}
		}
		return angle;
	}
	
	public void paint(Edge e, Graphics g, ScreenLayout l, boolean selected) {
		EdgeLayout elayout = l.getLayout(e);
		
		g.setColor(elayout.getColor());
		
		if(selected) {
			g.setColor(Color.cyan);
			if(elayout.getColor().equals(Color.cyan))
				g.setColor(Color.black);
		}
		
		Node root = l.getRoot();
		Node parent = e.getNode(0);
		Node child = e.getNode(1);
		
		Coordinate parentc = l.getCoordinate(parent);
		Coordinate childc = l.getCoordinate(child);
		Coordinate rootc = l.getCoordinate(root);
		double childX = childc.getX();
		double childY = childc.getY();
		
		if(parent.equals(root)) {
			g.drawLine((int) childX, (int) childY, (int) rootc.getX(), (int) rootc.getY());
			double angle2 = 90-getAngle(childc,rootc);
			if (angle2==0)
				angle2-=1;

			l.setIncomingAngle(e.getNode(1), angle2);
			
			if (elayout.labelVisible() && e.getLabel()!=null) {
				LabelWithAngle.AngledLabelNodeShape ns = new LabelWithAngle.AngledLabelNodeShape(
						(childX+rootc.getX())/2, (childY+rootc.getY())/2, e.getLabel(), l.getNodeLayouts().get(e.getNode(0)), angle2);
				ns.setFillBox(true);
				((Graphics2D)g).setBackground(new Color(255,255,255,200));
				ns.paintNodeShape((Graphics2D)g,false);
				((Graphics2D)g).setBackground(new Color(255,255,255,50));
			}
			
		} else {
			Coordinate midpoint = getBoundaryPoint(childc,parentc,rootc);
			int radius = (int) rootc.distance(parentc);
			double angle1 = getAngle(parentc,rootc);
			double angle2 = getAngle(childc,rootc);
			double min = Math.min(angle1, angle2);
			double max = Math.max(angle1, angle2);
			double angle = max-min;
			// Draw the straight line from child to midpoint
			g.drawLine((int) childX, (int) childY, (int) midpoint.getX(), (int) midpoint.getY());
			// Draw the arc from midpoint to parent
			double start = max;
			if(angle > 180) {
				angle = 360-angle;
			} else {
				start = min;
			}
					
			angle = Math.round(angle);
			start = Math.round(start);
			g.drawArc((int)rootc.getX()-radius, (int)rootc.getY()-radius, 2*radius, 2*radius, (int) start, (int)angle);
			
			
			double startX = midpoint.getX();
			double startY = midpoint.getY();
			double endX = childX;
			double endY = childY;
			
			double a = Math.abs(startY - endY);
			double c = Point.distance(startX, startY, endX, endY);
			angle = Math.acos(a/c)*180/Math.PI;
			
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
	}
	
	//Berührpunkt von gerader Linie und Arc
	private Coordinate getBoundaryPoint(Coordinate child, Coordinate parent, Coordinate root) {
		double x = child.getX();
		double y = child.getY();
		double radius = root.distance(parent); 
		double diagonalfactor = (child.distance(root) - radius)/child.distance(root);
		double xlength = diagonalfactor * Math.abs(root.getX() - x);
		double ylength = diagonalfactor * Math.abs(root.getY() - y);
		int px;
		int py;
		if(x < root.getX())
			px = (int) (x + xlength);
		else
			px = (int) (x - xlength);
		if(y < root.getY())
			py = (int) (y + ylength);
		else
			py = (int) (y - ylength);
		return new Coordinate(px,py);
	}

	public double distance(Edge e, ScreenLayout slayout, int x, int y) {
		Node root = slayout.getRoot();
		Node parent = e.getNode(0);
		Node child = e.getNode(1);
		Coordinate rootc = slayout.getCoordinate(root);
		Coordinate parentc = slayout.getCoordinate(parent);
		Coordinate childc = slayout.getCoordinate(child);
		Coordinate boundary = getBoundaryPoint(childc,parentc,rootc);
		if(parent.equals(root))
			return edgeDistance(childc, boundary, rootc, x, y);
		else return Math.min(edgeDistance(childc, boundary, rootc, x, y),
							 angleDistance(childc, parentc, rootc, x, y));
	}
	
	private double edgeDistance(Coordinate child, Coordinate parent, Coordinate root, int x, int y) {
		double px = parent.getX();
		double py = parent.getY();
		double cx = child.getX();
		double cy = child.getY();
		double m = (py - cy) / (px - cx);
		double dist;
		if((int) px == (int) cx) {
			if(y > Math.max(py,cy) || y < Math.min(py,cy))
				dist = Math.min(parent.distance(x,y), child.distance(x,y));
			else
				dist = Math.abs(x - px);
		} else if((int) py == (int) cy) {
			if(x > Math.max(px,cx) || x < Math.min(px,cx))
				dist = Math.min(parent.distance(x,y), child.distance(x,y));
			else
				dist = Math.abs(y - py);
		} else {
			double c = cy - m*cx;
			dist = Math.abs((m*x - y + c)/(Math.sqrt(m*m+1)));
			double m_ortho = -1/m;
			double c_ortho = y - m_ortho*x;
			double intersect_x = (c_ortho - c) / (m - m_ortho);
			if(!pointOnEdge(intersect_x,parent,child))
				dist = 10; //(au�erhalb Reaktions-Reichweite)
		}
		return dist;
	}
	
	private double angleDistance(Coordinate child, Coordinate parent, Coordinate root, int x, int y) {
		Coordinate point = new Coordinate(x,y);
		double angle1 = getAngle(child, root);
		double angle2 = getAngle(parent, root);
		double angle = getAngle(point, root);
		double radius = root.distance(parent);
		double radDist = root.distance(point);
		double min = Math.min(angle1, angle2);
		double max = Math.max(angle1, angle2);
		double dist;
		if(max - min <= 180) {
			if(angle > min && angle < max)
				dist = Math.abs(radDist-radius);
			else dist = 10; //out of range
		} else {
			if(angle < min || angle > max)
				dist = Math.abs(radDist-radius);
			else dist = 10;
		}
		return dist;
	}
	
	//Berechnet ob ein Punkt auf einer Kante liegt oder nur auf der Gerade auf der die Kante liegt
	private boolean pointOnEdge(double x,Coordinate n1, Coordinate n2) {
		return (x <= Math.max(n1.getX(), n2.getX())) && (x >= Math.min(n1.getX(), n2.getX()));
	}
	
	public String toString() {
		return "Radial Edges";
	}

}

