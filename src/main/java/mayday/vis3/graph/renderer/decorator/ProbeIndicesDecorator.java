package mayday.vis3.graph.renderer.decorator;

import java.awt.Dimension;
import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.util.HashMap;

import mayday.core.pluma.PluginInfo;
import mayday.core.pluma.PluginManagerException;
import mayday.core.structures.graph.Node;
import mayday.vis3.graph.renderer.ComponentRenderer;
import mayday.vis3.graph.renderer.RendererDecorator;
import mayday.vis3.graph.renderer.primary.HeatMapRenderer;

public class ProbeIndicesDecorator extends RendererDecorator
{
	public ProbeIndicesDecorator(ComponentRenderer renderer)
	{
		super(renderer);
	}
	
	public ProbeIndicesDecorator() 
	{
		
	}
	
	@SuppressWarnings("unchecked")
	@Override
	public void draw(Graphics2D g, Node node,Rectangle bounds, Object value, String label, boolean selected) 
	{

		
		if(getRenderer() instanceof HeatMapRenderer)
		{

			if(value instanceof Iterable)
			{
				Rectangle r=new Rectangle(bounds.x+10,bounds.y,bounds.width-10, bounds.height);
				getRenderer().draw(g, node, r, value, label, selected);
				
				int numProbes=0;
				for(@SuppressWarnings("unused") Object o: ((Iterable)value))
				{
					numProbes++;
				}
				int dy=(int) (bounds.getHeight() / numProbes);		
				for(int i=0;i!=numProbes+1;++i)
				{					
					g.drawLine(0, i*dy, 5, i*dy);
				}	
			}
		}else
		{
			getRenderer().draw(g, node, bounds, value, label, selected);
		}
	}
	
	public Dimension getSuggestedSize(Node node, Object value) 
	{
		if(getRenderer() instanceof HeatMapRenderer)
		{
			int w=getRenderer().getSuggestedSize(node,value).width+10;
			return new Dimension(w,getRenderer().getSuggestedSize(node,value).height);	
		}else
		{
			return getRenderer().getSuggestedSize(node,value);
		}
	}
	
	@Override
	public PluginInfo register() throws PluginManagerException 
	{
		PluginInfo pli = new PluginInfo(
				this.getClass(),
				"PAS.GraphViewer.RendererDecorator.ProbeIndex",
				new String[]{},
				MC_DECORATOR,
				new HashMap<String, Object>(),
				"Stephan Symons",
				"symons@informatik.uni-tuebingen.de",
				"Display Probe Indices",
				"Probe Indices"				
		);
		pli.addCategory(GROUP_DECORATORS);
		return pli;	
	}
	
}
