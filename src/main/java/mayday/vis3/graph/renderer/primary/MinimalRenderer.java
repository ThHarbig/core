package mayday.vis3.graph.renderer.primary;

import java.awt.Graphics2D;
import java.awt.Rectangle;

import mayday.core.pluma.PluginManager.IGNORE_PLUGIN;
import mayday.core.structures.graph.Node;
import mayday.vis3.graph.renderer.RendererTools;

@IGNORE_PLUGIN
public class MinimalRenderer extends DefaultComponentRenderer
{
	public static final MinimalRenderer sharedInstance=new MinimalRenderer();
	
	@Override
	public void draw(Graphics2D g, Node node, Rectangle bounds, Object value, String label, boolean selected) 
	{
		RendererTools.fillEllipse(g, bounds, selected);
	}
	
	@Override
	public boolean hasLabel(Node node, Object value) 
	{
		return false;
	}
	
	
}
