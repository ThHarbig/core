package mayday.vis3.graph.renderer.decorator;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import mayday.core.ClassSelectionModel;
import mayday.core.DataSet;
import mayday.core.pluma.PluginInfo;
import mayday.core.pluma.PluginManagerException;
import mayday.core.settings.Setting;
import mayday.core.settings.events.SettingChangeEvent;
import mayday.core.settings.events.SettingChangeListener;
import mayday.core.settings.generic.HierarchicalSetting;
import mayday.core.settings.typed.ClassSelectionSetting;
import mayday.core.structures.graph.Node;
import mayday.vis3.gradient.ColorGradient;
import mayday.vis3.gradient.ColorGradientSetting;
import mayday.vis3.graph.renderer.ComponentRenderer;
import mayday.vis3.graph.renderer.RendererDecorator;
import mayday.vis3.graph.renderer.RendererTools;

public class ClassSelectionDecorator extends RendererDecorator implements SettingChangeListener
{
	private ClassSelectionSetting model=new ClassSelectionSetting("Class Partition", null, new ClassSelectionModel(1,1), 1, 20);	
	protected ColorGradientSetting gradient=new ColorGradientSetting("Color Gradient","Colors for the class labels",ColorGradient.createRainbowGradient(0, 2));	
	private HierarchicalSetting setting=new HierarchicalSetting("Class Partition Settings");
	
	private List<Color> classColors;

	
	
	public ClassSelectionDecorator() 
	{
		super();
		setting.addSetting(model).addSetting(gradient);
		setting.addChangeListener(this);
	}

	public ClassSelectionDecorator(ComponentRenderer renderer, ClassSelectionModel model) 
	{
		super(renderer);		
		this.model.setModel(model);
		setting.addSetting(this.model).addSetting(gradient);
		applyGradient();
		setting.addChangeListener(this);
	}
	
	private void applyGradient() 
	{
		classColors=new ArrayList<Color>();
		Map<String, Color> cc=new HashMap<String, Color>();
		for(int i=0; i!= model.getModel().getNumClasses(); ++i)
		{
			cc.put(model.getModel().getClassesLabels().get(i),gradient.getColorGradient().mapValueToColor(i));				
		}
		for(int i=0; i!= model.getModel().getNumObjects(); ++i)
		{
			if(cc.containsKey(model.getModel().getObjectClass(i)))
				classColors.add(cc.get(model.getModel().getObjectClass(i)));
			else
				classColors.add(Color.LIGHT_GRAY);
		}
	}

	public ClassSelectionModel getModel() {
		return model.getModel();
	}

	public void setModel(ClassSelectionModel model) {
		this.model.setModel(model);
		applyGradient();
	}

	public ColorGradient getGradient() {
		return gradient.getColorGradient();
	}

	public void setGradient(ColorGradient gradient) {
		this.gradient.setColorGradient(gradient);
		applyGradient();
	}

	public void draw(Graphics2D g, Node node,Rectangle bounds, Object value,
			String label, boolean selected) 
	{
		if(model!=null)
		{
			Rectangle r=new Rectangle(bounds.x,bounds.y,bounds.width, bounds.height-10);
			getRenderer().draw(g, node, r, value, label, selected);
			r=new Rectangle(bounds.x,bounds.height-10,bounds.width, 10);
			RendererTools.drawColorLine(g, classColors, r);
		}else
		{
			getRenderer().draw(g, node, bounds, value, label, selected);
		}
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
				"PAS.GraphViewer.RendererDecorator.Class",
				new String[]{},
				MC_DECORATOR,
				new HashMap<String, Object>(),
				"Stephan Symons",
				"symons@informatik.uni-tuebingen.de",
				"Adds a class selection to the component",
				"Class Label"				
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
	public void stateChanged(SettingChangeEvent e) 
	{
		applyGradient();		
	}
	
	@Override
	public void setDataSet(DataSet ds)
	{
		model.setModel(new ClassSelectionModel(ds.getMasterTable()));
		applyGradient();
		setting=new HierarchicalSetting("Class Partition Settings");
		setting.addSetting(model).addSetting(gradient);
	}

}
