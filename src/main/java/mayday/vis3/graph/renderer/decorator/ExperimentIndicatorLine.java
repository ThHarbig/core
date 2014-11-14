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

public class ExperimentIndicatorLine extends RendererDecorator{

	private HierarchicalSetting setting;
	private ColorSetting color;
	private SuperColorProvider coloring;
	
	public ExperimentIndicatorLine()
	{
		super();
		color=new ColorSetting("Indicator Color", null, Color.blue);
		setting=new HierarchicalSetting("Settings");
		setting.addSetting(color);
	}
	
	public ExperimentIndicatorLine(ComponentRenderer renderer)
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
		getRenderer().draw(g, node, bounds, value, label, selected);
		g.setColor(Color.black);
		// brew color:
		Color co=color.getColorValue();
		Color c=new Color(co.getRed(), co.getGreen(), co.getBlue(), 100);
		// render:
		RendererTools.drawHighlightLine(g, 
				coloring.getViewModel().getDataSet().getMasterTable().getNumberOfExperiments(), 
				c, 
				coloring.getExperiment(), 
				bounds);
		
	}
	
	public Dimension getSuggestedSize(Node node, Object value) 
	{
		return getRenderer().getSuggestedSize(node,value);	
	}
	
	@Override
	public PluginInfo register() throws PluginManagerException 
	{
		PluginInfo pli = new PluginInfo(
				this.getClass(),
				"PAS.GraphViewer.RendererDecorator.ExperimentIndicatorLine",
				new String[]{},
				MC_DECORATOR,
				new HashMap<String, Object>(),
				"Stephan Symons",
				"symons@informatik.uni-tuebingen.de",
				"Highlight the selected experiment",
				"Indicate Experiment (Line)"				
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
