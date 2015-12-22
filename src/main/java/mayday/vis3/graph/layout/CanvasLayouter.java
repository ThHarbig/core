package mayday.vis3.graph.layout;

import java.awt.Container;
import java.awt.Rectangle;

import mayday.vis3.graph.model.GraphModel;

/**
 * Interface for a layouter that layouts the graph in the models. 
 * @author Stephan Symons
 *
 */
public interface CanvasLayouter 
{
	/**
	 * Does the layout.
	 * @param container
	 * @param bounds
	 * @param model
	 */
	public void layout(Container container, Rectangle bounds, GraphModel model);
		
}
