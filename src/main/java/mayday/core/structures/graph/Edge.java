package mayday.core.structures.graph;

import java.util.HashMap;
import java.util.Map;

import javax.xml.stream.XMLStreamWriter;

import mayday.core.XMLTools;
import mayday.core.structures.Pair;
import mayday.core.structures.graph.io.GraphMLExport;

/**
 * An edge (arc) connecting two nodes of a graph. The Edge is defined to be directed,
 * as the edge keeps a source and a target node. Any edge can have a name, a weight and
 * a role. 
 * @author Stephan Symons
 * @see Node
 * @see Graph
 */
public class Edge implements EditableGraphComponent, Cloneable, Comparable<Edge>
{
	/** Source node of the Edge */
	protected Node source;
	/** Target node of the Edge */
	protected Node target;	
	/** Name of the edge */
	protected String name;
	/** Weight of the edge */
	protected double weight;
	/** Role of the edge */
	protected String role=new String(); 
	/** Properties associated with the edge.  */
	protected Map<String,String> properties; 

	/** Tags for XML and string representation**/
	public final static String EDGE_START_TAG = "<Edge>";
	public final static String EDGE_END_TAG = "</Edge>";
	protected final static String SOURCE_START_TAG = "<Source>";
	protected final static String SOURCE_END_TAG = "</Source>";
	protected final static String TARGET_START_TAG = "<Target>";
	protected final static String TARGET_END_TAG = "</Target>";
	protected final static String ROLE_START_TAG = "<Role>";
	protected final static String ROLE_END_TAG = "</Role>";
	protected final static String NAME_START_TAG = "<Name>";
	protected final static String NAME_END_TAG = "</Name>";
	protected final static String WEIGHT_START_TAG = "<Weight>";
	protected final static String WEIGHT_END_TAG = "</Weight>";
	public final static String EDGE_STRING = "Edge";
	protected final static String SOURCE_STRING = "Source: ";
	protected final static String TARGET_STRING = "Target: ";
	protected final static String ROLE_STRING = "Role: ";
	protected final static String NAME_STRING = "Name: ";
	protected final static String WEIGHT_STRING = "Weight: ";

	/**
	 * Creates a new Edge connecting no nodes with empty name and unit weight.
	 */
	protected Edge()
	{
		name="";
		weight=1.0;
	}

	/**
	 * Constructs an edge targeting a node.
	 * @param target target node
	 */
	public Edge(Node source, Node target)
	{	
		name="";
		weight=1.0;
		this.source=source;
		this.target=target;
	}

	/**
	 * Constructs an edge targeting a node with a weight
	 * @param target target node
	 * @param weight edge weight
	 */
	public Edge(Node target, double weight)
	{	
		this.target=target;
		this.weight=weight;
	}

	/**
	 * @return the target node 
	 */
	public Node getTarget() {
		return target;
	}

	/**
	 * @param target the target to set
	 */
	public void setTarget(Node target) {
		this.target = target;
	}

	/**
	 * @return the edge weight
	 */
	public double getWeight() {
		return weight;
	}

	/**
	 * @param weight the weight to set
	 */
	public void setWeight(double weight) {
		this.weight = weight;
	}

	/**
	 * @return the source
	 */
	public Node getSource() {
		return source;
	}

