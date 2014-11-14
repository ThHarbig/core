package mayday.vis3.graph.renderer.primary;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Polygon;
import java.awt.Rectangle;
import java.util.HashMap;

import mayday.core.pluma.PluginInfo;
import mayday.core.pluma.PluginManagerException;
import mayday.core.settings.Setting;
import mayday.core.settings.generic.HierarchicalSetting;
import mayday.core.settings.typed.IntSetting;
import mayday.core.settings.typed.IntSetting.LayoutStyle;
import mayday.core.structures.graph.Node;
import mayday.vis3.graph.renderer.RendererTools;
import mayday.vis3.graph.vis3.SuperColorProvider;

public class PolygonRenderer extends ShapeRenderer
{
	private IntSetting sides=new IntSetting("Number of sides", null, 3, 3, 32, true, true);
	
	public PolygonRenderer() 
	{
		super();
	}
	
	public PolygonRenderer(Color col) 
	{
		super(col);
	}
	
	public PolygonRenderer(SuperColorProvider coloring)
	{
		super(coloring);
	}
	
	public void draw(Graphics2D g, Node node, Rectangle bounds, Object value,String label, boolean selected) 
	{
		Polygon p=RendererTools.drawPolygon(bounds, sides.getIntValue());		
		draw(g,value,selected,p);		
	}
	
	@Override
	public Setting getSetting() 
	{
		HierarchicalSetting res=(HierarchicalSetting)super.getSetting();
		sides.setLayoutStyle(LayoutStyle.SLIDER);
		res.addSetting(sides);
		return res;
	}

	@Override
	public PluginInfo register() throws PluginManagerException 
	{
		PluginInfo pli = new PluginInfo(
				this.getClass(),
				"PAS.GraphViewer.Renderer.Polygon",
				new String[]{},
				MC,
				new HashMap<String, Object>(),
				"Stephan Symons",
				"symons@informatik.uni-tuebingen.de",
				"Render nodes as polygons.",
				"Polygon Renderer"				
		);
		pli.addCategory(GROUP_PRIMARY);
		return pli;	
	}	
}
