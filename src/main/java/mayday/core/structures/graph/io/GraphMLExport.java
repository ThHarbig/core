package mayday.core.structures.graph.io;

import java.awt.Rectangle;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

import javax.xml.stream.XMLOutputFactory;
import javax.xml.stream.XMLStreamWriter;

import mayday.core.structures.graph.Edge;
import mayday.core.structures.graph.Node;
import mayday.vis3.graph.components.CanvasComponent;
import mayday.vis3.graph.components.NodeComponent;
import mayday.vis3.graph.model.GraphModel;

public class GraphMLExport extends GraphExport
{

	public static void export(GraphModel model, File f) throws IOException, Exception
	{
		XMLOutputFactory xof = XMLOutputFactory.newInstance();
		XMLStreamWriter writer =xof.createXMLStreamWriter(new FileWriter(f));
		writer.writeStartDocument("utf-8", "1.0");
		writer.writeStartElement("graphml");
		writer.writeAttribute("xmlns", "http://graphml.graphdrawing.org/xmlns");
		writer.writeAttribute("xmlns:xsi", "http://www.w3.org/2001/XMLSchema-instance");
		writer.writeAttribute("xsi:schemaLocation", "http://graphml.graphdrawing.org/xmlns http://graphml.graphdrawing.org/xmlns/1.0/graphml.xsd");
		writer.writeComment("Created by Mayday");
		writeDefaultAttributes(writer);		
		writeAttribute(writer, RENDERER_KEY, NODE_TARGET, RENDERER_KEY);
		writer.writeStartElement("key");
		writer.writeAttribute("for", NODE_TARGET);
		writer.writeAttribute("id", "DefaultNodeAttributes");
		writer.writeAttribute("attr.type", "m.keyvaluelist");
		writer.writeAttribute("attr.name", "Node Attributes");
		writer.writeEndElement();

		model.getGraph().exportGraphHead(writer);

		for(CanvasComponent comp:model.getComponents())
		{
			Node n=((NodeComponent)comp).getNode();
			n.exportNodeHead(writer);

			writeDataElement(writer, RENDERER_KEY, comp.getRenderer().getClass().getCanonicalName());
			writeDataElement(writer, GEOMETRY_KEY, rectangleToString(comp.getBounds()));
			writer.writeEndElement();

		}
		writer.flush();
		for(Edge e:model.getGraph().getEdges())
		{
			e.export(writer);
		}

		writer.writeEndDocument();
		writer.flush();
		writer.close();
	}

	public static String rectangleToString(Rectangle rect)
	{
		return rect.x+","+rect.y+","+rect.width+","+rect.height;
	}

	public static Rectangle parseRectangle(String str)
	{
		String[] tok=str.split(",");
		return new Rectangle((int)Double.parseDouble(tok[0]),
				(int)Double.parseDouble(tok[1]),
				(int)Double.parseDouble(tok[2]),
				(int)Double.parseDouble(tok[3])	);
	}

	public static void exportYed(GraphModel model, File f) throws IOException, Exception
	{
		XMLOutputFactory xof = XMLOutputFactory.newInstance();
		XMLStreamWriter writer =xof.createXMLStreamWriter(new FileWriter(f));

		writer.writeStartDocument("utf-8", "1.0");
		writer.writeStartElement("graphml");
		writer.writeAttribute("xmlns", "http://graphml.graphdrawing.org/xmlns");
		writer.writeAttribute("xmlns:xsi", "http://www.w3.org/2001/XMLSchema-instance");
		writer.writeAttribute("xmlns:y", "http://www.yworks.com/xml/graphml");
		writer.writeAttribute("xsi:schemaLocation", "http://graphml.graphdrawing.org/xmlns http://www.yworks.com/xml/schema/graphml/1.0/ygraphml.xsd");
		writeDefaultAttributes(writer);		
		writer.writeComment("Created by Mayday");
		writer.writeComment("Creates graphical properties of the nodes");

		writer.writeStartElement("key");
		writer.writeAttribute("for", NODE_TARGET);
		writer.writeAttribute("id", "d0");
		writer.writeAttribute("yfiles.type", "nodegraphics");
		writer.writeEndElement();

		writer.writeStartElement("key");
		writer.writeAttribute("for", NODE_TARGET);
		writer.writeAttribute("id", "d1");
		writer.writeAttribute("attr.type", "string");
		writer.writeAttribute("attr.name", "description");
		writer.writeEndElement();


		writer.writeStartElement("key");
		writer.writeAttribute("for", EDGE_TARGET);
		writer.writeAttribute("id", "d2");
		writer.writeAttribute("yfiles.type", "edgegraphics");
		writer.writeEndElement();

		writer.writeStartElement("key");
		writer.writeAttribute("for", EDGE_TARGET);
		writer.writeAttribute("id", "d3");
		writer.writeAttribute("attr.type", "string");
		writer.writeAttribute("attr.name", "description");
		writer.writeEndElement();

		writer.writeStartElement("key");
		writer.writeAttribute("for", GRAPHML_TARGET);
		writer.writeAttribute("id", "d4");
		writer.writeAttribute("yfiles.type", "resources");
		writer.writeEndElement();

		model.getGraph().exportGraphHead(writer);

		for(CanvasComponent comp:model.getComponents())
		{
			exportY(writer,(NodeComponent)comp);
		}
		for(Edge e: model.getGraph().getEdges())
		{
			exportY(writer, e);
		}

		writer.writeEndDocument();
		writer.flush();
		writer.close();
	}

