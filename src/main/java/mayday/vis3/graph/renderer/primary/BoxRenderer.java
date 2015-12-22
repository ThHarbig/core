package mayday.vis3.graph.renderer.primary;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.awt.geom.Rectangle2D;
import java.util.HashMap;

import mayday.core.pluma.PluginInfo;
import mayday.core.pluma.PluginManagerException;
import mayday.core.structures.graph.Node;
import mayday.vis3.graph.vis3.SuperColorProvider;

public class BoxRenderer  extends ShapeRenderer
{
	public BoxRenderer()	
	{
		super();
	}
	
	public BoxRenderer(Color col)	
	{
		super(col);
	}
	
	public BoxRenderer(SuperColorProvider coloring)
	{
		super(coloring);		
	}
	
	public void draw(Graphics2D g, Node node, Rectangle bounds, Object value,String label, boolean selected) 
	{
		Rectangle2D p=new Rectangle2D.Double(bounds.getMinX(),bounds.getMinY(),bounds.getMaxX()-1, bounds.getMaxY()-1);
		draw(g,value,selected,p);			
	}

	@Override
	public PluginInfo register() throws PluginManagerException 
	{
		PluginInfo pli = new PluginInfo(
				this.getClass(),
				"PAS.GraphViewer.Renderer.Box",
				new String[]{},
				MC,
				new HashMap<String, Object>(),
				"Stephan Symons",
				"symons@informatik.uni-tuebingen.de",
				"Renders every node as a box",
				"Box Renderer"				
		);
		pli.addCategory(GROUP_PRIMARY);
		return pli;	
	}
}
