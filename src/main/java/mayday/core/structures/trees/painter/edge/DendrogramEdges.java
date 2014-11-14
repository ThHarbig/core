package mayday.core.structures.trees.painter.edge;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.geom.Line2D;

import mayday.core.structures.trees.layout.Coordinate;
import mayday.core.structures.trees.layout.EdgeLayout;
import mayday.core.structures.trees.layouter.TopDownDendrogram;
import mayday.core.structures.trees.painter.IEdgePainter;
import mayday.core.structures.trees.painter.node.LabelWithAngle;
import mayday.core.structures.trees.screen.ScreenLayout;
import mayday.core.structures.trees.tree.Edge;



public class DendrogramEdges implements IEdgePainter {
	
	public Color selectionColor = Color.cyan;

	public void paint(Edge e, Graphics g, ScreenLayout l, boolean selected) {
		boolean topDown = true;
		if (l.getLayouter() instanceof TopDownDendrogram) {
			topDown = ((TopDownDendrogram)l.getLayouter()).isTopDown();
		}		
		EdgeLayout elayout = l.getLayout(e);

		g.setColor(elayout.getColor());
		if(selected) {
			g.setColor(selectionColor);
			if(elayout.getColor().equals(selectionColor))
				g.setColor(Color.black);
		}

		
		Coordinate parent = l.getCoordinate(e.getNode(0));
		Coordinate child = l.getCoordinate(e.getNode(1));
		
        int startX = (int) parent.getX();
        int startY = (int) parent.getY();
        int endX = (int) child.getX();
        int endY = (int) child.getY();
        int midX = topDown ? endX : startX;
        int midY = topDown ? startY : endY;
        
       	g.drawLine(startX, startY, midX, midY);
       	g.drawLine(midX, midY, endX, endY);     
       	
       	l.setIncomingAngle(e.getNode(1), topDown ? 0d : 270d);
       	
		if (elayout.labelVisible() && e.getLabel()!=null) {
			LabelWithAngle.AngledLabelNodeShape ns = new LabelWithAngle.AngledLabelNodeShape(
					topDown?endX:(midX+endX)/2,
					topDown?(midY+endY)/2:endY,
					e.getLabel(), l.getNodeLayouts().get(e.getNode(0)), 
					topDown?0:270
			);
			ns.setFillBox(true);
			((Graphics2D)g).setBackground(new Color(255,255,255,200));
			ns.paintNodeShape((Graphics2D)g,false);
			((Graphics2D)g).setBackground(new Color(255,255,255,50));
		}
	}
	
	public String toString() {
		return "Dendrogram Edges";
	}
	
	//Abstand (- - -) zu Punkt f�r Eck-Kanten
	// P------
	//       |
	//       | - - - x
	//       |
	//       C
	public double distance(Edge e, ScreenLayout slayout, int x, int y) {
		boolean topDown = true;
		if (slayout.getLayouter() instanceof TopDownDendrogram) {
			topDown = ((TopDownDendrogram)slayout.getLayouter()).isTopDown();
		}	
		
		Coordinate parent = slayout.getCoordinate(e.getNode(0));
		Coordinate child = slayout.getCoordinate(e.getNode(1));
		
	    int startX = (int) parent.getX();
        int startY = (int) parent.getY();
        int endX = (int) child.getX();
        int endY = (int) child.getY();
        int midX = topDown ? endX : startX;
        int midY = topDown ? startY : endY;
		
//       	return Math.min( lineDist(startX,startY,midX,midY,x,y), lineDist(endX,endY,midX,midY,x,y) );
        return Math.min( Line2D.ptSegDist(startX,startY,midX,midY,x,y), Line2D.ptSegDist(endX,endY,midX,midY,x,y) );
        
	}
	
//	/** works only for 90° angles */
//	protected double lineDist(int sx, int sy, int ex, int ey, int x, int y) {
//		if (sx>ex) {
//			int tx = ex;
//			ex = sx;
//			sx = tx;
//		}
//		if (sy>ey) {
//			int ty = ey;
//			ey = sy;
//			sy = ty;
//		}
//		// find out which direction the edge is
//		if (sy==ey) { // edge is horizontal
//			if (x < sx || x > ex) { // outside of edge, take distance to endpoint
//				return Math.min(Point.distance(sx, sy, x, y), Point.distance(ex, ey, x, y));
//			} else {
//				return Math.abs( y-sy );
//			}			
//		} else {
//			if (y < sy || y > ey) { // outside of edge, take distance to endpoint
//				return Math.min(Point.distance(sx, sy, x, y), Point.distance(ex, ey, x, y));
//			} else {
//				return Math.abs( x-sx );
//			}		
//		}
//		
//	}
	
}
