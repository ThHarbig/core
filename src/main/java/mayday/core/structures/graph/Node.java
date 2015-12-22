package mayday.core.structures.graph;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import javax.xml.stream.XMLStreamWriter;

import mayday.core.XMLTools;
import mayday.core.structures.graph.io.GraphMLExport;

/**
 * Node class for graphs ({@link mayday.graph.Graph}. Nodes have a name, bounds
 * for visualization purposes. Nodes can fixed to suppress movement on screen. 
 * Two nodes are considered equal, if their names are equal. This is enforced
 * by means of {@code equals} function. 
 * @author Stephan Symons
 *
 */
public class Node implements Cloneable, Comparable<Node>
{
	/** the name of the node */
	protected String name;	
	/** the graph the node belongs to */
	protected Graph graph;
	/** the role, i.e. the function of the node */
	protected String role=new String();
	
	/** the id of the node */
	protected int id=hashCode();
	
	/** Tags for XML and string representation**/
	protected final static String ID_START_TAG = "<Id>";
	protected final static String ID_END_TAG = "</Id>";
	public final static String NODE_START_TAG = "<Node>";
	public final static String NODE_END_TAG = "</Node>";
	protected final static String ROLE_START_TAG = "<Role>";
	protected final static String ROLE_END_TAG = "</Role>";
	protected final static String NAME_START_TAG = "<Name>";
	protected final static String NAME_END_TAG = "</Name>";
	protected final static String ID_STRING = "Id: ";
	protected final static String NAME_STRING = "Name: ";
	protected final static String ROLE_STRING = "Role: ";
	public final static String NODE_STRING = "Node";
	
	/**
	 * Construct empty node
	 */
	public Node(Graph graph)
	{		
		name="";
		this.graph=graph;
		
	}
	
	/**
	 * Construct a node with name name
	 * @param name The name to set.
	 */
	public Node(Graph graph, String name)
	{
		this.name=name;
		this.graph=graph;
	}
	
	/**
	 * Construct a node with name and role
	 * @param graph
	 * @param name
	 * @param role
	 */
	public Node(Graph graph,String name, String role) 
	{
		this.name=name;
		this.graph=graph;
		setRole(role);
	}
	
	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString()
	{
		return name;
	}

	/**
	 * @return the name
	 */
	public String getName() {
		return name;
	}

	/**
	 * @param name the name to set
	 */
	public void setName(String name) {
		this.name = name;
	}


	/* (non-Javadoc)
	 * @see java.lang.Object#clone()
	 */
	@Override
	public Object clone()
	{
		Node clone=new Node(graph);
		clone.setName(new String(name));
		return clone;		
	}

	/**
	 * @return the graph
	 */
	public Graph getGraph() {
		return graph;
	}

	/**
	 * @param graph the graph to set
	 */
	public void setGraph(Graph graph) {
		this.graph = graph;
	}
	
	/**
	 * @return The edges leaving this node
	 */
	public List<Edge> getOutEdges()
	{
		return graph.getEdges(this);
	}

	/**
	 * @return The edges arriving this node
	 */
	public List<Edge> getInEdges()
	{
		List<Edge> res=new ArrayList<Edge>();
		for(Edge e:graph.getEdges())
		{
			if(e.getTarget().equals(this)) 
				res.add(e);
		}
		return res;
	}
		
	/**
	 * @return All edges adjacent to this node.
	 */
	public List<Edge> getEdges()
	{
		List<Edge> res=new ArrayList<Edge>();
		res.addAll(getInEdges());
		res.addAll(getOutEdges());
		return res;
	}
	
	/**
	 * @return All nodes adjacent to this node.
	 */
	public Set<Node> getNeighbors()
	{
		return graph.getNeighbors(this);
	}
	
	/**
	 * The number of edges leaving this node
	 * @param node
	 * @return
	 */
	public int getOutDegree()
	{
		return graph.getOutDegree(this);
	}
	
	/**
	 * The number of edges arriving the node
	 * @param node
	 * @return
	 */
	public int getInDegree()
	{
		return graph.getInDegree(this);
	}
	
