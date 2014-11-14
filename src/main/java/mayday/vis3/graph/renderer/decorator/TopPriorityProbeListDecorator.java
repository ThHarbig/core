package mayday.vis3.graph.renderer.decorator;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import mayday.core.Probe;
import mayday.core.pluma.PluginInfo;
import mayday.core.pluma.PluginManagerException;
import mayday.core.structures.graph.Node;
import mayday.vis3.graph.renderer.RendererDecorator;
import mayday.vis3.graph.renderer.RendererTools;

public class TopPriorityProbeListDecorator  extends RendererDecorator
{


	@SuppressWarnings("unchecked")
	@Override
	public void draw(Graphics2D g, Node node, Rectangle bounds, Object value,
			String label, boolean selected) 
	{		
		boolean renderNormally=false;
		List<Color> colors=new ArrayList<Color>();
		if(value instanceof Probe)
		{
			colors.add(((Probe)value).getProbeLists().get(0).getColor());
		}
		if(value instanceof Iterable)
		{
			for(Probe p:(Iterable<Probe>)value)
			{
				colors.add(p.getProbeLists().get(0).getColor());
			}					
		}
		if(colors.isEmpty())
			renderNormally=true;
		else
		{
			Rectangle r=new Rectangle(bounds.x,bounds.y,bounds.width, bounds.height-10);
			getRenderer().draw(g, node, r, value, label, selected);
			r=new Rectangle(bounds.x,bounds.height-10,bounds.width, 10);
			RendererTools.drawColorLine(g, colors, r);
		}

		if(renderNormally)
		{
			getRenderer().draw(g, node, bounds, value, label, selected);
		}

	}


	@Override
	public Dimension getSuggestedSize(Node node, Object value) 
	{
		int h=getRenderer().getSuggestedSize(node,value).height+10;
		return new Dimension(getRenderer().getSuggestedSize(node,value).width,h);
	}

	@Override
	public PluginInfo register() throws PluginManagerException 
	{
		PluginInfo pli = new PluginInfo(
				this.getClass(),
				"PAS.GraphViewer.RendererDecorator.TopPriorityPL",
				new String[]{},
				MC_DECORATOR,
				new HashMap<String, Object>(),
				"Stephan Symons",
				"symons@informatik.uni-tuebingen.de",
				"adds a color bar displaying the top priority probe lists ",
				"Top Priority ProbeList"				
		);
		pli.addCategory(GROUP_DECORATORS);
		return pli;	
	}
}
