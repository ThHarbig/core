package mayday.core.structures.trees.painter.node;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.geom.Point2D;

import mayday.core.structures.trees.layout.NodeLayout;
import mayday.core.structures.trees.painter.NodeShape;
import mayday.core.structures.trees.screen.ScreenLayout;
import mayday.core.structures.trees.tree.Node;

public class QuadraticUnlabeledNode extends AbstractNodePainter {

	@SuppressWarnings("serial")
	@Override
	protected NodeShape getNodeShape(double x, double y, Node n, NodeLayout nl,
			ScreenLayout sl, double angle) {
		return (n.isRoot()?
				new QuadraticNodeShape(x,y,sl.getLabel(n),nl) {
					public void paintNodeShape(Graphics2D g, boolean selected) {
						g.setColor(Color.red);
						g.fill(this);
					}
				}
				:new QuadraticNodeShape(x,y,sl.getLabel(n),nl));
	}

	public void paint(Node n, Graphics g, ScreenLayout l, boolean selected) {
		NodeLayout nlayout = (NodeLayout) l.getLayout(n);
		g.setColor(getColor(n,nlayout));        
        Graphics2D g2 = ((Graphics2D)g);
        getNodeShape(n, l).paintNodeShape(g2, selected);
	}
	
	public String toString() {
		return "Quadratic unlabeled nodes";
	}
	
	@SuppressWarnings("serial")
	public class QuadraticNodeShape extends NodeShape {

		protected double w,h;
		
		public QuadraticNodeShape(double x, double y, String label, NodeLayout nl) {
			super(x,y,label,0);
			w=nl.getWidth();
			h=nl.getHeight();
			addPoint(new Point2D.Double(x-w/2,y-h/2));
			addPoint(new Point2D.Double(x-w/2,y+h/2));
			addPoint(new Point2D.Double(x+w/2,y+h/2));
			addPoint(new Point2D.Double(x+w/2,y-h/2));	
		}
		
		@Override
		public void paintNodeShape(Graphics2D g, boolean selected) {
			Color c = g.getColor();
	        if(selected) {
				if (c.equals(Color.cyan))
					c=Color.black;
				else
					c = Color.cyan;
			}
	        g.setColor(c);
			g.fill(this);
		}
		
	}

}
