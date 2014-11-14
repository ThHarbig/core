package mayday.vis3.model.wrapped;

import java.awt.Color;
import java.lang.ref.WeakReference;
import java.util.Map;
import java.util.concurrent.ConcurrentSkipListMap;

import mayday.core.Probe;
import mayday.core.ProbeList;
import mayday.core.ProbeListEvent;
import mayday.core.ProbeListListener;
import mayday.core.meta.types.AnnotationMIO;
import mayday.core.probelistmanager.UnionProbeList;

/** WrappedProbeList wraps around an existing ProbeList and 
 * 1) replaces the MasterTable with an instance of WrappedMasterTable (provided in the constructor)
 * 2) wraps all Probes in WrappedProbes upon the first access
 * 3) Equality checks using hashCode() and equals() make no distinction between
 *    the WrappedProbeList and the wrapped ProbeList contained inside. 
 * Everything else is delegated to the wrapped ProbeList
 * @author battke
 *
 */
public class WrappedProbeList extends ProbeList {

	ProbeList wrapped;
	WrappedMasterTable smt;

	public WrappedProbeList(ProbeList parent, WrappedMasterTable smt) {
		super(smt.getDataSet(), false);
		this.smt = smt;
		wrapped = parent;		
		parent.addProbeListListener(new WeakProbeListListener(this, parent));
	}

	public Map<String,Probe> toMap()  { 
		synchronized (this) {
			if (probes==null) {
				probes = new ConcurrentSkipListMap<String, Probe>();
				for (Probe pb : wrapped.toCollection()) 
					probes.put(pb.getName(), new WrappedProbe(pb, smt));					
			}
			return probes;			
		}
	}
	
	public ProbeList getWrappedProbeList() {
		return wrapped;
	}

	public void probeListChanged(ProbeListEvent event) {
		probes = null; // force update
	}
	
	private static class WeakProbeListListener implements ProbeListListener {

		WeakReference<WrappedProbeList> wspl;
		ProbeList parent;
		
		public WeakProbeListListener(WrappedProbeList subsetProbeList, ProbeList parent) {
			wspl = new WeakReference<WrappedProbeList>(subsetProbeList);
			this.parent=parent;
		}

		@Override
		public void probeListChanged(ProbeListEvent event) {
			WrappedProbeList spl = wspl.get();
			if (spl==null) 
				parent.removeProbeListListener(this);
			else 
				spl.probeListChanged(event);				
		}
		
	}
	
	// === WRAPPED 

	public int hashCode() {
		return wrapped.hashCode();
	}

	public AnnotationMIO getAnnotation() {
		return wrapped.getAnnotation();
	}

	public void propagateClosing() {
		wrapped.propagateClosing();		    	
	}
	public String getName() {
		return wrapped.getName();
	}
	public void setName(String Name) {
		wrapped.setName(Name);
	}
	public void setAnnotation( AnnotationMIO annotation ) {
		wrapped.setAnnotation(annotation);
	}

	public void addProbeListListener( ProbeListListener listener ) {
		wrapped.addProbeListListener(listener);
	}
	public void removeProbeListListener( ProbeListListener listener ) {
		wrapped.removeProbeListListener(listener);
	}
	public void fireProbeListChanged( int change ) {
		wrapped.fireProbeListChanged(change);
	}
	public Color getColor() {
		return wrapped.getColor();
	}
	public void setColor( Color color ) {
		wrapped.setColor(color);
	}
	public void addProbe( Probe probe ) throws RuntimeException {
		wrapped.addProbe(probe);
	}
	public void setProbes( Probe[] probes ) throws RuntimeException {
		wrapped.setProbes(probes);
	}
	public void removeProbe( Probe probe ) {
		wrapped.removeProbe(probe);
	}

	public void setProbe( String name, Probe probe ) {
		wrapped.setProbe(name, probe);
	}

	public boolean equals( Object probeList ) {
		return wrapped.equals(probeList);
	}

	public Object clone() {
		return new WrappedProbeList(parent, smt);
	}

	public Object cloneProperly() {
		return clone();
	}
	
	public boolean isSticky() {
		return wrapped.isSticky();
	}
	public void setSticky( boolean isSticky ) {
		wrapped.setSticky(isSticky);
	}

	public boolean isSilent() {
		return wrapped.isSilent();
	}
	public void setSilent( boolean isSilent ) {
		wrapped.setSilent(isSilent);
	}


	public UnionProbeList getParent() {
		return wrapped.getParent();
	}
	public void setParent(UnionProbeList plgparent) {
		wrapped.setParent(plgparent);
	}
	public void invalidateCache() {
		probes = null;
	}

}
