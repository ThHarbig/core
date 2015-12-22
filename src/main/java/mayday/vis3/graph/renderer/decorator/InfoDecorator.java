package mayday.vis3.graph.renderer.decorator;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.util.HashMap;
import java.util.Set;
import java.util.TreeSet;

import mayday.core.Probe;
import mayday.core.pluma.PluginInfo;
import mayday.core.pluma.PluginManagerException;
import mayday.core.settings.Setting;
import mayday.core.settings.generic.HierarchicalSetting;
import mayday.core.settings.typed.RestrictedStringSetting;
import mayday.core.structures.graph.Node;
import mayday.vis3.graph.renderer.ComponentRenderer;
import mayday.vis3.graph.renderer.RendererDecorator;
import mayday.vis3.graph.renderer.primary.PrimaryComponentRenderer;

public class InfoDecorator extends RendererDecorator
{
	private HierarchicalSetting setting;
	private static final String[] modes={"Rendering Info","Number of Probes","Node Properties", "Probe Origin"};
	private RestrictedStringSetting modeSetting=new RestrictedStringSetting(
			"Type of Information", 
			"The source of information:\nRendering: details about the rendering\n" +
			"Probes: Number of Probes"+
			"Node: role and connectivity\nCoordinates of the node"+ 
			"Probe Origin: show the Dataset where the probes are from", 
			0, 
			modes);

	private Font font=new Font(Font.SANS_SERIF,Font.PLAIN,10);
	
	
	
	
	public InfoDecorator()
	{
		super();
		
		setting=new HierarchicalSetting("Info Settings");
		setting.addSetting(modeSetting);
	}	
	
	public InfoDecorator(ComponentRenderer renderer)
	{
		super(renderer);
		setting=new HierarchicalSetting("Info Settings");	
		setting.addSetting(modeSetting);
	}
	
	@Override
	public void draw(Graphics2D g, Node node,Rectangle bounds, Object value,String label, boolean selected) 
	{
		Rectangle r=new Rectangle(bounds.x,bounds.y,bounds.width, bounds.height-10);
		getRenderer().draw(g, node, r, value, label, selected);
		Font f=g.getFont();
		g.setFont(font);
		String message="";
		switch(modeSetting.getSelectedIndex())
		{
			case 0:
				message=getRenderingInfo();
				break;
			case 1:
				message=getProbesInfo(value);
				break;
			case 2:
				message=getNodeInfo(node);
				break;	
			case 3:
				message=getDataSetInfo(value);
				break;		
		}
		g.setColor(Color.black);
		g.drawString(message, r.x, bounds.y+bounds.height);
		g.setFont(f);		
	}
	
	private String getRenderingInfo()
	{
		ComponentRenderer renderer=getRenderer();
		while(!(renderer instanceof PrimaryComponentRenderer) && renderer!=null)
		{
			renderer=((RendererDecorator)renderer).getRenderer();
		}
		if(renderer==null)
			return "";
		return ((PrimaryComponentRenderer)renderer).getRendererStatus();
	}
	
	@SuppressWarnings("unchecked")
	private String getProbesInfo(Object value)
	{
		if(value==null)
		return "0 Probes";		
		if(value instanceof Probe)
			return "1 Probe";
		if(value instanceof Iterable)
		{
			int i=0;
			for(@SuppressWarnings("unused") Object p: ((Iterable) value))
				++i;
			return i+" Probes";
		}
		return "other object";
	}
	
	private String getNodeInfo(Node node)
	{
		return node.getRole()+" in:"+node.getInDegree()+" out:"+node.getOutDegree();
	}
	

	@SuppressWarnings("unchecked")
	private String getDataSetInfo(Object value)
	{
		if(value instanceof Probe)
			return ((Probe) value).getMasterTable().getDataSet().getName();
		if(value instanceof Iterable)
		{
			Set<String> names=new TreeSet<String>();
			for(Object p: ((Iterable) value))
			{
				names.add(((Probe) p).getMasterTable().getDataSet().getName());
			}
			return names.toString(); 
		}
		return "n/a";
	}
	
	@Override
	public PluginInfo register() throws PluginManagerException 
	{
		PluginInfo pli = new PluginInfo(
				this.getClass(),
				"PAS.GraphViewer.RendererDecorator.Info",
				new String[]{},
				MC_DECORATOR,
				new HashMap<String, Object>(),
				"Stephan Symons",
				"symons@informatik.uni-tuebingen.de",
				"Add information about the node",
				"Information"				
		);
		pli.addCategory(GROUP_DECORATORS);
		return pli;	
	}
	
	@Override
	public Dimension getSuggestedSize(Node node, Object value) 
	{
		int h=getRenderer().getSuggestedSize(node,value).height+10;
		return new Dimension(getRenderer().getSuggestedSize(node,value).width,h);
	}

	@Override
	public Setting getSetting() 
	{
		return setting;
	}
}
