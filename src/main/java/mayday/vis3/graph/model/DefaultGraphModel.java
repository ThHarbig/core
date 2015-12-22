package mayday.vis3.graph.model;

import java.util.Collections;
import java.util.List;

import mayday.core.structures.graph.Graph;
import mayday.core.structures.graph.Graphs;
import mayday.core.structures.graph.Node;
import mayday.vis3.graph.components.CanvasComponent;
import mayday.vis3.graph.components.NodeComponent;

public class DefaultGraphModel extends GraphModel
{
	public DefaultGraphModel(Graph graph) 
	{
		super(graph);
	}
	
	protected void init()
	{
		clear();
		for(Node n:getGraph().getNodes())
		{
			CanvasComponent comp=new NodeComponent(n);
			addComponent(comp);	
			getNodeMap().put(comp, n);
			getComponentMap().put(n, comp);			
		}
		Collections.sort(getComponents());
	}
	
	@Override
	public GraphModel buildSubModel(List<Node> selectedNodes) 
	{
		Graph g=Graphs.restrict(getGraph(), selectedNodes);
		return new DefaultGraphModel(g);
	}
}
