package mayday.vis3.model.wrapped;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import javax.swing.ListModel;

import mayday.core.DataSet;
import mayday.core.ProbeList;
import mayday.core.ProbeListEvent;
import mayday.core.StoreListener;
import mayday.core.probelistmanager.ProbeListManager;
import mayday.core.probelistmanager.ProbeListManagerListener;
import mayday.core.probelistmanager.gui.ProbeListManagerView;


/** WrappedProbeListManager wraps around an existing ProbeListManager and 
 * 1) replaces the MasterTable with an instance of WrappedMasterTable (provided in the constructor)
 * 2) wraps all ProbeLists in WrappedProbeLists UPON ACCESS (which might be inefficient if called often)
 * 3) returns the WrappedDataSet associated with the WrappedMasterTable instead of the original DataSet  
 * Everything else is delegated to the wrapped ProbeListManager
 * @author battke
 *
 */
public class WrappedProbeListManager implements ProbeListManager
{
	
	ProbeListManager wrapped;
	WrappedMasterTable smt;
	
	public WrappedProbeListManager(ProbeListManager wrapped, WrappedMasterTable smt) {
		this.wrapped=wrapped;
		this.smt = smt;
	}
	
	public DataSet getDataSet() {
		return smt.getDataSet();
	}
	public void setDataSet(DataSet ds) {
		smt.setDataSet(ds);
	}
	
	@SuppressWarnings("unchecked")
	public List getObjects() {
		return (List)getProbeLists();
	}
	
	public List<ProbeList> getProbeLists() {
		List<ProbeList> vl = wrapped.getProbeLists();
		ArrayList<ProbeList> ret = new ArrayList<ProbeList>(vl.size());
		for (ProbeList pl : vl)
			ret.add(new WrappedProbeList(pl, smt));
		return ret;
		
	}
	
	public ProbeList getProbeList(String name) {
		ProbeList pl = wrapped.getProbeList(name);
		if (pl==null)
			return pl;
		return new WrappedProbeList(pl,smt);	
	}
	
	public List<ProbeList> getProbeListsBelow(ProbeList probelistparent) {
		List<ProbeList> vl = wrapped.getProbeListsBelow(probelistparent);
		ArrayList<ProbeList> ret = new ArrayList<ProbeList>(vl.size());
		for (ProbeList pl : vl)
			ret.add(new WrappedProbeList(pl, smt));
		return ret;
	}

	// === WRAPPED FUNCTIONS ===
	
	public void setProbeListManagerView(ProbeListManagerView plmv) {
		wrapped.setProbeListManagerView(plmv);
	}
	public ProbeListManagerView getProbeListManagerView() {
		return wrapped.getProbeListManagerView();
	}
	
	@SuppressWarnings("unchecked")
	public void setObjects( java.util.List objects ) {
		wrapped.setObjects(objects);
	}

	public int getNumberOfObjects() {
		return wrapped.getNumberOfObjects();
	}
	
	public boolean contains(String name) {
		return wrapped.contains(name);
	}
	
	public boolean contains(Object obj) {
		return wrapped.contains(obj);
	}
	
	public void addProbeListManagerListener( ProbeListManagerListener listener ) {
		wrapped.addProbeListManagerListener(listener);
	}
	
	public void removeProbeListManagerListener( ProbeListManagerListener listener ) {
		wrapped.removeProbeListManagerListener(listener);
	}

	public boolean moveUpProbeList( int index ) {
		return wrapped.moveUpProbeList(index);
	}
	public boolean moveUpProbeList( ProbeList probeList ) {
		return wrapped.moveUpProbeList(probeList);
	}
	public boolean moveDownProbeList( int index ) {
		return wrapped.moveDownProbeList(index);
	}
	public boolean moveDownProbeList( ProbeList probeList ) {
		return wrapped.moveDownProbeList(probeList);
	}

	public void addObject( Object object ) {
		wrapped.addObject(object);
	}
	public void addObjectAtBottom( Object object ) {
		wrapped.addObjectAtBottom(object);
	}
	public void addObjectAtTop( Object object ) {
		wrapped.addObjectAtTop(object);
	}

	public void removeObject( Object object ){
		wrapped.removeObject(object);
	}
	
	public void orderChangedExternally() {
		wrapped.orderChangedExternally();
	}

	public void replaceObject(Object object, Object replacement) {
		wrapped.replaceObject(object, replacement);
	}

	public void clear() {
		wrapped.clear();
	}
	
	public boolean isSilent() {
		return wrapped.isSilent();
	}
	public void setSilent( boolean isSilent ) {
		wrapped.setSilent(isSilent);
	}

	public void addStoreListener(StoreListener list) {
		wrapped.addStoreListener(list);
	}
	public void removeStoreListener(StoreListener list) {
		wrapped.removeStoreListener(list);
	}
	
	public ProbeList getSharedAncestor(Collection<ProbeList> probeLists) {
		return wrapped.getSharedAncestor(probeLists);
	}
	
	public ListModel getModel() {
		return wrapped.getModel(); // TODO dangerous, contains untransformed probelists
	}

	@Override
	public void probeListChanged(ProbeListEvent event) {
		wrapped.probeListChanged(event);
	}
}