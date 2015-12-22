package mayday.vis3.graph.renderer.primary;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Polygon;
import java.awt.Rectangle;
import java.util.HashMap;

import mayday.core.pluma.PluginInfo;
import mayday.core.pluma.PluginManagerException;
import mayday.core.structures.graph.Node;
import mayday.vis3.graph.renderer.RendererTools;
import mayday.vis3.graph.vis3.SuperColorProvider;

public class StarRenderer extends ShapeRenderer
{
	public StarRenderer() 
	{
		super();
	}
	
	public StarRenderer(Color col) 
	{
		super(col);
	}
	
	public StarRenderer(SuperColorProvider coloring)
	{
		super(coloring);
	}
	
	public void draw(Graphics2D g, Node node, Rectangle bounds, Object value,String label, boolean selected) 
	{
//		int r=Math.min(bounds.width, bounds.height)-1;
//		Polygon p=new Polygon();
//	
//		double r0=r*0.5;
//		double r1=0.5*r0;
//		int rc=(bounds.width-r)/2;		
//		for(int i=0; i!=10; ++i)
//		{
//			double phi=i*(Math.PI*2)/10;
//			if(i%2==0)
//			{
//				double x= 0*Math.cos(phi) - -r0*Math.sin(phi);
//				double y= 0*Math.sin(phi) + -r0*Math.cos(phi);
//				p.addPoint((int)(x+r0+rc),(int)(y+r0));
//			}else
//			{
//				double x= 0*Math.cos(phi) - -r1*Math.sin(phi);
//				double y= 0*Math.sin(phi) + -r1*Math.cos(phi);
//				p.addPoint((int)(x+r0+rc),(int)(y+r0));
//			}			
//		}
		Polygon p=RendererTools.drawStar(bounds);
		draw(g,value,selected,p);		
	}

	@Override
	public PluginInfo register() throws PluginManagerException 
	{
		PluginInfo pli = new PluginInfo(
				this.getClass(),
				"PAS.GraphViewer.Renderer.Star",
				new String[]{},
				MC,
				new HashMap<String, Object>(),
				"Stephan Symons",
				"symons@informatik.uni-tuebingen.de",
				"Render nodes as a star.",
				"Star Renderer"				
		);
		pli.addCategory(GROUP_PRIMARY);
		return pli;	
	}	
}
