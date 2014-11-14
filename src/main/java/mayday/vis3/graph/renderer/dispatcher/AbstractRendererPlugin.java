package mayday.vis3.graph.renderer.dispatcher;

import java.awt.Dimension;
import java.awt.Graphics2D;
import java.awt.Rectangle;

import mayday.core.pluma.AbstractPlugin;
import mayday.core.structures.graph.Node;
import mayday.vis3.graph.vis3.SuperColorProvider;

public abstract class AbstractRendererPlugin extends AbstractPlugin implements RendererPlugin 
{
	public static final String MC="GraphViewer/ComponentRenderers";
	public static final String MC_DECORATOR="ComponentRenderer Decorator";
	
	public static final String GROUP_PRIMARY="Primary Renderers";
	public static final String GROUP_DECORATORS="Renderer Decorators";

	

	@Override
	public abstract void draw(Graphics2D g, Node node, Rectangle bounds, Object value, String label, boolean selected);

	@Override
	public abstract  Dimension getSuggestedSize(Node node, Object value);
	
	@Override
	public void init() {}
	
	public abstract void setColorProvider(SuperColorProvider coloring);
	

}
