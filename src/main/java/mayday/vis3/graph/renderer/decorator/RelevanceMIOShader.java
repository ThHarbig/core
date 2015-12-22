package mayday.vis3.graph.renderer.decorator;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import mayday.core.DataSet;
import mayday.core.Probe;
import mayday.core.math.Statistics;
import mayday.core.meta.MIManager;
import mayday.core.pluma.PluginInfo;
import mayday.core.pluma.PluginManagerException;
import mayday.core.settings.Setting;
import mayday.core.settings.generic.HierarchicalSetting;
import mayday.core.settings.typed.ColorSetting;
import mayday.core.structures.graph.Node;
import mayday.vis3.RelevanceSetting;
import mayday.vis3.graph.renderer.ComponentRenderer;
import mayday.vis3.graph.renderer.RendererDecorator;

public class RelevanceMIOShader extends RendererDecorator
{
	private RelevanceSetting relevanceSetting;
	private ColorSetting shadeColor=new ColorSetting("Shading Color",null,Color.white);
	private HierarchicalSetting setting;
	
	public RelevanceMIOShader() 
	{
		setting=new HierarchicalSetting("Relevance Shader Settings");		
	}
	
	public RelevanceMIOShader(ComponentRenderer renderer, RelevanceSetting relevanceSetting, Color shadeColor) 
	{
		super(renderer);
		this.relevanceSetting=relevanceSetting;
		this.shadeColor.setColorValue(shadeColor);
		setting.addSetting(relevanceSetting).addSetting(this.shadeColor);
	}
	
	@SuppressWarnings("unchecked")
	@Override
	public void draw(Graphics2D g, Node node, Rectangle bounds, Object value,
			String label, boolean selected) 
	{
		// render normally
		getRenderer().draw(g, node, bounds, value, label, selected);
		if(relevanceSetting==null)
			return;
		List<Double> values=new ArrayList<Double>();
		if(value instanceof Probe)
		{
			double d=relevanceSetting.getRelevance(value);
			values.add(d);
		}
		
		if(value instanceof Iterable)
		{
			for(Probe p:(Iterable<Probe>)value)
			{				
				values.add( relevanceSetting.getRelevance(p) );
			}					
		}
		double v=1.0f; // for complete transparency 
		if(!values.isEmpty())
		{
			v= (float)Statistics.mean(values);
		}
		
		Color color=new Color(
				shadeColor.getColorValue().getRed(),
				shadeColor.getColorValue().getGreen(),
				shadeColor.getColorValue().getBlue(),
				(int)(v*255) );
		g.setColor(color);
		g.fillRect(0, 0, bounds.width, bounds.height);
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
				"PAS.GraphViewer.RendererDecorator.RelevanceShader",
				new String[]{},
				MC_DECORATOR,
				new HashMap<String, Object>(),
				"Stephan Symons",
				"symons@informatik.uni-tuebingen.de",
				"Shades the componet according to its relevance",
				"Relevance Shading"				
		);
		pli.addCategory(GROUP_DECORATORS);
		return pli;	
	}
	
	@Override
	public Setting getSetting() 
	{
		return setting;
	}

	public RelevanceSetting getRelevanceSetting() 
	{
		return relevanceSetting;
	}

	public void setRelevanceSetting(RelevanceSetting relevanceSetting) 
	{
		this.relevanceSetting = relevanceSetting;
	}

	public void setMIManager(MIManager manager) 
	{
		this.relevanceSetting=new RelevanceSetting(manager);
		setting=new HierarchicalSetting("Relevance Shader Settings");
		setting.addSetting(relevanceSetting).addSetting(shadeColor);
	}
	
	@Override
	public void setDataSet(DataSet ds) 
	{
		setMIManager(ds.getMIManager());
	}
	
	
	
}
