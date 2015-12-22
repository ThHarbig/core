package mayday.core.structures.trees.painter;

import java.awt.geom.Rectangle2D;

import mayday.core.structures.trees.screen.ScreenLayout;
import mayday.core.structures.trees.tree.Node;

/**
 * Uses a Layout and Node to paint this Node
 * 
 * @author Andreas Friedrich
 *
 */
public interface INodePainter extends IPainter<Node> {
	
	public Rectangle2D getNodeBounds(Node n, ScreenLayout l);
	
}
