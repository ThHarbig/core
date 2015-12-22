package mayday.core.structures.trees.painter.node;

import mayday.core.structures.trees.layout.NodeLayout;
import mayday.core.structures.trees.painter.NodeShape;
import mayday.core.structures.trees.screen.ScreenLayout;
import mayday.core.structures.trees.tree.Node;



public class LabelBelow extends LabelWithAngle {


	public String toString() {
		return "Labels below nodes";
	}

	protected NodeShape getNodeShape(double x, double y, Node n, NodeLayout nl, ScreenLayout sl, double angle) {
		if (sl.getLabel(n).trim().length()>0 && nl.labelVisible())
			return new LabelNodeShape(x,y,sl.getLabel(n),nl,angle);
		else
			return new QuadraticNodeShape(x,y,sl.getLabel(n),nl); 		
	}

	@SuppressWarnings("serial")
	public class LabelNodeShape extends AngledLabelNodeShape {

		public LabelNodeShape(double X, double Y, String label, NodeLayout nl, double Angle) {
			super(X,Y,label,nl,-90d);
		}

		protected void produceTransform() {
			super.produceTransform();
			// move label downwards by height and right
			at.translate(w/2, h/2);
		}
	}
}
