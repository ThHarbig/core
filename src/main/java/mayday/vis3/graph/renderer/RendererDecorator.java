package mayday.vis3.graph.renderer;

import java.awt.Graphics2D;
import java.awt.Rectangle;

import mayday.core.DataSet;
import mayday.core.structures.graph.Node;
import mayday.vis3.graph.components.LabelRenderer.Orientation;
import mayday.vis3.graph.renderer.dispatcher.AbstractRendererPlugin;
import mayday.vis3.graph.vis3.SuperColorProvider;

public abstract class RendererDecorator extends AbstractRendererPlugin implements ComponentRenderer
{
	private ComponentRenderer renderer;
	
	public RendererDecorator()
	{		
	}
	
	public RendererDecorator(ComponentRenderer renderer)
	{
		this.renderer=renderer;
	}
	
	/**
	 * @return the renderer
	 */
	public ComponentRenderer getRenderer() {
		return renderer;
	}

	/**
	 * @param renderer the renderer to set
	 */
	public void setRenderer(ComponentRenderer renderer) {
		this.renderer = renderer;
	}


	public abstract void draw(Graphics2D g, Node node, Rectangle bounds, Object value,
			String label, boolean selected);
	
	public void setDataSet(DataSet ds)
	{
		// well.... do nothing, i guess.
	}
	
	@Override
	public Orientation getLabelOrientation(Node node, Object value) 
	{
		return renderer.getLabelOrientation(node,value);
	}
	
	@Override
	public boolean hasLabel(Node node, Object value) 
	{
		return renderer.hasLabel(node,value);
	}
	
	@Override
	public void setColorProvider(SuperColorProvider coloring)
	{
		if(renderer instanceof RendererDecorator)
		{
			((RendererDecorator)renderer).setColorProvider(coloring);
		}
		if(renderer instanceof AbstractComponentRenderer)
		{
			((AbstractComponentRenderer) renderer).setColorProvider(coloring);
		}
	}

}