	private static void exportY(XMLStreamWriter writer, Edge edge) throws Exception
	{
		edge.exportEdgeHead(writer);
		writer.writeStartElement("data");
		writer.writeAttribute("key", "d2");
		writer.writeStartElement("y:PolyLineEdge");

		writer.writeStartElement("y:Path");
		writer.writeAttribute("sx", "0.0");
		writer.writeAttribute("sy", "0.0");
		writer.writeAttribute("tx", "0.0");
		writer.writeAttribute("ty", "0.0");
		writer.writeEndElement();

		writer.writeStartElement("y:LineStyle");
		writer.writeAttribute("color", "#000000");
		writer.writeAttribute("type", "line");
		writer.writeAttribute("width", "1.0");
		writer.writeEndElement();

		writer.writeStartElement("y:Arrows");
		writer.writeAttribute("source", "none");
		writer.writeAttribute("target", "standard");
		writer.writeEndElement();

		writer.writeStartElement("y:BendStyle");
		writer.writeAttribute("smoothed", "false");
		writer.writeEndElement();

		writer.writeEndElement();

		writer.writeEndElement();
		writer.writeEndElement(); 
	}

	private static void exportY(XMLStreamWriter writer, NodeComponent component) throws Exception
	{
		component.getNode().exportNodeHead(writer);
		writer.writeStartElement("data");
		writer.writeAttribute("key", "d0");
		writer.writeStartElement("y:ShapeNode");

		writer.writeStartElement("y:Geometry");
		writer.writeAttribute("height",""+component.getHeight());
		writer.writeAttribute("width",""+component.getWidth());
		writer.writeAttribute("x",""+component.getX());
		writer.writeAttribute("y",""+component.getY());
		writer.writeEndElement();	

		writer.writeStartElement("y:Fill");
		writer.writeAttribute("color","#FFFFFF");
		writer.writeAttribute("transparent","false");
		writer.writeEndElement();

		writer.writeStartElement("y:BorderStyle");
		writer.writeAttribute("color","#000000");
		writer.writeAttribute("type","line");
		writer.writeAttribute("width","1.0");
		writer.writeEndElement();

		writer.writeStartElement("y:NodeLabel");
		writer.writeAttribute("alignment","center");
		writer.writeAttribute("autoSizePolicy","content");
		writer.writeAttribute("fontFamily","Dialog");
		writer.writeAttribute("fontSize","13");
		writer.writeAttribute("fontStyle","plain");
		writer.writeAttribute("hasBackgroundColor","false");
		writer.writeAttribute("hasLineColor","false");
		writer.writeAttribute("height","20.0");
		writer.writeAttribute("modelName","internal");
		writer.writeAttribute("modelPosition","c");
		writer.writeAttribute("textColor","#000000");
		writer.writeAttribute("visible","true");
		writer.writeAttribute("width","12.0");
		writer.writeAttribute("x","9.0");
		writer.writeAttribute("y","4.9");
		writer.writeCharacters(component.getLabel());
		writer.writeEndElement();

		writer.writeStartElement("y:Shape");
		writer.writeAttribute("type","rectangle");
		writer.writeEndElement();	

		writer.writeEndElement();
		writer.writeEndElement();
		writer.writeEndElement();
	}









}