	/**
	 * The number of all edges either starting or ending at a node.
	 * @param node
	 * @return
	 */
	public int getDegree()
	{
		return getInDegree()+getOutDegree();
	}

	public int compareTo(Node o) 
	{
		return name.compareTo(o.getName());
	}

	/**
	 * @return the role
	 */
	public String getRole() {
		return role;
	}

	/**
	 * @param role the role to set
	 */
	public void setRole(String role) 
	{
		if(role!=null)
			this.role = role;
	}
	
	

	public void export(XMLStreamWriter writer) throws Exception 
	{
		exportNodeHead(writer);
		writer.writeEndElement();
	}
	
	public void exportNodeHead(XMLStreamWriter writer) throws Exception 
	{
		writer.writeStartElement("node");
		writer.writeAttribute("id", getXMLExportId());
		
		GraphMLExport.writeDataElement(writer, GraphMLExport.NAME_KEY, getName());
		GraphMLExport.writeDataElement(writer, GraphMLExport.ROLE_KEY, getRole());
		GraphMLExport.writeDataElement(writer, GraphMLExport.CLASS_KEY, getClass().getCanonicalName());
	}
	
	public String getXMLExportId()
	{
		return "node"+hashCode();
	}
	
	/**
	 * @return the node id 
	 */
	public int getID() {
		return id;
	}
	
	/**
	 * Sets the node id
	 * @param id
	 */
	public void setID(int id) {
		this.id=id;
	}

	/**
	 * Serialize the node to XML. 
	 * This does not produce GraphML. This is only acceptable for MIO serialization
	 * @param ret an xml string representing this node. 
	 */
	public void serializeXML(StringBuilder ret) {
		ret.append(NODE_START_TAG);
		ret.append(ID_START_TAG+XMLTools.xmlize(""+getID())+ID_END_TAG);
		if(role!=null) {
			ret.append(ROLE_START_TAG+XMLTools.xmlize(""+role)+ROLE_END_TAG);
		}
		ret.append(NAME_START_TAG+XMLTools.xmlize(""+name)+NAME_END_TAG);
		ret.append(NODE_END_TAG);
	}
	
	public int deSerializeXML(String XML) {
		Object[] ret = new Object[]{null,0};
		ret = XMLTools.nextSubstring(XML, ID_START_TAG, ID_END_TAG, (Integer)ret[1]);
		if (ret[0]==null) {
			return -1;
		}
		String idString = (String)ret[0];
		idString=XMLTools.unxmlize(idString);
		
		ret = XMLTools.nextSubstring(XML, ROLE_START_TAG, ROLE_END_TAG, (Integer)ret[1]);
		if (ret[0]!=null) {
			String roleString = (String)ret[0];
			roleString=XMLTools.unxmlize(roleString);
			this.role=roleString;
		}
		ret = XMLTools.nextSubstring(XML, NAME_START_TAG, NAME_END_TAG, (Integer)ret[1]);
		if (ret[0]!=null) {
			String nameString = (String)ret[0];
			nameString=XMLTools.unxmlize(nameString);
			this.name=nameString;
		}
		id=Integer.valueOf(idString);
		return id;
		
		
	}
	
	public void serializeString(StringBuilder ret) {
		ret.append(NODE_STRING+"\n");
		ret.append(ID_STRING+getID()+"\n");
		if(role!=null) {
			ret.append(ROLE_STRING+role+"\n");
		}
		ret.append(NAME_STRING+name+"\n");
	}

	public int deSerializeString(String currentString) {
		String[] splits = currentString.split("\n");
		for (String s : splits) {
			if(s.startsWith(ID_STRING)) {
				id=Integer.valueOf(s.replaceFirst(ID_STRING, ""));
			}
			else if(s.startsWith(ROLE_STRING)) {
				this.role=s.replaceFirst(ROLE_STRING, "");
			}
			else if(s.startsWith(NAME_STRING)) {
				this.name=s.replaceFirst(NAME_STRING, "");
			}
		}
		return id;
	}
	
	
	
}
