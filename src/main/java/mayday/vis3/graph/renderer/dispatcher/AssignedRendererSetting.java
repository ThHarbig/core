package mayday.vis3.graph.renderer.dispatcher;

import javax.xml.stream.XMLStreamWriter;

import mayday.core.DataSet;
import mayday.core.settings.typed.StringSetting;
import mayday.vis3.graph.vis3.SuperColorProvider;

public class AssignedRendererSetting extends RendererPluginSetting
{
	private StringSetting target;

	public AssignedRendererSetting(String target, DataSet ds, SuperColorProvider coloring) 
	{
		super(target, ds,coloring);
		this.target=new StringSetting("Role", "The node role for which this renderer is used", target);
		this.target.setStringValue(target);
		addSetting(this.target);
	}

	@Override
	public String toString() 
	{
		return target.getStringValue();
	}

	@Override
	public AssignedRendererSetting clone()
	{
		AssignedRendererSetting res=new AssignedRendererSetting(target.getStringValue(), ds, coloring);
		res.primaryRenderer=primaryRenderer.clone();
		res.primaryRenderer.getInstance().setColorProvider(coloring);
		res.decorators=decorators.clone();
		return res;
	}

	public StringSetting getTarget() {
		return target;
	}

	public void setTarget(StringSetting target) {
		this.target = target;
	}

	public static final String TARGET="target"; 

	public void exportXML(XMLStreamWriter writer) throws Exception
	{
		writer.writeStartElement(RENDERER);
		writer.writeAttribute(TARGET, target.getStringValue());
		primaryRenderer.exportXML(writer);
		decorators.exportXML(writer);
		writer.writeEndElement();

	}



}
