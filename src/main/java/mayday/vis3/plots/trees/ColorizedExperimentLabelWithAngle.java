package mayday.vis3.plots.trees;

import java.awt.Color;

import mayday.core.Experiment;
import mayday.core.structures.trees.layout.NodeLayout;
import mayday.core.structures.trees.layout.ObjectMapper;
import mayday.core.structures.trees.painter.node.LabelWithAngle;
import mayday.core.structures.trees.tree.Node;
import mayday.vis3.categorical.ClassSelectionColoring;

public class ColorizedExperimentLabelWithAngle extends LabelWithAngle {
	
	protected ClassSelectionColoring csc;
	protected ObjectMapper mpr;
	
	public ColorizedExperimentLabelWithAngle(ClassSelectionColoring csc, ObjectMapper mpr) {
		this.csc=csc;
		this.mpr=mpr;
	}
	
	@Override
	protected Color getColor(Node n, NodeLayout nl) {
		Color c = getColor0(n,nl); 
		return c;
	}
	
	protected Color getColor0(Node n, NodeLayout nl) {
		if (csc!=null && mpr!=null) {
			Experiment e = (Experiment)mpr.getObject(n);			
			if (e!=null) {
				Color c = csc.getColorForObject(e.getName());
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
