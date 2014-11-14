package mayday.core.structures.trees.painter;

import java.awt.Graphics;

import mayday.core.structures.trees.screen.ScreenLayout;
import mayday.core.structures.trees.tree.ITreePart;


/**
 * Interface representing Node- and EdgePainters
 * @author Andreas Friedrich
 *
 * @param <A> Edge or Node
 */
public interface IPainter<A extends ITreePart> {

	/**
	 * paints one TreePart
	 * @param a Edge or Node to paint
	 * @param g inherited Graphics object
	 * @param l Layout to get coordinates from
	 * @param selected boolean showing if this TreePart is selected
	 * @see @Layout @IEdgePainter @INodePainter
	 */
	public void paint(A a, Graphics g, ScreenLayout l, boolean selected);
	
	/**
	 * Computes the distances between a point and a TreePart
	 * @param a Edge or Node to compute distance to
	 * @param sl ScreenLayout providing the needed coordinates
	 * @param x x coordinate of the point
	 * @param y y coordinate of the point
	 * @return distance between point and Node
	 * @see @Layout
	 */
	public double distance(A a,  ScreenLayout sl, int x, int y);
	
}
