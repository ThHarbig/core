package mayday.vis3.graph.renderer.primary;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.awt.geom.Ellipse2D;
import java.util.HashMap;

import mayday.core.pluma.PluginInfo;
import mayday.core.pluma.PluginManagerException;
import mayday.core.settings.Setting;
import mayday.core.settings.events.SettingChangeEvent;
import mayday.core.settings.events.SettingChangeListener;
import mayday.core.settings.generic.HierarchicalSetting;
import mayday.core.settings.typed.IntSetting;
import mayday.core.structures.graph.Node;
import mayday.vis3.gradient.ColorGradient;
import mayday.vis3.gradient.ColorGradientSetting;
import mayday.vis3.graph.renderer.RendererTools;
import mayday.vis3.graph.vis3.SuperColorProvider;

public class NodeCircleRenderer extends CircleRenderer
{
	private ColorGradient gradient;
	private IntSetting maxDegree;
	private IntSetting minDegree;
	
	private ColorGradientSetting gradientSetting;
	private HierarchicalSetting setting;
	
	int maxDeg=0;

	public NodeCircleRenderer() 
	{
		super();
	}

	public NodeCircleRenderer(int MaxDegree) 
	{
		super();
		maxDeg=MaxDegree;
		getSetting();
	}
	
	@Override
	public Setting getSetting() 
	{
		if(setting==null)
		{
			maxDegree=new IntSetting("Maximum Degree","Upper Degree Cutoff. All nodes\n" +
					" with more edges will be displayed like a node with \n this many edges.", 10);
			minDegree=new IntSetting("Minimum Degree","Lower Degree Cutoff. All nodes" +
					"\n with less edges will be displayed like a node with \n this many edges. ", 0);
			
			gradient= ColorGradient.createDefaultGradient(minDegree.getIntValue(), maxDegree.getIntValue()+1);
			
			gradientSetting=new ColorGradientSetting("Color Gradient", null, gradient);
			setting=new HierarchicalSetting("Node Renderer");
			maxDegree.setIntValue(maxDeg);
			
			
			setting.addSetting(minDegree).addSetting(maxDegree).addSetting(gradientSetting);
			setting.addChangeListener(new SettingChangeListener() {
				
				@Override
				public void stateChanged(SettingChangeEvent e) {
					gradient= gradientSetting.getColorGradient();	
					gradient.setMax(maxDegree.getIntValue());
					gradient.setMin(minDegree.getIntValue());
				}
			});
		}
		return setting;
	}


	public void draw(Graphics2D g, Node node, Rectangle bounds, Object value,String label, boolean selected) 
	{
		int r=Math.min(bounds.width, bounds.height)-1;
		int d=node.getDegree();

		Ellipse2D p=new Ellipse2D.Double((bounds.width-r)/2, 0,r, r);	
		if(d==0)
		{
			g.setColor(selected?Color.red:Color.black);
			g.draw(p);
		}else
		{	
			if(d > maxDegree.getIntValue()) 
				d=maxDegree.getIntValue();
			if(d < minDegree.getIntValue())
				d=minDegree.getIntValue();
			Color c=gradient.mapValueToColor(d);
			g.setColor(c);	
			g.fill(p);
			RendererTools.drawEllipse(g, bounds, selected);
			
			if(selected)
			{
				c=RendererTools.invertColor(c);
				g.draw(p);
			}
		}
	}
	
	@Override
	public void setColorProvider(SuperColorProvider colorProvider) 
	{
		getSetting();
	}

	@Override
	public PluginInfo register() throws PluginManagerException 
	{
		PluginInfo pli = new PluginInfo(
				this.getClass(),
				"PAS.GraphViewer.Renderer.NodeCircle",
				new String[]{},
				MC,
				new HashMap<String, Object>(),
				"Stephan Symons",
				"symons@informatik.uni-tuebingen.de",
				"Render everything as a circle, color by degree",
				"Node property circle renderer"				
		);
		pli.addCategory(GROUP_PRIMARY);
		return pli;	
	}	
	
	
}
