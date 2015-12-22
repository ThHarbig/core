package mayday.vis3.graph.renderer.decorator;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import mayday.core.ClassSelectionModel;
import mayday.core.DataSet;
import mayday.core.Probe;
import mayday.core.pluma.PluginInfo;
import mayday.core.pluma.PluginManagerException;
import mayday.core.settings.Setting;
import mayday.core.settings.events.SettingChangeEvent;
import mayday.core.settings.generic.BooleanHierarchicalSetting;
import mayday.core.settings.generic.HierarchicalSetting;
import mayday.core.settings.typed.BooleanSetting;
import mayday.core.settings.typed.ClassSelectionSetting;
import mayday.core.structures.graph.Node;
import mayday.vis3.gradient.ColorGradient;
import mayday.vis3.gradient.ColorGradientSetting;
import mayday.vis3.gradient.ColorGradientSetting.LayoutStyle;
import mayday.vis3.graph.renderer.RendererTools;

public class ClassSelectionDatasetDecorator extends ClassSelectionDecorator 
{
	private HierarchicalSetting setting;
	private Map<DataSet, ClassSelectionSetting> mappings=new HashMap<DataSet, ClassSelectionSetting>();
	private Map<DataSet, BooleanHierarchicalSetting> activated=new HashMap<DataSet,BooleanHierarchicalSetting >();
	private Map<DataSet, List<Color> > colors=new HashMap<DataSet, List<Color> >();
	private Set<DataSet> activeDataSet=new HashSet<DataSet>();

	private BooleanSetting useClassColors;

	public ClassSelectionDatasetDecorator() 
	{
		super();
		setting=new HierarchicalSetting("Class Selections");
		gradient=new ColorGradientSetting("Color Gradient", null, ColorGradient.createRainbowGradient(0, 1));
		useClassColors=new BooleanSetting("Use Class Selection Colors", "Instead of the Gradient, use the same colors as in the class selection model", false);
		setting.addSetting(gradient);
		setting.addSetting(useClassColors);
		gradient.setLayoutStyle(LayoutStyle.FULL);
		setting.addChangeListener(this);
	}
	@Override
	public PluginInfo register() throws PluginManagerException 
	{
		PluginInfo pli = new PluginInfo(
				this.getClass(),
				"PAS.GraphViewer.RendererDecorator.ClassDataSet",
				new String[]{},
				MC_DECORATOR,
				new HashMap<String, Object>(),
				"Stephan Symons",
				"symons@informatik.uni-tuebingen.de",
				"Adds a class selection to the component",
				"Class Label (DataSet)"				
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
	public void setDataSet(DataSet ds)
	{
		for(DataSet dataSet: ds.getDataSetManager().getDataSets())
		{
			ClassSelectionSetting model=new ClassSelectionSetting(dataSet.getName(), null, new ClassSelectionModel(dataSet.getMasterTable()), 1, 20);
			BooleanHierarchicalSetting activator=new BooleanHierarchicalSetting(dataSet.getName(), null, false);
			activator.addSetting(model);
			mappings.put(dataSet,model); 
			activated.put(dataSet, activator);
			applyGradient(dataSet);
			setting.addSetting(activator);
		}
	}

	private void applyGradient(DataSet ds) 
	{
		ClassSelectionSetting model=mappings.get(ds);
		List<Color> dsColors=new ArrayList<Color>();
		if(useClassColors.getBooleanValue())
		{
			if(model.getModel().getNumClasses()==0)
				return;
			ClassSelectionModel m=model.getModel();
			Map<String, Color> color=new HashMap<String, Color>();
			for(int i=0; i!= m.getNumClasses(); ++i)
			{
				color.put(m.getClassNames().get(i), ClassSelectionModel.getColor(i, m.getNumClasses()));
			}
			for(int i=0; i!= m.getNumObjects(); ++i)
			{
				dsColors.add(color.get(m.getObjectClass(i)));
			}
		}else
		{
			Map<String, Color> cc=new HashMap<String, Color>();
			for(int i=0; i!= model.getModel().getNumClasses(); ++i)
			{
				cc.put(model.getModel().getClassesLabels().get(i),
						gradient.getColorGradient().mapValueToColor((1.0*i)/(1.0*model.getModel().getNumClasses())) );		
			}

			for(int i=0; i!= model.getModel().getNumObjects(); ++i)
			{
				if(cc.containsKey(model.getModel().getObjectClass(i)))
					dsColors.add(cc.get(model.getModel().getObjectClass(i)));
				else
					dsColors.add(Color.LIGHT_GRAY);			
			}
		}
		colors.put(ds, dsColors);
	}

	@Override
	public void stateChanged(SettingChangeEvent e) 
	{
		activeDataSet.clear();
		for(DataSet ds: activated.keySet())
		{
			if(activated.get(ds).getBooleanValue())
			{
				activeDataSet.add(ds);
				applyGradient(ds);
			}
		}
	}

	@SuppressWarnings("unchecked")
	public void draw(Graphics2D g, Node node,Rectangle bounds, Object value,
			String label, boolean selected) 
	{
		try{
			//inspect first probe
			DataSet thisDs=null;
			if(value instanceof Probe)
			{
				thisDs=((Probe)value).getMasterTable().getDataSet();
			}
			if(value instanceof Iterable)
			{
				// just look at first element
				if( ((Iterable<Probe>)value).iterator().hasNext())
				{
					Probe p=((Iterable<Probe>)value).iterator().next();
					thisDs=p.getMasterTable().getDataSet();
				}
				
			}
			// see if ds in activeDataSets
			if(thisDs!=null && activeDataSet.contains(thisDs))
			{
				// render box, 
				Rectangle r=new Rectangle(bounds.x,bounds.y,bounds.width, bounds.height-10);
				getRenderer().draw(g, node, r, value, label, selected);
				r=new Rectangle(bounds.x,bounds.height-10,bounds.width, 10);
				RendererTools.drawColorLine(g, colors.get(thisDs), r);
			}else
			{
				// no dataset that is active; render the component. 
				getRenderer().draw(g, node, bounds, value, label, selected);
			}

		}catch(Throwable r)
		{
			// in any case: just render stuff.
			//			System.out.println("Error");
			r.printStackTrace();
			getRenderer().draw(g, node, bounds, value, label, selected);			
		}



	}
}
