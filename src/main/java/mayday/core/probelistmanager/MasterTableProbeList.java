package mayday.core.probelistmanager;
import java.awt.Color;
import java.util.Map;
import java.util.concurrent.ConcurrentSkipListMap;

import mayday.core.DataSet;
import mayday.core.MasterTableEvent;
import mayday.core.MasterTableListener;
import mayday.core.Probe;
import mayday.core.ProbeListEvent;

/**
 * @author neil
 * @version 
 */
public class MasterTableProbeList extends UnionProbeList implements MasterTableListener
{

	public MasterTableProbeList( DataSet dataSet ) {
		super(dataSet, null);
		super.setName("Complete DataSet");
		dataSet.getMasterTable().addMasterTableListener(this);
	}

	public Map<String,Probe> toMap()  {
		if (!isMapCacheValid()) {
			probes = new ConcurrentSkipListMap<String, Probe>();
			probes.putAll(getDataSet().getMasterTable().getProbes());
		}
		return probes;
	}

	public void masterTableChanged(MasterTableEvent event) {
		invalidateCache();
	}

	public void setName(String Name) {
		// not changeable
		System.err.println("MasterTable ProbeList name cannot be changed");
		fireProbeListChanged(ProbeListEvent.LAYOUT_CHANGE);
	}

	public void setColor( Color color ) {
		// not changeable
		System.err.println("MasterTable ProbeList color cannot be changed");
		if ( isSticky() )
			fireProbeListChanged( ProbeListEvent.LAYOUT_CHANGE );
	}


}
