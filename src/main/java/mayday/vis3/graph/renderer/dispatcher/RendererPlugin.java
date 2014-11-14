package mayday.vis3.graph.renderer.dispatcher;

import java.awt.Dimension;
import java.awt.Graphics2D;
import java.awt.Rectangle;

import mayday.core.structures.graph.Node;
import mayday.vis3.graph.renderer.ComponentRenderer;

public interface RendererPlugin extends ComponentRenderer
{
	public abstract void draw(Graphics2D g, Node node, Rectangle bounds, Object value, String label, boolean selected);
	
	public abstract Dimension getSuggestedSize(Node node, Object value);
	
}
