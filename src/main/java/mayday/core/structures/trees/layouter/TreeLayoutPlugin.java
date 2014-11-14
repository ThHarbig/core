package mayday.core.structures.trees.layouter;

import mayday.core.pluma.AbstractPlugin;
import mayday.core.structures.trees.layout.Layout;
import mayday.core.structures.trees.tree.Node;

public abstract class TreeLayoutPlugin extends AbstractPlugin  {
	
	protected boolean ignoreEdgeLengths = false;
	
	public void setIgnoreEdgeLengths(boolean ignoreEdgeLengths) {
		this.ignoreEdgeLengths=ignoreEdgeLengths;
	}
	
	public final static String MC = "Visualization Plot/Tree Layout Algorithm";
	
	public abstract Layout doLayout(Node root);
}
