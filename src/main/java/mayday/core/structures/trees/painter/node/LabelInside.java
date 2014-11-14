package mayday.core.structures.trees.painter.node;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;

import mayday.core.structures.trees.layout.NodeLayout;
import mayday.core.structures.trees.painter.NodeShape;
import mayday.core.structures.trees.screen.ScreenLayout;
import mayday.core.structures.trees.tree.Node;



public class LabelInside extends LabelWithAngle {
	
	public void paint(Node n, Graphics g, ScreenLayout l, boolean selected){
		NodeLayout nlayout = (NodeLayout) l.getLayout(n);
        Graphics2D g2 = ((Graphics2D)g);
        if (l.getLabel(n).trim().length()>0 && nlayout.labelVisible()) {
        	g2.setColor(Color.white);
        	g2.setBackground(getColor(n,nlayout));
        } else {
        	g2.setColor(getColor(n,nlayout));
        }
        getNodeShape(n, l).paintNodeShape(g2, selected);
	}

	public String toString() {
		return "Labels in Nodes";
	}
	
	protected NodeShape getNodeShape(double x, double y, Node n, NodeLayout nl, ScreenLayout sl, double angle) {
		if (sl.getLabel(n).trim().length()>0 && nl.labelVisible())
			return new InsideNodeShape(x,y,sl.getLabel(n),nl,angle);
		else
			return new QuadraticNodeShape(x,y,sl.getLabel(n),nl); 		
	}

	@SuppressWarnings("serial")
	public class InsideNodeShape extends AngledLabelNodeShape {

		public InsideNodeShape(double X, double Y, String label, NodeLayout nl, double Angle) {
			super(X,Y,label,nl,-90d);
			fillBox = true;
		}

		protected void produceTransform() {
			super.produceTransform();
			// move label right
			at.translate(w/2,0 );
		}
		
	}
}
