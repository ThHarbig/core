package mayday.core.structures.graph;

import java.util.Map;

public interface EditableGraphComponent 
{
	public void setName(String name);
	public void setRole(String role);
	public void setProperties(Map<String, String> properties);
	
	public String getName();
	public String getRole();
	public Map<String, String> getProperties();
}
