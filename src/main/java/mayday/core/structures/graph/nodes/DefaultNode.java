package mayday.core.structures.graph.nodes;

import java.util.HashMap;
import java.util.Map;

import javax.xml.stream.XMLStreamWriter;

import mayday.core.structures.graph.EditableGraphComponent;
import mayday.core.structures.graph.Graph;
import mayday.core.structures.graph.Node;
import mayday.core.structures.graph.io.GraphMLExport;

/**
 * Default node class. It can carry a set of properties in key-value form.  
 * @author Stephan Symons
 *
 */
public class DefaultNode extends Node implements EditableGraphComponent
{
	public static final String NAME_KEY="name";
	public static final String ROLE_KEY="role";
	
	private Map<String, String> properties=new HashMap<String, String>();
	
	/**
	 * @param graph
	 * @param name
	 * @param role
	 */
	public DefaultNode(Graph graph, String name, String role) 
	{
		super(graph, name, role);
	}

	/**
	 * @param graph
	 * @param properties
	 */
	public DefaultNode(Graph graph, Map<String, String> properties) 
	{
		super(graph);
		this.properties = properties;
		if(properties.containsKey(NAME_KEY))
			setName(properties.get(NAME_KEY));
		if(properties.containsKey(ROLE_KEY))
			setName(properties.get(ROLE_KEY));
				
	}

	/**
	 * 
	 * @param graph
	 * @param name
	 */
	public DefaultNode(Graph graph, String name) 
	{
		super(graph, name);
	}

	/**
	 * Create a new blank node with empty name and role. It belongs to graph graph. 
	 * @param graph
	 */
	public DefaultNode(Graph graph) 
	{
		super(graph);
	}
	
	public boolean hasProperty(String key)
	{
		return properties.containsKey(key);
	}
	
	/**
	 * Get the value to the property with key "key"
	 * @param key
	 * @return
	 */
	public String getPropertyValue(String key)
	{
		return properties.get(key);
	}

	/**
	 * Get all the properties. 
	 * @return
	 */
	public Map<String, String> getProperties() 
	{
		return properties;
	}
	
	/**
	 * Set property "key" to the value "value". 
	 * @param key
	 * @param value
	 */
	public void setProperty(String key, String value) 
	{
		properties.put(key, value);
	}
	
	
	public void exportNodeHead(XMLStreamWriter writer) throws Exception 
	{
		super.exportNodeHead(writer);
		
		for(String key:properties.keySet())
		{
			GraphMLExport.writeDataElement(writer, key, properties.get(key));
		}	
	
	}
	
	public void setProperties(Map<String, String> properties) 
	{
		this.properties = properties;
	}

	
}
