package mayday.vis3.graph.renderer.decorator;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.util.HashMap;

import mayday.core.pluma.PluginInfo;
import mayday.core.pluma.PluginManagerException;
import mayday.core.settings.Setting;
import mayday.core.settings.generic.HierarchicalSetting;
import mayday.core.settings.typed.ColorSetting;
import mayday.core.structures.graph.Node;
import mayday.vis3.graph.renderer.ComponentRenderer;
import mayday.vis3.graph.renderer.RendererDecorator;
import mayday.vis3.graph.renderer.RendererTools;
import mayday.vis3.graph.vis3.SuperColorProvider;

public class ExperimentIndicator extends RendererDecorator
{
	private HierarchicalSetting setting;
	private ColorSetting color;
	private SuperColorProvider coloring;
	
	
	public ExperimentIndicator ()
	{
		super();
		color=new ColorSetting("Indicator Color", null, Color.blue);
		setting=new HierarchicalSetting("Settings");
		setting.addSetting(color);
	}
	
	public ExperimentIndicator (ComponentRenderer renderer)
	{
		super(renderer);
		color=new ColorSetting("Indicator Color", null, Color.blue);
		setting=new HierarchicalSetting("Settings");
		setting.addSetting(color);
	}
	
	@Override
	public void draw(Graphics2D g, Node node,Rectangle bounds, Object value,
			String label, boolean selected) 
	{

		if(coloring==null)
		{
			getRenderer().draw(g, node, bounds, value, label, selected);
			return;
		}
		Rectangle r=new Rectangle(bounds.x,bounds.y,bounds.width, bounds.height-5);
		getRenderer().draw(g, node, r, value, label, selected);
		
		Rectangle r2=new Rectangle(bounds.x,bounds.height-5,bounds.width, 10);	
		g.setColor(Color.black);
			
		RendererTools.drawHighlightLine(g, 
				coloring.getViewModel().getDataSet().getMasterTable().getNumberOfExperiments(), 
				color.getColorValue(), 
				coloring.getExperiment(), 
				Color.white, r2);
		
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
				"PAS.GraphViewer.RendererDecorator.ExperimentIndicator",
				new String[]{},
				MC_DECORATOR,
				new HashMap<String, Object>(),
				"Stephan Symons",
				"symons@informatik.uni-tuebingen.de",
				"Highlight the selected experiment",
				"Indicate Experiment"				
		);
		pli.addCategory(GROUP_DECORATORS);
		return pli;	
	}
	
	@Override
	public Setting getSetting() 
	{
		return setting;
	}
	
	@Override
	public void setColorProvider(SuperColorProvider coloring) 
	{
		this.coloring=coloring;
		super.setColorProvider(coloring);
	}
}
