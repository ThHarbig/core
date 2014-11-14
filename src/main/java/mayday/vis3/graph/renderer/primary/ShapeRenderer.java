package mayday.vis3.graph.renderer.primary;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics2D;
import java.awt.Shape;

import mayday.core.Probe;
import mayday.core.settings.Setting;
import mayday.core.settings.generic.BooleanHierarchicalSetting;
import mayday.core.settings.generic.HierarchicalSetting;
import mayday.core.settings.typed.BooleanSetting;
import mayday.core.settings.typed.ColorSetting;
import mayday.core.structures.graph.Node;
import mayday.vis3.graph.vis3.SuperColorProvider;

public abstract class ShapeRenderer extends DefaultComponentRenderer
{
	protected SuperColorProvider colorProvider;	

	protected BooleanHierarchicalSetting fill=new BooleanHierarchicalSetting("Fill", null, true);
	protected ColorSetting fillColor=new ColorSetting("Color", "The default fill color", Color.blue);
	protected BooleanHierarchicalSetting outline=new BooleanHierarchicalSetting("Draw Outline", null, true);
	protected ColorSetting outlineColor=new ColorSetting("Color", "The default outline color", Color.black);
	protected BooleanSetting useDefaultColor=new BooleanSetting("Single color", "do not use values for coloring", false);


	public ShapeRenderer()
	{
		fill.addSetting(fillColor);
		outline.addSetting(outlineColor);
	}

	public ShapeRenderer(Color col) 
	{
		this();
		fillColor.setColorValue(col);
	}

	public ShapeRenderer(SuperColorProvider coloring)
	{
		this();
		this.colorProvider=coloring;		
	}

	/**
	 * @return the color
	 */
	public Color getColor() {
		return fillColor.getColorValue();
	}

	/**
	 * @param color the color to set
	 */
	public void setColor(Color color) {
		this.fillColor.setColorValue(color);
	}

	@Override	
	public Dimension getSuggestedSize() 
	{
		return new Dimension(15,15);
	}

	@Override
	public Dimension getSuggestedSize(Node node, Object value) 
	{
		return new Dimension(15,15);
	}

	@Override
	public Setting getSetting() 
	{
		HierarchicalSetting res=new HierarchicalSetting("Renderer Settings");
		res.addSetting(fill).addSetting(outline).addSetting(useDefaultColor);
		return res;
	}

	public void setColorProvider(SuperColorProvider colorProvider) 
	{
		this.colorProvider = colorProvider;
	}

	@SuppressWarnings("unchecked")
	public void draw(Graphics2D g, Object value, boolean selected, Shape shape)
	{
		if(fill.getBooleanValue())
		{
			if(useDefaultColor.getBooleanValue())
			{
				g.setColor(fillColor.getColorValue());
				g.fill(shape);				
			}else
			{
				if(value instanceof Probe) 
				{
					Color c=colorProvider.getColor((Probe)value);
					g.setColor(c);
					g.fill(shape);
				}else
				{
					if(value instanceof Iterable && ((Iterable)value).iterator().hasNext()) {					
						g.setColor(colorProvider.getMeanColor((Iterable<Probe>) value));
						g.fill(shape);
					}else {				
						g.setColor(fillColor.getColorValue());
						g.fill(shape);
					}
				}
			}
		}
		if(selected)
		{
			g.setColor(Color.red);
			g.setStroke(new BasicStroke(4.0f));
			g.draw(shape);		
		}else
		{
			if(outline.getBooleanValue())
			{
				g.setColor(outlineColor.getColorValue());
				g.draw(shape);
			}
		}
	}



}
