package mayday.core.structures.graph.nodes;

import java.util.List;

import mayday.core.Probe;
import mayday.core.ProbeList;
import mayday.core.meta.MIGroup;
import mayday.core.structures.graph.Graph;


/**
 * Node that stores a comparable MIO Object
 * @author Stephan Symons
 *
 */
public class MIONode extends MultiProbeNode
{
	private MIGroup miGroup;
	
	public MIONode(Graph graph, List<Probe> probes) 
	{
		super(graph, probes);
		setRole(Nodes.Roles.MIO_ROLE);		
	}

	public MIONode(Graph graph, Probe p) 
	{
		super(graph, p);
		setRole(Nodes.Roles.MIO_ROLE);
	}

	public MIONode(Graph graph, ProbeList pl) 
	{
		super(graph, pl);
		setRole(Nodes.Roles.MIO_ROLE);
	}

	public MIONode(Graph graph) 
	{
		super(graph);
		setRole(Nodes.Roles.MIO_ROLE);		
	}

	public MIGroup getMiGroup() 
	{
		return miGroup;
	}
	
	public void setMiGroup(MIGroup miGroup) 
	{
		this.miGroup = miGroup;
		setName(miGroup.getName());
	}
	


}
