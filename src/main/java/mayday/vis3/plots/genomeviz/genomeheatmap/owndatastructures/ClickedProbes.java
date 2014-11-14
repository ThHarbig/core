package mayday.vis3.plots.genomeviz.genomeheatmap.owndatastructures;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

import mayday.core.Probe;
import mayday.vis3.plots.genomeviz.EnumManagerGHM.StrandInformation;

public class ClickedProbes implements Comparable<Object>{
 

	protected int cellnumber;
	protected StrandInformation strand;
	protected List<Probe> containedProbes = Collections.emptyList();
	
	
	public ClickedProbes(int cellnumber, StrandInformation strand, List<Probe> containedProbes){
		this.cellnumber = cellnumber;
		this.strand = strand;
		this.containedProbes = containedProbes;
	}
	
	public int getCellnumber(){
		return this.cellnumber;
	}
	
	public StrandInformation getStrand(){
		return this.strand;
	}
	
	public void setProbes(LinkedList<Probe> probes){
		this.containedProbes = probes;
	}
	
	public List<Probe> getProbes(){
		if(!containedProbes.isEmpty()){
			return this.containedProbes;
		} else {
			return Collections.emptyList();
		}
	}

	public int compareTo(Object obj) {
		if(this.strand.equals(((ClickedProbes)obj).strand)){
			
			if(this.cellnumber == ((ClickedProbes)obj).cellnumber){
				return 0;
			} else {
				if (this.containedProbes.size() == ((LinkedList<Probe>)((ClickedProbes)obj).containedProbes).size() &&
						this.containedProbes.containsAll((LinkedList<Probe>)((ClickedProbes)obj).containedProbes)){
					return 0;
				}
			}	
		}
		return -1;
	}
}
