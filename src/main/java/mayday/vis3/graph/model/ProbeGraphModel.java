package mayday.vis3.graph.model;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import mayday.core.Probe;
import mayday.core.ProbeList;
import mayday.core.structures.graph.Graph;
import mayday.core.structures.graph.Graphs;
import mayday.core.structures.graph.Node;
import mayday.core.structures.graph.nodes.MultiProbeNode;
import mayday.core.structures.maps.MultiHashMap;
import mayday.vis3.graph.components.CanvasComponent;
import mayday.vis3.graph.components.MultiProbeComponent;
import mayday.vis3.graph.model.GraphModelEvent.GraphModelChange;

public class ProbeGraphModel extends GraphModel implements GraphWithProbeModel
{
//	protected Map<Probe,NodeComponent> probeToComponent=new HashMap<Probe, NodeComponent>();
	protected MultiHashMap<Probe,MultiProbeComponent> probeToComponent=new MultiHashMap<Probe, MultiProbeComponent>();
	
	public ProbeGraphModel()
	{
		setGraph(new Graph());
	}
	
	public ProbeGraphModel(Set<Probe> probes)
	{
		Graph graph=new Graph();
		for(Probe p:probes)
		{
			MultiProbeNode n=new MultiProbeNode(graph,p);
			graph.addNode(n);
		}
		setGraph(graph);
		init();
	}
	
	public ProbeGraphModel(Graph g)
	{
		setGraph(g);
		
		init();
	}
	
	public void clearAll()
	{
		probeToComponent.clear();
		super.clearAll();
	}

	@Override
	protected void init() 
	{
		clear();
		for(Node n:getGraph().getNodes())
		{
			MultiProbeComponent comp=new MultiProbeComponent((MultiProbeNode)n);
			addComponent(comp);	
			getNodeMap().put(comp, n);
			getComponentMap().put(n, comp);	
			if(n instanceof MultiProbeNode)
			{
				for(Probe p: ((MultiProbeNode)n).getProbes())
					probeToComponent.put(p, comp);
			}
		}
		Collections.sort(getComponents());	
		fireEvent(new GraphModelEvent(GraphModelChange.AllComponentsChanged, new HashSet<CanvasComponent>()));
	}
	
//	@Deprecated
//	public MultiProbeComponent getComponent(Probe p)
//	{
//		if(probeToComponent.containsKey(p))
//			return (MultiProbeComponent)probeToComponent.get(p).get(0);
//		else
//			return null;
//	}
	
	public List<MultiProbeComponent> getComponents(Probe probe) 
	{
//		List<CanvasComponent> res=new ArrayList<CanvasComponent>();
//		res.add(getComponent(probe));
//		return res;
		
		return probeToComponent.get(probe);
	}
	
	@Override
	public ProbeGraphModel buildSubModel(List<Node> selectedNodes) 
	{
		Graph g=Graphs.restrict(getGraph(), selectedNodes);
		return new ProbeGraphModel(g);
	}
	
	public Set<Probe> getProbes()
	{
		return probeToComponent.keySet();
	}
	
	public MultiProbeComponent addProbeListNode(ProbeList pl)
	{
		MultiProbeNode n=new MultiProbeNode(getGraph(),pl);
		getGraph().addNode(n);

		MultiProbeComponent comp=new MultiProbeComponent(n);

		addComponent(comp);	
		getNodeMap().put(comp, n);
		getComponentMap().put(n, comp);	

		for(Probe p:pl)
		{
			probeToComponent.put(p, comp);
		}		
		return comp;
	}
	
	public MultiProbeComponent addProbe(Probe p)
	{
		MultiProbeNode n=new MultiProbeNode(getGraph(),p);

		getGraph().addNode(n);

		MultiProbeComponent comp=new MultiProbeComponent(n);

		addComponent(comp);	
		getNodeMap().put(comp, n);
		getComponentMap().put(n, comp);			
		probeToComponent.put(p, comp);
		return comp;
	}
	
	public List<CanvasComponent> addProbes(Iterable<Probe> probes)
	{
		List<CanvasComponent> res=new ArrayList<CanvasComponent>();
		for(Probe p:probes)
		{
			MultiProbeNode n=new MultiProbeNode(getGraph(),p);	
			getGraph().addNode(n);	
			MultiProbeComponent comp=new MultiProbeComponent(n);
	
			getNodeMap().put(comp, n);
			getComponentMap().put(n, comp);			
			probeToComponent.put(p, comp);	
			res.add(comp);
		}
		addComponent(res);
		return res;
	}

}
