package mayday.vis3.graph.renderer.decorator;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.util.HashMap;

import mayday.core.Probe;
import mayday.core.pluma.PluginInfo;
import mayday.core.pluma.PluginManagerException;
import mayday.core.structures.graph.Node;
import mayday.vis3.graph.renderer.ComponentRenderer;
import mayday.vis3.graph.renderer.RendererDecorator;
import mayday.vis3.graph.renderer.RendererTools;

public class ExperimentIndicesDecorator extends RendererDecorator
{
	public ExperimentIndicesDecorator()
	{
		super();
	}	
	
	public ExperimentIndicesDecorator(ComponentRenderer renderer)
	{
		super(renderer);
	}
	
	@SuppressWarnings("unchecked")
	@Override
	public void draw(Graphics2D g, Node node,Rectangle bounds, Object value,
			String label, boolean selected) 
	{
		
		Rectangle r=new Rectangle(bounds.x,bounds.y,bounds.width, bounds.height-5);
		getRenderer().draw(g, node, r, value, label, selected);
		int idx=0;
		if(value instanceof Probe)
		{
			idx=((Probe)value).getNumberOfExperiments();
		}
		
		if(value instanceof Iterable)
		{
			if( ((Iterable<Probe>)value).iterator().hasNext())
				idx=((Iterable<Probe>)value).iterator().next().getNumberOfExperiments();			
		}		
		
		Rectangle r2=new Rectangle(bounds.x,bounds.height-5,bounds.width, 10);	
		g.setColor(Color.black);
		RendererTools.drawIndexLine(g, idx, r2);
		
	}
	
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
				"PAS.GraphViewer.RendererDecorator.Indices",
				new String[]{},
				MC_DECORATOR,
				new HashMap<String, Object>(),
				"Stephan Symons",
				"symons@informatik.uni-tuebingen.de",
				"Show tickmarks for experiments",
				"Experiment indices"				
		);
		pli.addCategory(GROUP_DECORATORS);
		return pli;	
	}
	

}
