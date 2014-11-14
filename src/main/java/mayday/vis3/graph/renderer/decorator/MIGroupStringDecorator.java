package mayday.vis3.graph.renderer.decorator;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import mayday.core.DataSet;
import mayday.core.Probe;
import mayday.core.meta.GenericMIO;
import mayday.core.meta.MIGroup;
import mayday.core.meta.MIManager;
import mayday.core.meta.MIType;
import mayday.core.meta.types.DoubleMIO;
import mayday.core.pluma.PluginInfo;
import mayday.core.pluma.PluginManagerException;
import mayday.core.settings.Setting;
import mayday.core.settings.events.SettingChangeEvent;
import mayday.core.settings.events.SettingChangeListener;
import mayday.core.settings.generic.HierarchicalSetting;
import mayday.core.settings.typed.MIGroupSetting;
import mayday.core.structures.graph.Node;
import mayday.vis3.graph.renderer.MIOColoring;
import mayday.vis3.graph.renderer.RendererDecorator;

public class MIGroupStringDecorator extends RendererDecorator implements SettingChangeListener
{
	private HierarchicalSetting setting;
	private MIGroupSetting miGroup;
	private MIOColoring coloring;
	private Font font=new Font(Font.SANS_SERIF,Font.PLAIN,10);
	
	public MIGroupStringDecorator() 
	{
		coloring=new MIOColoring();
		setting=new HierarchicalSetting("MI Group (String)");			
	}
	
	@SuppressWarnings("unchecked")
	@Override
	public void draw(Graphics2D g, Node node, Rectangle bounds, Object value,
			String label, boolean selected) 
	{
		
		boolean renderNormally=false;
		g.setBackground(Color.white);
		g.clearRect(0, bounds.height-10, bounds.width, 10);
		if(miGroup!=null && getMiGroup()!=null)
		{
			
			List<Color> colors=new ArrayList<Color>();
			List<String> values=new ArrayList<String>();
			if(value instanceof Probe)
			{
				if(getMiGroup().contains(value))
				{
					colors.add(getColoring().getColor((GenericMIO) getMiGroup().getMIO(value)));
					values.add(getMiGroup().getMIO(value).toString());
				}
				
			}
			
			if(value instanceof Iterable)
			{
				for(Probe p:(Iterable<Probe>)value)
				{
					if(getMiGroup().contains(p))
					{
						colors.add(getColoring().getColor((GenericMIO) getMiGroup().getMIO(p)));
						MIType t= getMiGroup().getMIO(p);
						if(t instanceof DoubleMIO)
							values.add(NumberFormat.getNumberInstance().format(((DoubleMIO) t).getValue()));
						else
							values.add(t.toString());
					}
				}					
			}
			if(colors.isEmpty())
				renderNormally=true;
			else
			{
				Rectangle r=new Rectangle(bounds.x,bounds.y,bounds.width, bounds.height-10);
				getRenderer().draw(g, node, r, value, label, selected);							
				r=new Rectangle(bounds.x,bounds.height-10,bounds.width, 10);
				
				int x=bounds.x;
				g.setFont(font);
				FontMetrics fm=g.getFontMetrics();
				
				for(int i=0; i!= values.size(); ++i)
				{
					g.setColor(colors.get(i));
					g.drawString(values.get(i)+",", x, bounds.y+bounds.height);
					x+=fm.getStringBounds(values.get(i)+", ", g).getBounds().width;					
				}
				
				
			}
		}else
			renderNormally=true; 
		if(renderNormally)
		{
			getRenderer().draw(g, node, bounds, value, label, selected);
		}
		
	}
	
	private MIGroup getMiGroup()
	{
		return miGroup.getMIGroup();
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
				"PAS.GraphViewer.RendererDecorator.MIOString",
				new String[]{},
				MC_DECORATOR,
				new HashMap<String, Object>(),
				"Stephan Symons",
				"symons@informatik.uni-tuebingen.de",
				"adds a text displaying a mi group ",
				"MI Group (String)"				
		);
		pli.addCategory(GROUP_DECORATORS);
		return pli;	
	}
	
	@Override
	public Setting getSetting() 
	{
		return setting;
	}
	
	public void setMIManager(MIManager manager) 
	{
		this.miGroup=new MIGroupSetting("Meta Information", "The meta information to display", null, manager, false);
		setting=new HierarchicalSetting("MI Group (String)");
		coloring.setMIGroup(miGroup.getMIGroup());		
		setting.addSetting(miGroup).addSetting(coloring.getColorGradient());
		setting.addChangeListener(this);
	}
	
	@Override
	public void stateChanged(SettingChangeEvent e) 
	{
		coloring.setMIGroup(miGroup.getMIGroup());
	}
	
	@Override
	public void setDataSet(DataSet ds) 
	{
		setMIManager(ds.getMIManager());
	}
	
	
	public MIOColoring getColoring() {
		return coloring;
	}
}
