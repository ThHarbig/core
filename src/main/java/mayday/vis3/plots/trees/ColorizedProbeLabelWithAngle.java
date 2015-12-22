package mayday.vis3.plots.trees;

import java.awt.Color;

import mayday.core.Probe;
import mayday.core.structures.trees.layout.NodeLayout;
import mayday.core.structures.trees.layout.ObjectMapper;
import mayday.core.structures.trees.painter.node.LabelWithAngle;
import mayday.core.structures.trees.tree.Node;
import mayday.vis3.ColorProvider;

public class ColorizedProbeLabelWithAngle extends LabelWithAngle {
	
	protected ColorProvider cp;
	protected ObjectMapper mpr;
	
	public ColorizedProbeLabelWithAngle(ColorProvider cp, ObjectMapper mpr) {
		this.cp=cp;
		this.mpr=mpr;
	}
	
	@Override
	protected Color getColor(Node n, NodeLayout nl) {
		Color c = getColor0(n,nl); 
		return c;
	}
	
	protected Color getColor0(Node n, NodeLayout nl) {
		if (cp!=null && mpr!=null) {
			Probe pb = (Probe)mpr.getObject(n);
			if (pb!=null) {
				Color c = cp.getColor(pb);
				if (c!=null)
					return c;
			}
		}
		return nl.getColor();
	}
	
	public String toString() {
		return "Colorized Angled Nodes";
	}
	
}
