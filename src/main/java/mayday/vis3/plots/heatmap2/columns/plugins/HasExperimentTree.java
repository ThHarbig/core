package mayday.vis3.plots.heatmap2.columns.plugins;

import javax.swing.event.ChangeListener;

import mayday.core.structures.trees.tree.Node;

public interface HasExperimentTree {
	
	public Node getTree();
	
	public void addTreeChangeListener(ChangeListener cl);

}
