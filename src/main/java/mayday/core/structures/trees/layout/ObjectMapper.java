package mayday.core.structures.trees.layout;

import mayday.core.structures.trees.tree.Node;

public interface ObjectMapper {
	
	public Node getNode(Object o);
	
	public Object getObject(Node o);

	public String getLabel(Node n);

}
