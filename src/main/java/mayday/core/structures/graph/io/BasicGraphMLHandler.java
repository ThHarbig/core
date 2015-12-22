package mayday.core.structures.graph.io;

import java.io.FileReader;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import mayday.core.Probe;
import mayday.core.structures.graph.Edge;
import mayday.core.structures.graph.Graph;
import mayday.core.structures.graph.Node;

import org.xml.sax.Attributes;
import org.xml.sax.ContentHandler;
import org.xml.sax.EntityResolver;
import org.xml.sax.InputSource;
import org.xml.sax.Locator;
import org.xml.sax.SAXException;
import org.xml.sax.XMLReader;
import org.xml.sax.helpers.XMLReaderFactory;

public class BasicGraphMLHandler implements ContentHandler 
{
	private static final String ID="id";


	private Map<String, Map<String,String>> graphs;
	private Map<String, Map<String,String>> nodes;
	private Map<String, Map<String,String>> edges;

	private Map<String, Map<String,Map<String,String>>> graphNodes;
	private Map<String, Map<String,Map<String,String>>> graphEdges; 

	private boolean isNode;
	private boolean isEdge;
	private boolean isGraph;

	//	private boolean isKey;
	//	private boolean isData;
	private String currentDataName;
	//	private String currentData;

	private Map<String,String> nodeAttributes;
	private Map<String,String> edgeAttributes;
	private Map<String,String> graphAttributes;

	String characters;
	String currentDefault;
	Key key;

	public void characters(char[] ch, int start, int length) throws SAXException 
	{
		characters=String.copyValueOf(ch, start, length).trim();
	}

	public void endPrefixMapping(String prefix) throws SAXException {}
	public void ignorableWhitespace(char[] ch, int start, int length) throws SAXException {}
	public void processingInstruction(String target, String data) throws SAXException {}
	public void setDocumentLocator(Locator locator){}
	public void skippedEntity(String name) throws SAXException{}
	public void startPrefixMapping(String prefix, String uri) throws SAXException {}


	public void startDocument() throws SAXException 
	{
		graphs=new HashMap<String, Map<String,String>>();
		nodes=new HashMap<String, Map<String,String>>();
		edges=new HashMap<String, Map<String,String>>();
		new HashMap<String, Key>();
		graphNodes=new HashMap<String, Map<String,Map<String,String>>>();
		graphEdges=new HashMap<String, Map<String,Map<String,String>>>();
	}

	public void endElement(String uri, String localName, String name) throws SAXException 
	{
		if(name.equals("node"))
		{
			isNode=false;
			nodes.put(nodeAttributes.get(ID), nodeAttributes);
			return;
		}

		if(name.equals("edge"))
		{
			isEdge=false;
			edges.put(edgeAttributes.get(ID), edgeAttributes);
			return;
		}

		if(name.equals("graph"))
		{
			isGraph=false;		
			graphs.put(graphAttributes.get(ID), graphAttributes);
			graphNodes.put(graphAttributes.get(ID), nodes);
			graphEdges.put(graphAttributes.get(ID), edges);
			return;
		}

		if(name.equals("default"))
		{
			currentDefault=characters;	
			return;
		}

		if(name.equals("key"))
		{
			if(currentDefault!=null)
				key.defaultValue=currentDefault;
			currentDefault=null;
			return;
		}


		if(name.equals("data"))
		{
			if(isNode)
			{
				nodeAttributes.put(currentDataName, characters);
				return;
			}
			if(isEdge)
			{
				edgeAttributes.put(currentDataName,characters);
				return;
			}
			if(isGraph)
			{
				graphAttributes.put(currentDataName, characters);
			}
			return;
		}
		// none of the above
		if(!characters.isEmpty())
		{
			if(isNode)
			{
				nodeAttributes.put(name, characters);
				return;
			}	
			if(isEdge)
			{
				edgeAttributes.put(name, characters);
				return;			
			}
			if(isGraph)
			{
				graphAttributes.put(name, characters);
			}
		}
	}

	public void startElement(String uri, String localName, String name, Attributes atts) throws SAXException 
	{
		if(name.equals("graph"))
		{
			isGraph=true;
			graphAttributes=new HashMap<String, String>();
			graphAttributes.put(ID,atts.getValue(ID) );

		}
		if(name.equals("node"))
		{
			isNode=true;
			nodeAttributes=new HashMap<String, String>();
			nodeAttributes.put(ID, atts.getValue(ID));
		}

		if(name.equals("edge"))
		{
			isEdge=true;
			edgeAttributes=new HashMap<String, String>();
			edgeAttributes.put(ID, atts.getValue(ID));
			edgeAttributes.put("source", atts.getValue("source"));
			edgeAttributes.put("target", atts.getValue("target"));

		}
		if(name.equals("key"))
		{
			key=new Key();
			key.id=atts.getValue(ID);
			key.name=atts.getValue("attr.name");
			key.target=atts.getValue("for");


		}

		if(name.equals("data"))
		{
			//			isData=true;
			currentDataName=atts.getValue("key");
		}

		if(name.equals("y:Geometry"))
		{
			if(isNode)
			{
				String geom=atts.getValue("x")+","+atts.getValue("y")+","+atts.getValue("width")+","+atts.getValue("height");
				nodeAttributes.put(GraphMLExport.GEOMETRY_KEY, geom);
			}
		}


	}

