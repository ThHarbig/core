package mayday.vis3.graph.renderer.dispatcher;

import javax.xml.stream.XMLStreamWriter;

import mayday.core.DataSet;
import mayday.core.settings.generic.HierarchicalSetting;
import mayday.core.settings.generic.PluginInstanceSetting;
import mayday.vis3.graph.renderer.ComponentRenderer;
import mayday.vis3.graph.renderer.RendererDecorator;
import mayday.vis3.graph.renderer.primary.DefaultComponentRenderer;
import mayday.vis3.graph.renderer.primary.PrimaryComponentRenderer;
import mayday.vis3.graph.vis3.SuperColorProvider;

public class RendererPluginSetting extends HierarchicalSetting
{
	protected RendererInstanceSetting primaryRenderer;	
	protected DecoratorListSetting decorators;	
	protected DataSet ds;
	protected SuperColorProvider coloring;
	
	public static final String RENDERER="rendererConfiguration";
	
	public RendererPluginSetting(DataSet ds, SuperColorProvider coloring) 
	{
		this("Renderer",ds,coloring);
	}
	
	public RendererPluginSetting(String name, DataSet ds, SuperColorProvider coloring) 
	{
		super(name,LayoutStyle.TREE,false);
		try{
		
		primaryRenderer=new RendererInstanceSetting(
				"Primary Renderer", 
				"This is the renderer that actually displays the data",
				AbstractRendererPlugin.MC);
		primaryRenderer.getInstance().setColorProvider(coloring);
		primaryRenderer.setupPredef(coloring);
		decorators=new DecoratorListSetting(ds);
		
		this.ds=ds;		
		this.coloring=coloring;
		
		addSetting(primaryRenderer).addSetting(decorators);
		}catch(Throwable t)
		{
			t.printStackTrace();
		}
	}
	
	public ComponentRenderer getRenderer()
	{
		ComponentRenderer renderer=primaryRenderer.getInstance();
		if(renderer==null) // should never happen!
		{
			renderer=new DefaultComponentRenderer();
		}
		((PrimaryComponentRenderer)renderer).setColorProvider(coloring);
		for(RendererDecorator dec: decorators.getSelection())
		{
			dec.setRenderer(renderer);
			renderer=dec;
		}
		return renderer;
	}
	
	@Override
	public RendererPluginSetting clone() 
	{
		RendererPluginSetting res=new RendererPluginSetting(ds,coloring);
		res.primaryRenderer=primaryRenderer.clone();
		res.primaryRenderer.setupPredef(getColoring());
		res.primaryRenderer.getInstance().setColorProvider(coloring);
		res.decorators=decorators.clone();
		return res;
	}
	
	public void setPrimaryRenderer(String pluginID)
	{
		try
		{
			primaryRenderer.setStringValue(pluginID);		
//			primaryRenderer.getInstance().setColorProvider(coloring);
		}catch(Exception e)
		{
			e.printStackTrace();
		}
	}

	public PluginInstanceSetting<PrimaryComponentRenderer> getPrimaryRenderer() 
	{
		return primaryRenderer;
	}

	public DecoratorListSetting getDecorators() {
		return decorators;
	}

	public SuperColorProvider getColoring() {
		return coloring;
	}

	public void setPrimaryRenderer(RendererInstanceSetting primaryRenderer) {
		this.primaryRenderer = primaryRenderer;
		primaryRenderer.getInstance().setColorProvider(coloring);
	}

	public void setDecorators(DecoratorListSetting decorators) {
		this.decorators = decorators;
	}
	

	public void exportXML(XMLStreamWriter writer) throws Exception
	{
		writer.writeStartElement(RENDERER);
		primaryRenderer.exportXML(writer);
		decorators.exportXML(writer);
		writer.writeEndElement();
	}
	
	
	
}
