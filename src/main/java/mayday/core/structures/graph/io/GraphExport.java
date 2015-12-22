package mayday.core.structures.graph.io;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import javax.xml.stream.XMLOutputFactory;
import javax.xml.stream.XMLStreamWriter;

import mayday.core.structures.graph.Edge;
import mayday.core.structures.graph.Graph;
import mayday.core.structures.graph.Node;

public class GraphExport 
{
	public static final String ROLE_KEY="role";
	public static final String NAME_KEY="name";
	public static final String WEIGHT_KEY="weight";
	public static final String CLASS_KEY="class";
	public static final String GEOMETRY_KEY="geometry";
	public static final String RENDERER_KEY="renderer";
	public static final String EDGES_KEY="edges";
	public static final String PROBES_KEY="probes";
	
	public static final String GRAPHML_TARGET="graphml";
	public static final String GRAPH_TARGET="graph";
	public static final String NODE_TARGET="node";
	public static final String EDGE_TARGET="edge";
	public static final String ALL_TARGET="all";
	
	
	public static void exportDot(Graph g, File f) throws IOException
	{
		BufferedWriter w=new BufferedWriter(new FileWriter(f));
		
		w.write("digraph g{\n");
		for(Edge e:g.getEdges())
		{
			w.write("\""+e.getSource().getName()+"\" -> \""+e.getTarget().getName()+"\";\n");
		}
		w.write("}\n");
		w.close();
	}
	
	public static void exportText(Graph g, File f) throws IOException
	{
		BufferedWriter w=new BufferedWriter(new FileWriter(f));
		if(g.getName()!=null)
		{
			w.write("# ");
			w.write(g.getName());
		}
		for(Edge e: g.getEdges())
		{
			w.write(e.getSource().getName());
			w.write("\t");
			w.write(e.getTarget().getName());
			w.write("\n");
		}
	}
	
	public static void exportGML(Graph g, File f) throws IOException
	{
		BufferedWriter w=new BufferedWriter(new FileWriter(f));
		w.write("Version 1 \n");
		w.write("graph [\n");
		w.write("Vendor \"Mayday\"\n");
		w.write("directed 1\n");
		if(g.getName()!=null)
			w.write("label \""+g.getName()+"\"\n");
		Map<Node, Integer> idMap=new HashMap<Node, Integer>();
		int nId=0;
		for(Node n:g.getNodes())
		{
			idMap.put(n, nId);
			w.write("node \n[");
			w.write("id " +nId+"\n");
			w.write("label "+"\""+n.getName()+"\"\n");
			w.write("]");
			nId++;
		}
		for(Edge e:g.getEdges())
		{
			w.write("edge \n[");
			w.write("source " +idMap.get(e.getSource())+"\n");
			w.write("target " +idMap.get(e.getTarget())+"\n");
			w.write("label "+"\""+e.getName()+"\"\n");
			w.write("]");			
		}
		w.write("]\n");
		w.close();
	}
	
	public static void exportGraphML(Graph g, File f) throws IOException, Exception
	{
		XMLOutputFactory xof = XMLOutputFactory.newInstance();
        XMLStreamWriter writer =xof.createXMLStreamWriter(new FileWriter(f));
      
        writer.writeStartDocument("utf-8", "1.0");
		writer.writeStartElement("graphml");
		writer.writeAttribute("xmlns", "http://graphml.graphdrawing.org/xmlns");
		writer.writeAttribute("xmlns:xsi", "http://www.w3.org/2001/XMLSchema-instance");
		writer.writeAttribute("xsi:schemaLocation", "http://graphml.graphdrawing.org/xmlns http://graphml.graphdrawing.org/xmlns/1.0/graphml.xsd");
		
		writeDefaultAttributes(writer);		
		writer .writeComment("Created by Mayday");
		g.export(writer);
		writer.writeEndDocument();
		writer.flush();
		writer.close();
	}
	
	protected static void writeDefaultAttributes(XMLStreamWriter writer) throws Exception
	{
		writeAttribute(writer, ROLE_KEY, ALL_TARGET, ROLE_KEY);
		writeAttribute(writer, NAME_KEY, ALL_TARGET, NAME_KEY);
		writeAttribute(writer, WEIGHT_KEY, EDGE_TARGET, WEIGHT_KEY);
		writeAttribute(writer, CLASS_KEY, ALL_TARGET, CLASS_KEY);
		writeAttribute(writer, GEOMETRY_KEY, NODE_TARGET, GEOMETRY_KEY);
	}
	
	public static void writeAttribute(XMLStreamWriter writer, String id, String target, String name) throws Exception
	{
		writeAttribute(writer, id, target, name,null);	
	}
	
	public static void writeAttribute(XMLStreamWriter writer, String id, String target, String name, String defaultValue) throws Exception
	{
		writer.writeStartElement("key");
		writer.writeAttribute("id", id);
		writer.writeAttribute("for", target);
		writer.writeAttribute("attr.name", name);
		writer.writeAttribute("attr.type","string");
		
		if(defaultValue!=null)
		{
			writer.writeStartElement("default");
			writer.writeCharacters(defaultValue);
			writer.writeEndElement();
		}
		writer.writeEndElement();
	}
	public static void writeDataElement(XMLStreamWriter writer, String key, String value) throws Exception
	{
		writer.writeStartElement("data");
		writer.writeAttribute("key", key);
		writer.writeCharacters(value);
		writer.writeEndElement();
	}	
}


