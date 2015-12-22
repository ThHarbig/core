package mayday.vis3.graph.renderer.decorator;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import mayday.core.DataSet;
import mayday.core.Probe;
import mayday.core.datasetmanager.DataSetManager;
import mayday.core.pluma.PluginInfo;
import mayday.core.pluma.PluginManagerException;
import mayday.core.settings.Setting;
import mayday.core.settings.events.SettingChangeEvent;
import mayday.core.settings.events.SettingChangeListener;
import mayday.core.settings.generic.HierarchicalSetting;
import mayday.core.structures.graph.Node;
import mayday.vis3.gradient.ColorGradient;
import mayday.vis3.gradient.ColorGradientSetting;
import mayday.vis3.graph.renderer.ComponentRenderer;
import mayday.vis3.graph.renderer.RendererDecorator;
import mayday.vis3.graph.renderer.RendererTools;

public class DataSetDecorator extends RendererDecorator implements SettingChangeListener
{
	private HierarchicalSetting setting;
	private ColorGradientSetting colorGradient=new ColorGradientSetting("Color Gradient","Colors for the class labels",ColorGradient.createRainbowGradient(0, 1));
	private Map<DataSet,Color> colors;
	
	public DataSetDecorator()
	{
		super();
		setting=new HierarchicalSetting("DataSet Settings");
		setting.addSetting(colorGradient);
		colorGradient.addChangeListener(this);
		setupColors();
	}	
	
	public DataSetDecorator(ComponentRenderer renderer)
	{
		super(renderer);
		setting=new HierarchicalSetting("DataSet Settings");
		setting.addSetting(colorGradient);
		colorGradient.addChangeListener(this);
		setupColors();
	}
	
	private void setupColors() 
	{
		colors=new HashMap<DataSet, Color>();
		int numDS=DataSetManager.singleInstance.getDataSets().size();
		int i=0;
		for(DataSet ds:DataSetManager.singleInstance.getDataSets())
		{
			double val=(1.0*i)/(1.0*numDS);
			val= val* 0.666 + 0.333;
			colors.put(ds, colorGradient.getColorGradient().mapValueToColor(val));
			++i;
		}	
	}

	@SuppressWarnings("unchecked")
	@Override
	public void draw(Graphics2D g, Node node,Rectangle bounds, Object value,String label, boolean selected) 
	{
		Rectangle r=new Rectangle(bounds.x+12,bounds.y,bounds.width-12, bounds.height);
		getRenderer().draw(g, node, r, value, label, selected);
		if(colors.isEmpty())
		{
			setupColors();
		}
		if(value instanceof Probe)
		{
			g.setColor(colors.get( ((Probe)value).getMasterTable().getDataSet()));
			g.fillRect(bounds.x, bounds.y, 10, bounds.height);
		}
		if(value instanceof Iterable)
		{
			List<Color> lcolors=new ArrayList<Color>();
			for(Object o:((Iterable)value) )
			{
				Probe p=(Probe)o;
				lcolors.add(colors.get(p.getMasterTable().getDataSet()));				
			}
			RendererTools.drawColorColumn(g, lcolors, new Rectangle(bounds.x,bounds.y, 10,bounds.height));
		}
		g.setColor(Color.white);
		g.fillRect(bounds.x+10, bounds.y+1, 1, bounds.height-2);
	}
		
	@Override
	public PluginInfo register() throws PluginManagerException 
	{
		PluginInfo pli = new PluginInfo(
				this.getClass(),
				"PAS.GraphViewer.RendererDecorator.DataSet",
				new String[]{},
				MC_DECORATOR,
				new HashMap<String, Object>(),
				"Stephan Symons",
				"symons@informatik.uni-tuebingen.de",
				"Add information about the datasets of the probes present at the node",
				"Data Set"				
		);
		pli.addCategory(GROUP_DECORATORS);
		return pli;	
	}
	
	@Override
	public Dimension getSuggestedSize(Node node, Object value) 
	{
		int w=getRenderer().getSuggestedSize(node,value).width+15;
		return new Dimension(w,getRenderer().getSuggestedSize(node,value).height);
	}

	@Override
	public Setting getSetting() 
	{
		return setting;
	}
	
	@Override
	public void stateChanged(SettingChangeEvent e) 
	{
		setupColors();		
	}
}
