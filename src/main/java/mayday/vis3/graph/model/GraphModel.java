package mayday.vis3.graph.model;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import mayday.core.structures.graph.Edge;
import mayday.core.structures.graph.Graph;
import mayday.core.structures.graph.Node;
import mayday.core.structures.graph.edges.Edges;
import mayday.vis3.graph.components.CanvasComponent;
import mayday.vis3.graph.model.GraphModelEvent.GraphModelChange;

public abstract class GraphModel
{
	protected Graph graph;
	protected List<CanvasComponent> components;
	protected Map<CanvasComponent,Node> nodeMap;
	protected Map<Node,CanvasComponent> componentMap;

	private List<GraphModelListener> listeners=new ArrayList<GraphModelListener>();
	private boolean silent=false;
	
	
	public GraphModel()
	{
		graph=new Graph();
		clear();
		init();
	}

	public GraphModel(Graph graph)
	{
		this.graph=graph;	
		clear();
		init();
	}

	protected abstract void init();

	protected void clear()
	{
		components=new ArrayList<CanvasComponent>();
		nodeMap=new HashMap<CanvasComponent, Node>();
		componentMap=new HashMap<Node, CanvasComponent>();		
	}

	public void clearAll()
	{
		graph.clear();		
		fireEvent(new GraphModelEvent(GraphModelChange.ComponentsRemoved,components));
		clear();
	}

	public void remove(CanvasComponent comp)
	{
		components.remove(comp);
		componentMap.remove(nodeMap.get(comp));

		Node n=nodeMap.get(comp);
		if(n!=null)
			graph.removeNode(n);

		nodeMap.remove(comp);		
		fireEvent(new GraphModelEvent(GraphModelChange.ComponentsRemoved,comp));
	}

	public int componentCount()
	{
		return components.size();
	}

	public Set<Edge> getEdges()
	{
		return graph.getEdges();
	}

	/**
	 * @return the components
	 */
	public List<CanvasComponent> getComponents() 
	{
		return components;
	}

	public void addComponent(CanvasComponent component)
	{
		components.add(component);
		fireEvent(new GraphModelEvent(GraphModelChange.ComponentsAdded,component));
	}


	public void addComponent(List<CanvasComponent> components)
	{
		this.components.addAll(components);
		fireEvent(new GraphModelEvent(GraphModelChange.ComponentsAdded,components));
	}

	public Node getNode(CanvasComponent component)
	{
		return nodeMap.get(component);
	}

	public CanvasComponent getComponent(Node node)
	{	
		return componentMap.get(node);
	}

	/**
	 * @return the graph
	 */
	public Graph getGraph()
	{
		return graph;
	}

	/**
	 * @param graph the graph to set
	 */
	protected void setGraph(Graph graph) 
	{
		this.graph = graph;		
	}

	/**
	 * @return the nodeMap
	 */
	public Map<CanvasComponent, Node> getNodeMap() 
	{
		return nodeMap;
	}

	/**
	 * @return the componentMap
	 */
	public Map<Node, CanvasComponent> getComponentMap() 
	{
		return componentMap;
	}

	public Edge connect(CanvasComponent comp1, CanvasComponent comp2)
	{
		Node node1=getNode(comp1);
		Node node2=getNode(comp2);		
		Edge e=new Edge(node1,node2);
		e.setRole(Edges.Roles.EDGE_ROLE);
		graph.connect(e);
		fireEvent(new GraphModelEvent(GraphModelChange.EdgeAdded));
		return e;
	}

	public Edge connect(CanvasComponent comp1, CanvasComponent comp2, Edge template)
	{
		Node node1=getNode(comp1);
		Node node2=getNode(comp2);		
		Edge e=new Edge(node1,node2);
		e.setName(template.getName());
		e.setWeight(template.getWeight());
		e.setRole(template.getRole());
		e.setProperties(template.getProperties());
		graph.connect(e);
		fireEvent(new GraphModelEvent(GraphModelChange.EdgeAdded));
		return e;
	}



	public void removeEdge(Edge edge)
	{
		graph.removeEdge(edge);
		fireEvent(new GraphModelEvent(GraphModelChange.EdgeRemoved));
	}

	public void addGraphModelListener(GraphModelListener listener)
	{
		listeners.add(listener);
	}

	public void removeGraphModelListener(GraphModelListener listener)
	{
		listeners.remove(listener);
	}

	protected void fireEvent(GraphModelEvent event)
	{
		if(!silent)
		{
			for(int i=0; i!= listeners.size(); ++i)
			{
				listeners.get(i).graphModelChanged(event);
			}
		}
	}
	
	public boolean isSilent() {
		return silent;
	}
	
	public void setSilent(boolean silent) 
	{
		this.silent = silent;
		if(!this.silent)
		{
			// fire new event to update all listeners. 
			fireEvent(new GraphModelEvent(GraphModelChange.AllComponentsChanged));
		}
	}

	public abstract GraphModel buildSubModel(List<Node> selectedNodes);

}

