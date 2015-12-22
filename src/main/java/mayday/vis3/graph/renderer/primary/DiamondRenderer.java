package mayday.vis3.graph.renderer.primary;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Polygon;
import java.awt.Rectangle;
import java.util.HashMap;

import mayday.core.pluma.PluginInfo;
import mayday.core.pluma.PluginManagerException;
import mayday.core.structures.graph.Node;
import mayday.vis3.graph.vis3.SuperColorProvider;

public class DiamondRenderer  extends ShapeRenderer
{
	public DiamondRenderer()
	{	
		super();
	}
	
	public DiamondRenderer(Color col)
	{	
		super(col);
	}
	
	public DiamondRenderer(SuperColorProvider coloring)
	{
		this.colorProvider=coloring;		
	}
	
	public void draw(Graphics2D g, Node node, Rectangle bounds, Object value,String label, boolean selected) 
	{
		Polygon p=new Polygon();
		p.addPoint((int)bounds.getCenterX(),(int)bounds.getMinY());
		p.addPoint((int)bounds.getMaxX(),(int)bounds.getCenterY());
		p.addPoint((int)bounds.getCenterX(),(int)bounds.getMaxY());
		p.addPoint((int)bounds.getMinX(),(int)bounds.getCenterY());
		p.addPoint((int)bounds.getCenterX(),(int)bounds.getMinY());
		
		draw(g,value,selected,p);		
	}

	@Override
	public PluginInfo register() throws PluginManagerException 
	{
		PluginInfo pli = new PluginInfo(
				this.getClass(),
				"PAS.GraphViewer.Renderer.Diamond",
				new String[]{},
				MC,
				new HashMap<String, Object>(),
				"Stephan Symons",
				"symons@informatik.uni-tuebingen.de",
				"Render everything as a diamond",
				"Diamond Renderer"				
		);
		pli.addCategory(GROUP_PRIMARY);
		return pli;	
	}
}
