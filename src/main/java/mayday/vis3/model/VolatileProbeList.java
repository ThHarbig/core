package mayday.vis3.model;

import java.awt.Color;
import java.util.Collection;
import java.util.TreeMap;

import mayday.core.DataSet;
import mayday.core.Probe;
import mayday.core.ProbeList;
import mayday.core.ProbeListEvent;

public class VolatileProbeList extends ProbeList {

	protected String originalName;
	
	public VolatileProbeList(DataSet dataSet) {
		super(dataSet, false);
	}
	
	public void addProbes(Collection<Probe> probes) {
		boolean changed=false;
		for(Probe pb : probes) {
			changed|=!this.probes.containsKey(pb.getName());
			this.probes.put(pb.getName(), pb);
		}
		if (changed) {
			invalidateCache();
			fireProbeListChanged( ProbeListEvent.CONTENT_CHANGE ); 			
		}
	}
	
	public void removeProbes(Collection<Probe> probes) {
		boolean changed=false;
		for(Probe pb : probes)
			changed|=(this.probes.remove(pb.getName())!=null);
		if (changed) {
			invalidateCache();
			fireProbeListChanged( ProbeListEvent.CONTENT_CHANGE );
		}
	}
	
	public void setProbes(Collection<Probe> probes) {
		if (this.probes==null)
			this.probes = new TreeMap<String, Probe>();
		else
			this.probes.clear();
		addProbes(probes);
	}
	
	 public void setColor( Color color )
	    {
	        if ( !getColor().equals( color ) )
	        {
	            super.setColor(color);
                fireProbeListChanged( ProbeListEvent.LAYOUT_CHANGE );
	        }
	    }

	public void setOriginalName(String name) {
		originalName = name;		
	}
	
	public String getOriginalName() {
		return originalName;
	}
	
}
