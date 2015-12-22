package mayday.vis3.plots.genomeviz.genomeheatmap.owndatastructures;

import java.util.Collections;
import java.util.List;

import mayday.core.Probe;
import mayday.vis3.plots.genomeviz.EnumManagerGHM.StrandInformation;

public class CellObject {
 
	protected List<Probe> probes = Collections.emptyList();
	protected StrandInformation strand = null;
	protected int cellnumber = -1;
	
	public CellObject(List<Probe> probes , StrandInformation strand, int cellnumber){
		if(probes.isEmpty()) this.probes = Collections.emptyList();
		else this.probes = probes;
		
		this.strand = strand;
		this.cellnumber = cellnumber;
	}
	
	public int getCellnumber(){ 

		return this.cellnumber;
	}
	
	public StrandInformation getStrand(){
		return this.strand;
	}
	
	public List<Probe> getProbes(){
		if(!probes.isEmpty()){
			return this.probes;
		} else {
			return Collections.emptyList();
		}
	}
}