	/**
	 * @param source the source to set
	 */
	public void setSource(Node source) {
		this.source = source;
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
	 * @see java.lang.Object#equals(java.lang.Object)
	 */
	@Override
	public boolean equals(Object e)
	{
		if(e==this)
			return true;
		if(! (e instanceof Edge)) return false;		
		return (((Edge)e).getSource()==this.source && 
				((Edge)e).getTarget()==this.target && 
				((Edge)e).getName().equals(this.name) && 
				((Edge)e).getWeight()==this.weight);

	}

	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString()
	{
		return source +"->"+target;
	}

	/* (non-Javadoc)
	 * @see java.lang.Object#hashCode()
	 */
	@Override
	public int hashCode() 
	{
		return super.hashCode();
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
	public void setRole(String role) {
		this.role = role;
	}

	public void export(XMLStreamWriter writer) throws Exception 
	{
		writer.writeStartElement("edge");
		writer.writeAttribute("id", "edge"+hashCode());
		writer.writeAttribute("source", "node"+source.hashCode());
		writer.writeAttribute("target", "node"+target.hashCode());

		GraphMLExport.writeDataElement(writer, GraphMLExport.NAME_KEY, getName());
		GraphMLExport.writeDataElement(writer, GraphMLExport.ROLE_KEY, getRole());
		GraphMLExport.writeDataElement(writer, GraphMLExport.WEIGHT_KEY, ""+getWeight());
		GraphMLExport.writeDataElement(writer, GraphMLExport.CLASS_KEY, getClass().getCanonicalName());

		if(properties!=null){
			for(String key:properties.keySet())
			{
				GraphMLExport.writeDataElement(writer, key, properties.get(key));
			}
		}
		writer.writeEndElement();

	}

	public void exportEdgeHead (XMLStreamWriter writer) throws Exception 
	{
		writer.writeStartElement("edge");
		writer.writeAttribute("id", "edge"+hashCode());
		writer.writeAttribute("source", "node"+source.hashCode());
		writer.writeAttribute("target", "node"+target.hashCode());

		GraphMLExport.writeDataElement(writer, GraphMLExport.NAME_KEY, getName());
		GraphMLExport.writeDataElement(writer, GraphMLExport.ROLE_KEY, getRole());
		GraphMLExport.writeDataElement(writer, GraphMLExport.WEIGHT_KEY, ""+getWeight());
		GraphMLExport.writeDataElement(writer, GraphMLExport.CLASS_KEY, getClass().getCanonicalName());

		if(properties!=null){
			for(String key:properties.keySet())
			{
				GraphMLExport.writeDataElement(writer, key, properties.get(key));
			}
		}
	}

	/* (non-Javadoc)
	 * @see mayday.core.structures.graph.EditableGraphComponent#getProperties()
	 */
	public Map<String, String> getProperties() 
	{
		if(properties==null)
			properties=new HashMap<String, String>();
		return properties;
	}

	public void addProperty(String key, String value)
	{
		if(properties==null)
			properties=new HashMap<String, String>();
		properties.put(key, value);
	}

	/* (non-Javadoc)
	 * @see mayday.core.structures.graph.EditableGraphComponent#setProperties(java.util.Map)
	 */
	public void setProperties(Map<String, String> properties) {
		this.properties = properties;
	}



	/* (non-Javadoc)
	 * @see java.lang.Object#clone()
	 */
	public Edge clone() 
	{
		Edge clone=new Edge();
		clone.setSource(source);
		clone.setTarget(target);
		clone.setProperties(properties);
		clone.setWeight(weight);
		clone.setRole(role);
		clone.setName(name);
		return clone;
	}

	public void serializeXML(StringBuilder ret) {
		ret.append(EDGE_START_TAG);
		if(source!=null) {
			ret.append(SOURCE_START_TAG+XMLTools.xmlize(""+source.getID())+SOURCE_END_TAG);
		}
		if(target!=null) {
			ret.append(TARGET_START_TAG+XMLTools.xmlize(""+target.getID())+TARGET_END_TAG);
		}
		if(role!=null) {
			ret.append(ROLE_START_TAG+XMLTools.xmlize(""+role)+ROLE_END_TAG);
		}
		ret.append(NAME_START_TAG+XMLTools.xmlize(""+name)+NAME_END_TAG);
		ret.append(WEIGHT_START_TAG+XMLTools.xmlize(""+weight)+WEIGHT_END_TAG);
		ret.append(EDGE_END_TAG);
	}

	/**
	 * Deserialize XML MIO serialization stuff. 
	 * @param XML an
	 * @return
	 */
	public Pair<Integer,Integer> deSerializeXML(String XML) {
		Object[] ret = new Object[]{null,0};

		ret = XMLTools.nextSubstring(XML, SOURCE_START_TAG, SOURCE_END_TAG, (Integer)ret[1]);
		String sourceString=null;
		if (ret[0]!=null) {
			sourceString = (String)ret[0];
			sourceString=XMLTools.unxmlize(sourceString);
		}
		ret = XMLTools.nextSubstring(XML, TARGET_START_TAG, TARGET_END_TAG, (Integer)ret[1]);
		if (ret[0]==null) {
			return null;
		}
		String targetString = (String)ret[0];
		targetString=XMLTools.unxmlize(targetString);

		ret = XMLTools.nextSubstring(XML, ROLE_START_TAG, ROLE_END_TAG, (Integer)ret[1]);
		if (ret[0]!=null) {
			String roleString = (String)ret[0];
			this.role=XMLTools.unxmlize(roleString);
		}

		ret = XMLTools.nextSubstring(XML, NAME_START_TAG, NAME_END_TAG, (Integer)ret[1]);
		if (ret[0]!=null) {
			String nameString = (String)ret[0];
			this.name=XMLTools.unxmlize(nameString);
		}

		ret = XMLTools.nextSubstring(XML, WEIGHT_START_TAG, WEIGHT_END_TAG, (Integer)ret[1]);
		if (ret[0]!=null) {
			String weightString = (String)ret[0];
			this.weight=Double.valueOf(XMLTools.unxmlize(weightString));
		}

		return new Pair<Integer,Integer>(Integer.valueOf(sourceString),Integer.valueOf(targetString));


	}

	/**
	 * Serialize the edge to XML. 
	 * This does not produce GraphML. This is only acceptable for MIO serialization
	 * @param ret an xml string representing this node. 
	 */
	public void serializeString(StringBuilder ret) {
		ret.append(EDGE_STRING+"\n");
		if(source!=null) {
			ret.append(SOURCE_STRING+source.getID()+"\n");
		}
		if(target!=null) {
			ret.append(TARGET_STRING+target.getID()+"\n");
		}
		if(role!=null) {
			ret.append(ROLE_STRING+role+"\n");
		}
		ret.append(NAME_STRING+name+"\n");
		ret.append(WEIGHT_STRING+weight+"\n");
	}

	/**
	 * De-serializes MIO-serialization from string 
	 * @param currentString
	 * @return 
	 */
	public Pair<Integer, Integer> deSerializeString(String currentString) {
		String[] splits = currentString.split("\n");
		int sourceID=-1;
		int targetID=-1;
		for (String s : splits) {
			if(s.startsWith(SOURCE_STRING)) {
				sourceID=Integer.valueOf(s.replaceFirst(SOURCE_STRING, ""));
			}
			else if(s.startsWith(TARGET_STRING)) {
				targetID=Integer.valueOf(s.replaceFirst(TARGET_STRING, ""));
			}
			else if(s.startsWith(ROLE_STRING)) {
				this.role=s.replaceFirst(ROLE_STRING, "");
			}
			else if(s.startsWith(NAME_STRING)) {
				this.name=s.replaceFirst(NAME_STRING, "");
			}
			else if(s.startsWith(WEIGHT_STRING)) {
				this.weight=Double.valueOf(s.replaceFirst(WEIGHT_STRING, ""));
			}
		}
		return new Pair<Integer,Integer>(sourceID,targetID);
	}

	/* (non-Javadoc)
	 * @see java.lang.Comparable#compareTo(java.lang.Object)
	 */
	@Override
	public int compareTo(Edge o) 
	{
		int weightDifference =  (int)(weight - o.weight);

		if(weightDifference != 0) 
			return weightDifference<0?-1:1;

		int c1 = 0;
		if (source!=null && o.source!=null)
			c1 = source.compareTo(o.source);
		if (c1==0 && target!=null && o.target!=null)
			c1 = target.compareTo(o.target);

		return c1;


	}

}
