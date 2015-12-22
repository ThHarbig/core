package mayday.core.structures.graph.nodes;

import java.util.ArrayList;
import java.util.List;

import javax.xml.stream.XMLStreamWriter;

import mayday.core.Probe;
import mayday.core.ProbeList;
import mayday.core.structures.graph.Graph;
import mayday.core.structures.graph.io.GraphMLExport;

/**
 * Node that stores a List of Probes.
 * @author Stephan Symons
 * @version 1.1
 */
public class MultiProbeNode extends DefaultNode
{
	/** The probes this node represents. */
	protected List<Probe> probes;
	
	/**
	 * Create a new MultiProbeNode belonging to a Graph, 
	 * and containing the probe p.
	 * @param graph The parent graph
	 * @param p The probe to be used
	 */
	public MultiProbeNode(Graph graph, Probe p) 
	{
		super(graph);
		this.probes=new ArrayList<Probe>();
		probes.add(p);
		setName(p.getDisplayName());
		role=Nodes.Roles.PROBE_ROLE;
	}
	
	/**
	 * Create a new MultiProbeNode belonging to a Graph, 
	 * and containing all the genes of ProbeList pl.
	 * @param graph The parent graph
	 * @param pl The probelist with the probes to be used
	 */
	public MultiProbeNode(Graph graph, ProbeList pl) 
	{
		super(graph);
		this.probes=new ArrayList<Probe>(pl.getAllProbes());
		setName(pl.getName());
		role=Nodes.Roles.PROBES_ROLE;		
	}
	
	/**
	 * Create a new MultiProbeNode belonging to a Graph, 
	 * which keeps all Probes of the List of probes. 
	 * @param graph
	 * @param probes
	 */
	public MultiProbeNode(Graph graph) 
	{
		super(graph);
		this.probes=new ArrayList<Probe>();
		role=Nodes.Roles.NODE_ROLE;
		setName("");
	}
	
	/**
	 * Create a new MultiProbeNode belonging to a Graph, 
	 * which keeps all Probes of the List of probes. 
	 * @param graph
	 * @param probes
	 */
	public MultiProbeNode(Graph graph, List<Probe> probes) 
	{
		super(graph);
		this.probes=probes;
		role=Nodes.Roles.PROBES_ROLE;
		setName("");
	}

	/**
	 * Get all probes from this node. 
	 * @return the list of probes
	 */
	public List<Probe> getProbes() {
		return probes;
	}
	
	public ProbeList getProbeList(){
		ProbeList res=new ProbeList(probes.get(0).getMasterTable().getDataSet(),false);
		for(Probe p:probes)
		{
			if(!res.contains(p))
				res.addProbe(p);
		}
		return res;
	}

	/**	 * 
	 * @param probes The probes to set.
	 */
	public void setProbes(List<Probe> probes) 
	{
		this.probes = probes;
	}
	
	/**
	 * Add a single probe to the node
	 * @param p The probe to be added
	 */
	public void addProbe(Probe p)
	{
		probes.add(p);
	}

	@Override
	public void exportNodeHead(XMLStreamWriter writer) throws Exception 
	{
		super.exportNodeHead(writer);
		StringBuffer probeNames=new StringBuffer();
		boolean first=true;
		for(Probe p:probes)
		{
			if(!first)
				probeNames.append(",");
			first=false;
			probeNames.append("\""+p.getMasterTable().getDataSet().getName()+"$"+p.getName()+"\"");
		}
		GraphMLExport.writeDataElement(writer, GraphMLExport.PROBES_KEY, probeNames.toString());
		
	}

}
