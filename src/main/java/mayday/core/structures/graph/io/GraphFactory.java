package mayday.core.structures.graph.io;

import java.util.Collection;
import java.util.Map;
import java.util.regex.Pattern;

import mayday.core.Probe;
import mayday.core.structures.graph.Edge;
import mayday.core.structures.graph.Graph;
import mayday.core.structures.graph.Node;
import mayday.core.structures.graph.nodes.DefaultNode;
import mayday.core.structures.graph.nodes.MultiProbeNode;
import mayday.vis3.graph.model.SummaryProbe;
import mayday.vis3.graph.model.SummaryProbe.SummaryMode;
import mayday.vis3.graph.model.SummaryProbe.WeightMode;

public class GraphFactory 
{
	protected Collection<Probe> probes;
	public static final Pattern probesSplitPattern=Pattern.compile("^\\\"|\\\"$|\\\",\\\"");
	public static final Pattern summaryPattern=Pattern.compile("^Summary\\d+\\:\\d\\:\\d\\:");

	public static final String ID_KEY="id";

	public GraphFactory(Collection<Probe> probes)
	{
		this.probes=probes;
	}



	public Graph produceGraph(String className)
	{
		if(className==null)
		{
			return new Graph();
		}
		if(className.equals(Graph.class.getName()))
		{
			return new Graph();
		}
		return new Graph();
	}

	public Node produceNode(String className, Graph graph)
	{
		if(className.equals(Node.class.getName()))
		{
			return new Node(graph);
		}		
		if(className.equals(MultiProbeNode.class.getName()))
		{
			return new MultiProbeNode(graph);
		}		
		return new MultiProbeNode(graph);
	}

	public Node produceNode(Map<String,String> attributes, Graph graph)
	{
		String className=attributes.get(GraphMLExport.CLASS_KEY);

		if(className==null)
		{
			MultiProbeNode n= new MultiProbeNode(graph);
			n.setProperties(attributes);	
			return n;
		}

		if(className.equals(MultiProbeNode.class.getName()))
		{
			MultiProbeNode node= new MultiProbeNode(graph);
			if(attributes.containsKey(GraphMLExport.PROBES_KEY))
			{
				String p=attributes.get(GraphMLExport.PROBES_KEY);
				String[] ps=probesSplitPattern.split(p);
				for(int i=1; i!= ps.length; ++i)// skip first empty element!;
				{
					if(ps[i].contains("$"))
					{
						String[] dspl=ps[i].split("\\$");
						if(summaryPattern.matcher(dspl[1]).matches())
						{
							String[] g=ps[1].split(":");
							SummaryMode m=SummaryMode.fromInt(Integer.parseInt(g[1]));
							WeightMode w=WeightMode.fromInt(Integer.parseInt(g[2]));
							SummaryProbe sp=new SummaryProbe(probes.iterator().next().getMasterTable(), graph, node,m,false);
							sp.setWeightMode(w);
							node.addProbe(sp);
						}else
						{
							for(Probe probe:probes)
							{
	
								if(probe.getName().equals(dspl[1]) && probe.getMasterTable().getDataSet().getName().equals(dspl[0]))
									node.addProbe(probe);
							}
						}
					}
				}
				if(node.getProbes().isEmpty())
				{
					node.setProperty(GraphMLExport.PROBES_KEY, p);
				}
			}
			if(attributes.containsKey(GraphMLExport.ROLE_KEY))
			{
				node.setRole(attributes.get(GraphMLExport.ROLE_KEY));
			}
			for(String key:attributes.keySet())
			{
				if(
						key.equals(GraphMLExport.CLASS_KEY) || 
						key.equals(GraphMLExport.PROBES_KEY) ||
						key.equals(GraphMLExport.NAME_KEY) ||
						//						key.equals(ID_KEY) ||						
						key.equals(GraphMLExport.ROLE_KEY) )
					continue;
				node.setProperty(key, attributes.get(key));
			}
			return node;
		}
		if(className.equals(DefaultNode.class.getName()))
		{
			DefaultNode node= new DefaultNode(graph,attributes);
			return node;
		}
		return produceNode(className, graph);
	}

	public Edge produceEdge(String className, Node source, Node target)
	{
		//		if(className==null)
		//		{
		//			return new Edge(source,target);
		//		}
		//		if(className.equals(Edge.class.getName()))
		//		{
		//			return new Edge(source,target);
		//		}	
		return new Edge(source,target);
	}
}
