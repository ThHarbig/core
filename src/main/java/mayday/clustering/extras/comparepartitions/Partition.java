package mayday.clustering.extras.comparepartitions;

import java.util.Collection;
import java.util.TreeMap;
import java.util.TreeSet;
import java.util.Map.Entry;

import mayday.core.Probe;
import mayday.core.ProbeList;
import mayday.core.meta.MIGroup;
import mayday.core.meta.MIType;
import mayday.core.meta.NominalMIO;

public class Partition {
	
	public static final String UNCLUSTERED = "**~ MISSING ~**";

	protected TreeMap<String, Collection<Probe>> partition_forward = new TreeMap<String, Collection<Probe>>();
	protected TreeMap<Probe, String> partition_backward = new TreeMap<Probe, String>();
	
	public Partition( MIGroup miGroup ) {
		partition_forward.put(UNCLUSTERED, new TreeSet<Probe>());
		populatePartition( miGroup );
	}
		
	public Partition( Collection<ProbeList> probeLists) {
		partition_forward.put(UNCLUSTERED, new TreeSet<Probe>());
		populatePartition(probeLists);
	}
		
	
	protected void addToPartition(String s, Probe p) {
		Collection<Probe> cp = partition_forward.get(s);
		if (cp == null) {
			cp = new TreeSet<Probe>();
			partition_forward.put(s, cp);
		}
		cp.add(p);
		partition_backward.put(p,s);
	}
	
	@SuppressWarnings("unchecked")
	protected void populatePartition( MIGroup migroup ) {
		for (Entry<Object,MIType> mio : migroup.getMIOs()) {
			Object k = mio.getKey();
			if (k instanceof Probe) {
				addToPartition( 
						((NominalMIO)mio.getValue()).getValue().toString(), 
						(Probe)k);
			}
		}
	}
	
	protected void populatePartition( Collection<ProbeList> probeLists ) {
		for (ProbeList pl : probeLists) {
			String key = pl.getName();
			for (Probe pb : pl.getAllProbes())
				addToPartition(key, pb);
		}
		int s = 0;
		for (Collection<Probe> p : partition_forward.values())
			s += p.size();
		if (s>partition_backward.size())
			throw new RuntimeException("This is not a partition! Some probes are contained in multiple probelists.");
	}
	
	protected void addUnclustered( Collection<Probe> allProbes ) {
		TreeSet<Probe> pbs = new TreeSet<Probe>(allProbes);
		pbs.removeAll(partition_backward.keySet());
		for (Probe p : pbs) 
			addToPartition(UNCLUSTERED, p);
	}
	
	public String getPartition(Probe pb) {
		return partition_backward.get(pb);
	}
	
	public Collection<Probe> getProbes() {
		return partition_backward.keySet();	
	}
	
	public Collection<String> getPartitionNames() {
		return partition_forward.keySet();
	}
	
	public int size() {
		return partition_forward.keySet().size();
	}
	
	public Collection<Probe> getPartitionPart(String part) { 
		return partition_forward.get(part);
	}
}
