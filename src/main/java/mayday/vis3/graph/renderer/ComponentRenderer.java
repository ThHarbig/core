package mayday.vis3.graph.renderer;

import java.awt.Dimension;
import java.awt.Graphics2D;
import java.awt.Rectangle;

import mayday.core.structures.graph.Node;
import mayday.vis3.graph.components.LabelRenderer.Orientation;

/**
 * Interface of all Renderers.
 * @author Stephan Symons
 *
 */
public interface ComponentRenderer 
{
	/**
	 * Renders the component
	 * @param g The graphics context of the component
	 * @param bounds The bounds to draw in
	 * @param value The object to be rendered
	 * @param label The label to draw 
	 * @param selected Whether the component is selected  
	 */
	public void draw(Graphics2D g, Node node, Rectangle bounds, Object value, String label, boolean selected);
	
	
	/**
	 * @param node The node to be displayed, or null
	 * @param value The value to be displayed, or null
	 * @return The optiomal size for the node. 
	 */
	public Dimension getSuggestedSize(Node node, Object value); 
	
	public Orientation getLabelOrientation(Node node, Object value);
	
	public boolean hasLabel(Node node, Object value);


}
