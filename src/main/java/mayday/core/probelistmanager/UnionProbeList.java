package mayday.core.probelistmanager;

import java.util.Enumeration;
import java.util.LinkedList;
import java.util.Map;
import java.util.concurrent.ConcurrentSkipListMap;

import mayday.core.DataSet;
import mayday.core.Probe;
import mayday.core.ProbeList;
import mayday.core.ProbeListEvent;
import mayday.core.ProbeListListener;
import mayday.core.probelistmanager.gui.ProbeListNode;

/**
 * @author neil
 * @version 
 */
public class UnionProbeList extends ProbeList implements ProbeListListener
{

	protected ProbeListNode node;
	
	protected LinkedList<ProbeList> sublists;
	
	protected boolean listening = true;

	public UnionProbeList( DataSet dataSet, ProbeListNode node ) {
		super(dataSet, false);
		this.node=node;
		sublists = new LinkedList<ProbeList>();
	}
	
	public void setNode(ProbeListNode node) {
		this.node = node;
		childrenChanged();
	}

	public ProbeListNode getNode() {
		return this.node;
	}
	
	@SuppressWarnings("unchecked")
	public Map<String,Probe> toMap()  {
		synchronized (this) {
			if (!isMapCacheValid()) {
				
				for (ProbeList pl: sublists)
					pl.removeProbeListListener(this);
				sublists.clear();
				
				probes = new ConcurrentSkipListMap<String, Probe>();
				// fill the cache with all our childrens' contents
				if (node!=null) {
					Enumeration<ProbeListNode> children = node.children();
					while (children.hasMoreElements()) {
						ProbeListNode nextChild = children.nextElement();
						ProbeList pl = nextChild.getProbeList();
						if (pl!=null) {
							probes.putAll(pl.toMap());
							sublists.add(pl);
							pl.addProbeListListener(this);
						}
					}
				}			
					
			}
			return probes;
		}
	}

	public void probeListChanged(ProbeListEvent event) {
		if (event.getChange()==ProbeListEvent.CONTENT_CHANGE) 
			childrenChanged();
	}
	
	public void childrenChanged() {
		if (listening) {
			invalidateCache();		
			fireProbeListChanged(ProbeListEvent.CONTENT_CHANGE);
		}
	}
	
	public void beginLargeUpdate() {
		listening = false;
	}
	
	public void endLargeUpdate() {
		listening = true;
		childrenChanged();
	}
	
    protected boolean isMapCacheValid() {
    	synchronized (this) {
        	return (probes!=null);			
		}
    }

    public void invalidateCache() {
    	synchronized (this) {
        	super.invalidateCache();
           	probes= null;   				
		}
    }

	// forbidden methods ====================================================
	
	public void addProbe( Probe probe ) throws RuntimeException {
		throw new RuntimeException("Unable to change contents of a probe list node");
	}


	public void setProbes( Probe[] probes ) throws RuntimeException {
		throw new RuntimeException("Unable to change contents of a probe list node");
	}


	public void removeProbe( Probe probe ) throws RuntimeException {
		throw new RuntimeException("Unable to change contents of a probe list node");
	}


	public void setOperation( MasterTableProbeList probeListA, MasterTableProbeList probeListB, int mode ) {
		throw new RuntimeException("Unable to change contents of a probe list node");
	}
	
	public void setProbe( String name, Probe probe ) throws RuntimeException  {
		throw new RuntimeException("Unable to change contents of a probe list node");
	}

	public void clearProbes() {
		throw new RuntimeException("Unable to change contents of a probe list node");
	}

	public void setSticky( boolean isSticky ) {
		throw new RuntimeException("Unable to change stickyness of a probe list node");
	}

}
