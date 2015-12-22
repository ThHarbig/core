package mayday.vis3.graph.renderer;

import java.awt.Graphics2D;
import java.awt.Rectangle;

import mayday.core.Probe;
import mayday.core.structures.graph.Node;
import mayday.vis3.graph.components.LabelRenderer.Orientation;
import mayday.vis3.graph.renderer.dispatcher.AbstractRendererPlugin;

public abstract class AbstractComponentRenderer extends AbstractRendererPlugin  implements ComponentRenderer
{

	/* (non-Javadoc)
	 * @see mayday.vis3.graph.renderer.ComponentRenderer#draw(java.awt.Graphics2D, java.awt.Rectangle, java.lang.Object, java.lang.String, boolean)
	 */
	@SuppressWarnings("unchecked")
	public void draw(Graphics2D g, Node node, Rectangle bounds, Object value,
			String label, boolean selected) 
	{
		if(value instanceof String)
		{
			drawString(g, bounds, selected, (String)value);
			return;
		}

		if(value instanceof Double)
		{
			double d= ((Double)value).doubleValue();
			drawDouble(g, bounds, label, selected, d);
			return;
		}

		if(value instanceof double[])
		{
			drawDouble(g, bounds, label, selected, (double[])value);
			return;
		}

		if(value instanceof Probe)
		{
			drawProbe(g, bounds, label, selected, (Probe)value);
			return;
		}

		if(value instanceof Iterable)
		{
			if( !((Iterable)value).iterator().hasNext() ){
				drawString(g, bounds, selected, node.getName());
				return;
			}else{
				drawProbes(g, bounds, label, selected, (Iterable<Probe>)value);
				return;
			}
		}
		drawObject(g, bounds, label, selected, value);

	}

	public abstract void drawString(Graphics2D g, Rectangle bounds, boolean selected, String value);

	public abstract void drawDouble(Graphics2D g, Rectangle bounds, String label, boolean selected, double... value);

	public abstract void drawProbe(Graphics2D g, Rectangle bounds, String label, boolean selected, Probe value);

	public abstract void drawProbes(Graphics2D g, Rectangle bounds, String label, boolean selected, Iterable<Probe> value);

	public abstract void drawNode(Graphics2D g, Rectangle bounds, String label, boolean selected, Node node);

	public abstract void drawObject(Graphics2D g, Rectangle bounds, String label, boolean selected, Object value);

	@Override
	public Orientation getLabelOrientation(Node node, Object value) 
	{
		return null;
	}

	@Override
	public boolean hasLabel(Node node, Object value) 
	{
		return true;
	}
}
