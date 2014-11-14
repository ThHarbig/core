package mayday.vis3.plots.genomeviz.genomeheatmap.owndatastructures;

import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

import mayday.core.Probe;
import mayday.genetics.basic.Strand;

public class ForwardBackwardProbes {
 
	protected List<Probe> forwardProbes;
	protected List<Probe> backwardProbes;
	protected Set<Probe> added;
	
	public ForwardBackwardProbes(){
		forwardProbes = new LinkedList<Probe>();
		backwardProbes = new LinkedList<Probe>();
		added = null;
	}
	
	public List<Probe> getForwardProbes(){
		return forwardProbes;
	}
	
	public List<Probe> getBackwardProbes(){
		return backwardProbes;
	}

	public void add(Probe pb, Strand strand) {
		
		if (added==null) {
			added = new HashSet<Probe>();
			added.addAll(forwardProbes);
			added.addAll(backwardProbes);
		}
		
		if (added.contains(pb))
			return;
		
		if (strand.similar(Strand.PLUS))
			forwardProbes.add(pb);
		if (strand.similar(Strand.MINUS))
			backwardProbes.add(pb);
		
		added.add(pb);
	}
	
	public void finalize() {
		if (added!=null)
			added.clear();
		added = null;
	}
}