	public void endDocument() throws SAXException 
	{
	}

	public List<Graph> getGraphs(Collection<Probe> probes)
	{
		GraphFactory factory=new GraphFactory(probes);
		return getGraphs(probes, factory);
	}


	public List<Graph> getGraphs(Collection<Probe> probes, GraphFactory factory)
	{
		List<Graph> res=new ArrayList<Graph>();
		for(Map<String, String> graphAtt: graphs.values())
		{
			String graphId=graphAtt.get(ID);
			Graph graph=factory.produceGraph(graphAtt.get(GraphMLExport.CLASS_KEY));
			if(graphAtt.containsKey(GraphMLExport.NAME_KEY))
				graph.setName(graphAtt.get(GraphMLExport.NAME_KEY));


			Map<String, Node> idNodeMap=new HashMap<String, Node>();

			for(Map<String, String> nodeAtt: graphNodes.get(graphId).values())
			{
				Node n=factory.produceNode(nodeAtt, graph);
				n.setName(nodeAtt.get(GraphMLExport.NAME_KEY));
				// try to give the node a name. if no name could be assigned, try the yed node label
				if(n.getName()==null && nodeAtt.containsKey("y:NodeLabel"))
					n.setName(nodeAtt.get("y:NodeLabel"));
				// still no name? use id!
				if(n.getName()==null)
					n.setName(nodeAtt.get("id"));

				n.setRole(nodeAtt.get(GraphMLExport.ROLE_KEY));
				graph.addNode(n);
				idNodeMap.put(nodeAtt.get(ID), n);
			}
			for(Map<String, String> edgeAtt: graphEdges.get(graphId).values())
			{
				Node source=idNodeMap.get(edgeAtt.get("source"));
				Node target=idNodeMap.get(edgeAtt.get("target"));
				Edge e= factory.produceEdge(edgeAtt.get(GraphMLExport.CLASS_KEY), source,target);
				if(edgeAtt.containsKey(GraphMLExport.NAME_KEY))
					e.setName(edgeAtt.get(GraphMLExport.NAME_KEY));
				if(edgeAtt.containsKey(GraphMLExport.ROLE_KEY))
					e.setRole(edgeAtt.get(GraphMLExport.ROLE_KEY));
				if(edgeAtt.containsKey(GraphMLExport.WEIGHT_KEY))
					e.setWeight(Double.parseDouble(edgeAtt.get(GraphMLExport.WEIGHT_KEY)));
				graph.connect(e);
			}
			res.add(graph);
		}
		return res;
	}



	public static void main(String[] args) throws Exception 
	{
		String file="/home/symons/small2.graphml";


		XMLReader parser;
		parser = XMLReaderFactory.createXMLReader();
		parser.setEntityResolver(new EntityResolver() {
			public InputSource resolveEntity(String publicID, String systemID)
			throws SAXException {
				return new InputSource(new StringReader(""));
			}
		}
		);
		BasicGraphMLHandler handler=new BasicGraphMLHandler();
		parser.setContentHandler(handler);
		parser.parse(new InputSource(new FileReader(file)));
		handler.getGraphs(new ArrayList<Probe>());
	}


	private class Key
	{
		String id;
		String target;
		String name;
		String defaultValue;

		@Override
		public String toString() 
		{
			return id+":"+name+" "+target+" default:"+defaultValue;
		}
	}

	public Map<String, Map<String, String>> getGraphs() {
		return graphs;
	}

	public void setGraphs(Map<String, Map<String, String>> graphs) {
		this.graphs = graphs;
	}

	public Map<String, Map<String, String>> getNodes() {
		return nodes;
	}

	public void setNodes(Map<String, Map<String, String>> nodes) {
		this.nodes = nodes;
	}

	public Map<String, Map<String, String>> getEdges() {
		return edges;
	}

	public void setEdges(Map<String, Map<String, String>> edges) {
		this.edges = edges;
	}

	public Map<String, Map<String, Map<String, String>>> getGraphNodes() {
		return graphNodes;
	}

	public void setGraphNodes(
			Map<String, Map<String, Map<String, String>>> graphNodes) {
		this.graphNodes = graphNodes;
	}

	public Map<String, Map<String, Map<String, String>>> getGraphEdges() {
		return graphEdges;
	}

	public void setGraphEdges(
			Map<String, Map<String, Map<String, String>>> graphEdges) {
		this.graphEdges = graphEdges;
	}



}
