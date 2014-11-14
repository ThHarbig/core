package mayday.vis3.graph.renderer.dispatcher;

import java.io.BufferedWriter;
import java.io.StringWriter;
import java.util.Set;

import javax.xml.stream.XMLStreamWriter;

import mayday.core.settings.generic.HierarchicalSetting;
import mayday.core.settings.generic.PluginInstanceSetting;
import mayday.vis3.graph.renderer.primary.PrimaryComponentRenderer;
import mayday.vis3.graph.vis3.SuperColorProvider;

public class RendererInstanceSetting extends PluginInstanceSetting<PrimaryComponentRenderer> 
{

	public RendererInstanceSetting(String Name, String Description, PrimaryComponentRenderer Default, Set<PrimaryComponentRenderer> apls) 
	{
		super(Name, Description, Default, apls);		
	}

	public RendererInstanceSetting(String Name, String Description,	String... MCs) 
	{
		super(Name, Description, MCs);		
	}


	public void setupPredef(SuperColorProvider coloring)
	{
		for(PrimaryComponentRenderer r:predef)
		{
			r.setColorProvider(coloring);
		}
	}

	@Override
	public RendererInstanceSetting clone() 
	{
		RendererInstanceSetting pts = new RendererInstanceSetting(getName(),getDescription(),getInstance(),predef);
		if (childSetting!=null)
			pts.childSetting.fromPrefNode(childSetting.toPrefNode());
		return pts;		
	}

	public static final String RENDERER_INSTANCE="rendererInstance";
	public static final String PLID="pluginId";

	public void exportXML(XMLStreamWriter writer) throws Exception
	{
		writer.writeStartElement(RENDERER_INSTANCE);
		writer.writeAttribute(PLID, getValueString());
		if(getInstance()!=null)
		{
			if(getInstance().getSetting()!=null)
			{
				writer.writeAttribute(RendererDispatcher.SETTINGS, serializeSetting());
				if(getInstance().getSetting() instanceof HierarchicalSetting)
				{
					DecoratorListSetting.serializeDataSetHint(writer, (HierarchicalSetting)getInstance().getSetting());
					DecoratorListSetting.serializeClassSelectionHint(writer, (HierarchicalSetting)getInstance().getSetting());
				}	
			}
		}

		writer.writeEndElement();
	}

	private String serializeSetting() throws Exception
	{
		StringWriter w=new StringWriter();
		getInstance().getSetting().toPrefNode().saveTo(new BufferedWriter(w));
		return w.toString();
	}

}
