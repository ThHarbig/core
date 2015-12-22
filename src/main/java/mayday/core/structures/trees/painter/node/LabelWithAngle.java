package mayday.core.structures.trees.painter.node;

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.font.TextLayout;
import java.awt.geom.AffineTransform;
import java.awt.geom.Point2D;

import mayday.core.structures.trees.layout.NodeLayout;
import mayday.core.structures.trees.painter.NodeShape;
import mayday.core.structures.trees.screen.ScreenLayout;
import mayday.core.structures.trees.tree.Node;



public class LabelWithAngle extends QuadraticUnlabeledNode {

	public String toString() {
		return "Angled Nodes";
	}

	protected NodeShape getNodeShape(double x, double y, Node n, NodeLayout nl, ScreenLayout sl, double angle) {
		if (sl.getLabel(n).trim().length()>0 && nl.labelVisible())
			return new AngledLabelNodeShape(x,y,sl.getLabel(n).trim(),nl,angle);
		else
			return super.getNodeShape(x, y, n, nl, sl, angle); 		
	}
	
	public void paint(Node n, Graphics g, ScreenLayout l, boolean selected) {
		super.paint(n, g, l, selected);
	}

	@SuppressWarnings("serial")
	public static class AngledLabelNodeShape extends NodeShape {

		protected AffineTransform at;
		protected double h,w;
		protected boolean fillBox=false, drawBox=false;
		protected Font f;

		protected void produceTransform() {
			at = new AffineTransform();			
			at.translate(x, y);
			at.rotate((90+angle)*Math.PI/180d);
			if (angle<0)
				at.translate(-w, 0);
			at.translate(5, (h-10)/2);
		}

		public AngledLabelNodeShape(double X, double Y, String label, NodeLayout nl, double Angle) {
			super(X,Y,label,Angle);
			f = nl.getFont();
			TextLayout tl = new TextLayout(label, f, frc);			
			w=tl.getBounds().getWidth()+10; // space around
			h=tl.getBounds().getHeight()+10;
			angle=Angle;
			produceTransform();
			addPoint(at.transform(new Point2D.Double(-5,5), null));
			addPoint(at.transform(new Point2D.Double(-5,0-h+5), null));
			addPoint(at.transform(new Point2D.Double(w+5,0-h+5), null));
			addPoint(at.transform(new Point2D.Double(w+5,5), null));	
		}

		public void setDrawBox(boolean set) {
			drawBox = set;
		}
		
		public void setFillBox(boolean set) {
			fillBox = set;
		}
		
		public void paintNodeShape(Graphics2D g, boolean selected) {
			AffineTransform before = g.getTransform();
			if (drawBox)
				g.draw(this);
			
			Color tmp = g.getColor();
			
			if (fillBox) {				
				g.setColor(g.getBackground());
				g.fill(this);
			}
			if (selected) {
				g.setColor(new Color(tmp.getRed(), tmp.getGreen(), tmp.getBlue(), 50));
				g.fill(this);
			}

			g.setColor(tmp);
			g.transform(at);
			g.setFont(f);
			g.drawString(label, 0,0);        	
			g.setTransform(before);
		}

	}
}
