package mayday.vis3.graph.renderer.primary;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.awt.geom.Ellipse2D;
import java.util.HashMap;

import mayday.core.pluma.PluginInfo;
import mayday.core.pluma.PluginManagerException;
import mayday.core.structures.graph.Node;
import mayday.vis3.graph.vis3.SuperColorProvider;

public class CircleRenderer extends ShapeRenderer
{

	public CircleRenderer() 
	{
		super();
	}
	
	public CircleRenderer(Color col) 
	{
		super(col);
	}
	
	public CircleRenderer(SuperColorProvider coloring)
	{
		super(coloring);
	}
	
	public void draw(Graphics2D g, Node node, Rectangle bounds, Object value,String label, boolean selected) 
	{
		int r=Math.min(bounds.width, bounds.height)-1;
		Ellipse2D p=new Ellipse2D.Double((bounds.width-r)/2, 0,r, r);		
		draw(g,value,selected,p);		
	}

	@Override
	public PluginInfo register() throws PluginManagerException 
	{
		PluginInfo pli = new PluginInfo(
				this.getClass(),
				"PAS.GraphViewer.Renderer.Circle",
				new String[]{},
				MC,
				new HashMap<String, Object>(),
				"Stephan Symons",
				"symons@informatik.uni-tuebingen.de",
				"Render everything as a circle",
				"Circle Renderer"				
		);
		pli.addCategory(GROUP_PRIMARY);
		return pli;	
	}	

}
