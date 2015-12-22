package mayday.vis3.graph.renderer;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import mayday.core.Probe;
import mayday.core.meta.MIGroup;
import mayday.core.meta.MIType;
import mayday.core.meta.types.StringListMIO;
import mayday.core.pluma.PluginInfo;
import mayday.core.pluma.PluginManagerException;
import mayday.core.settings.Setting;
import mayday.core.settings.generic.HierarchicalSetting;
import mayday.core.settings.typed.RestrictedStringSetting;
import mayday.core.structures.graph.Node;
import mayday.core.structures.graph.nodes.MIONode;
import mayday.vis3.graph.renderer.primary.DefaultComponentRenderer;

public class MIValueRenderer extends DefaultComponentRenderer
{
	static final String[] renderOptions={"Strings","String Colors" };
	
	private HierarchicalSetting setting=new HierarchicalSetting("Bar Plot Setting");
	private RestrictedStringSetting mode=new RestrictedStringSetting("Display Mode", null, 0, renderOptions);
	
	public MIValueRenderer() 
	{
		setting.addSetting(mode);
	}
	
	public Dimension getSuggestedSize() 
	{
		return new Dimension(150, 80);
	}
	
	@Override
	public void draw(Graphics2D g, Node node, Rectangle bounds, Object value, String label, boolean selected)
	{
		if(node instanceof MIONode)
		{			
			g.clearRect(0, 0, bounds.width, bounds.height);
			if(mode.getSelectedIndex()==0)
			{				
				drawStrings(g, bounds, node);
			}
			if(mode.getSelectedIndex()==1)
			{				
				drawColors(g, bounds, node);
			}	
			
			RendererTools.drawBox(g, bounds, selected);
		}else
			super.draw(g,node,bounds,value,label,selected);
			
	}
	
	private void drawStrings(Graphics2D g, Rectangle bounds, Node node)
	{
		MIGroup grp=((MIONode) node).getMiGroup();
		StringBuffer sb=new StringBuffer();
		int i=0;
		for(Probe p: ((MIONode) node).getProbes())
		{
			MIType mio=grp.getMIO(p);			
			if(mio instanceof StringListMIO)
			{
				for(String s: ((StringListMIO) mio).getValue())
				{
					sb.append(" "+s);
				}
			}else
			{
				sb.append(" "+mio.toString());
			}
			RendererTools.drawBreakingString(g, sb.toString().substring(1), bounds.width, 0, 0);
			++i;
		}
	}
	
	
	private void drawColors(Graphics2D g, Rectangle bounds, Node node)
	{
		MIGroup grp=((MIONode) node).getMiGroup();
		double dy=bounds.height / ((MIONode) node).getProbes().size();
		
		List<Color> lc=new ArrayList<Color>();
		int i=0;
		for(Probe p: ((MIONode) node).getProbes())
		{
			MIType mio=grp.getMIO(p);
			lc.clear();
			if(mio instanceof StringListMIO)
			{
				for(String s: ((StringListMIO) mio).getValue())
				{
					lc.add(RendererTools.wordToColor(s));
				}
			}else
			{
				lc.add(RendererTools.wordToColor(mio.toString()));
			}
			RendererTools.drawColorLine(g, lc, new Rectangle(0, (int)(i*dy),bounds.width,(int)dy));
			++i;
		}
	}
	
	@Override
	public Dimension getSuggestedSize(Node node, Object value) 
	{
		return new Dimension(150, 80);
	}
	
	@Override
	public PluginInfo register() throws PluginManagerException 
	{
		PluginInfo pli = new PluginInfo(
				this.getClass(),
				"PAS.GraphViewer.Renderer.MIO",
				new String[]{},
				MC,
				new HashMap<String, Object>(),
				"Stephan Symons",
				"symons@informatik.uni-tuebingen.de",
				"Render Meta Information using a heatmap",
				"Meta Information Renderer"				
		);
		pli.addCategory(GROUP_PRIMARY);
		return pli;	
	}
	
	@Override
	public Setting getSetting() 
	{
		return setting;
	}
	
	
	
}
