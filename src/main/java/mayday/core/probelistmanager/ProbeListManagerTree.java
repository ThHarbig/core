package mayday.core.probelistmanager;

import java.awt.Component;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.TreeSet;

import javax.swing.JOptionPane;
import javax.swing.ListModel;
import javax.swing.SwingUtilities;
import javax.swing.event.EventListenerList;
import javax.swing.tree.DefaultTreeModel;

import mayday.core.DataSet;
import mayday.core.DelayedUpdateTask;
import mayday.core.EventFirer;
import mayday.core.Probe;
import mayday.core.ProbeList;
import mayday.core.ProbeListEvent;
import mayday.core.ProbeListListener;
import mayday.core.ProbeListStore;
import mayday.core.Store;
import mayday.core.StoreEvent;
import mayday.core.StoreListener;
import mayday.core.meta.MIGroup;
import mayday.core.meta.MIGroupSelection;
import mayday.core.meta.MIType;
import mayday.core.probelistmanager.gui.ProbeListManagerView;
import mayday.core.probelistmanager.gui.ProbeListManagerViewTree;
import mayday.core.probelistmanager.gui.ProbeListNode;
import mayday.core.probelistmanager.models.ProbeListTreeListModel;
import mayday.core.probelistmanager.plug.PLMVPlugin;


public class ProbeListManagerTree
implements ProbeListListener, ProbeListManager
{
	protected EventFirer<ProbeListManagerEvent, ProbeListManagerListener> eventfirer
	= new EventFirer<ProbeListManagerEvent, ProbeListManagerListener>() {
		protected void dispatchEvent(ProbeListManagerEvent event, ProbeListManagerListener listener) {
			listener.probeListManagerChanged(event);
		}		
	};
	
	protected EventListenerList storeListenerList = new EventListenerList();

	private ProbeListManagerView probeListManagerView; // fb 080319 added to get at the seleected probelists from outside

	protected ProbeListTreeListModel treeModel;
	
	protected DataSet dataSet;

	protected HashMap<PLMVPlugin, ProbeListManagerView> OPEN_VIEWS = new HashMap<PLMVPlugin, ProbeListManagerView>();

	private boolean isSilent; // indicates whether listeners are notified or not

	public Store fakeStore = new ProbeListStore();

	
	
	public ProbeListManagerTree( DataSet dataSet ){
		this.dataSet = dataSet;
		treeModel = new ProbeListTreeListModel(dataSet);
	}

	public void setProbeListManagerView(ProbeListManagerView plmv) {
		probeListManagerView = plmv;
	}
	
	public void addOpenProbeListManagerView(ProbeListManagerView plmv, PLMVPlugin provider) {
		OPEN_VIEWS.put(provider, plmv);
	}
	
	public ProbeListManagerView getOpenProbeListManagerView(PLMVPlugin provider) {
		return OPEN_VIEWS.get(provider);
	}
	
	public ProbeListManagerView getProbeListManagerView() {
		return probeListManagerView;
	}

	public void addProbeListManagerListener( ProbeListManagerListener listener ) {
		eventfirer.addListener( listener );
	}

	public void removeProbeListManagerListener( ProbeListManagerListener listener )	{
		eventfirer.removeListener( listener );
	}

	protected void fireProbeListManagerChanged( int change ) {
		if ( isSilent() )
			return;
		eventfirer.fireEvent(new ProbeListManagerEvent( this, change ));
	}

	
	protected boolean moveItem( Object item, int direction ) {
		boolean canMove = true; 
		ProbeListNode inode = treeModel.nodeOf(item, treeModel.getRoot());
		ProbeListNode pnode = (ProbeListNode)inode.getParent();
		int currentIndex = pnode.getIndex(inode);
		treeModel.moveNode(inode, pnode, currentIndex+direction);		
		fireProbeListManagerChanged( ProbeListManagerEvent.ORDER_CHANGE );
		return canMove;
	}

	public boolean moveUpProbeList( int index )	{
		return moveItem( treeModel.getElementAt(index), -1);
	}


	public boolean moveUpProbeList( ProbeList probeList ) {
		if (probeList==null || probeList instanceof MasterTableProbeList)
			return false;
		return moveItem( probeList , -1);
	}


	public boolean moveDownProbeList( int index ) {
		return moveItem( treeModel.getElementAt(index) , +1);
	}

	
	public boolean moveDownProbeList( ProbeList probeList )	{
		if (probeList==null || probeList instanceof MasterTableProbeList)
			return false;
		return moveItem( probeList , +1);
	}


	@SuppressWarnings("unchecked")
	public void setObjects( java.util.List objects ) {
		for ( ProbeList pl : (List<ProbeList>)objects ) {
			pl.addProbeListListener( this );
			addObjectInHierarchy(pl, false);
		}
		fireProbeListManagerChanged( ProbeListManagerEvent.CONTENT_CHANGE );		    
	}
	
	public boolean contains( String name, Object unequalTo ) {
		for ( Object o : treeModel.toArray() ) {
			String xname = "";
			if (o instanceof ProbeList)
				xname = ((ProbeList)o).getName();
			if ( xname.equals( name ) ) {
				if (unequalTo==null || unequalTo!=o)
					return true;
			}
		}  
		return false;
	}
	
	protected void verifyNameUniqueness( ProbeList newPL ) {  
		
		ProbeListNamer.ensureNameUniqueness(this, newPL);
		
		/*DateFormat df = DateFormat.getTimeInstance();
		
        while ( newPL.getName().equals("") || contains( newPL.getName().trim() , newPL ) ) {
        	
        	// suggest a new name based on the current time - this will change every time the while loop executes
        	// so collisions should not last long
        	String suggestion = newPL.getName() + " ("+df.format(new Date())+")";
        	while (contains(suggestion)) {
        		suggestion+="'";
        	}
        	
            String message = MaydayDefaults.Messages.PROBE_LIST_NOT_UNIQUE;
            message = message.replaceAll( MaydayDefaults.Messages.REPLACEMENT, newPL.getName() );
            message += "\n" + MaydayDefaults.Messages.ENTER_NEW_NAME;
            
            String name = (String)JOptionPane.showInputDialog( null,
                message,
                MaydayDefaults.Messages.WARNING_TITLE,
                JOptionPane.WARNING_MESSAGE,
                null,
                null,
                suggestion );
            
            name=(name==null?newPL.getName():name);
            
            newPL.setName( name );                                                   
        }*/
	}

	
	public void replaceObject(ProbeList object, ProbeList replacement) {
		object.removeProbeListListener(this);

		if (!(object.getName().equals(replacement.getName()))) 
			verifyNameUniqueness(object);
		
		replacement.addProbeListListener(this);
		treeModel.replaceProbeList(object, replacement);
		fireProbeListManagerChanged( ProbeListManagerEvent.CONTENT_CHANGE );
		fireObjectRemoved(object);
		fireObjectAdded(replacement);
	}

	protected void addObjectInHierarchy( ProbeList object, boolean first ) {
		ProbeListNode parent;
		if (object.getParent()!=null) {
			parent = treeModel.nodeOf(object.getParent(), treeModel.getRoot()); 
		} else {
			parent = (ProbeListNode)treeModel.getRoot();
		}
		treeModel.insertProbeList(object, parent.getProbeList(), first?0:parent.getChildCount());

		if (probeListManagerView!=null && probeListManagerView instanceof ProbeListManagerViewTree)
			((ProbeListManagerViewTree)probeListManagerView).getTree().expandPathTo(parent);

		fireObjectAdded(object);
	}
	
	protected void addObject( ProbeList object, boolean first ) {
		if (contains(object)) {
			System.err.println("ProbeListManager: Cannot add probelist, because it is already contained: "+object.toString());
			return;
		}
		verifyNameUniqueness((ProbeList)object);
		addObjectInHierarchy( object, first );
		((ProbeList)object).addProbeListListener( this );
		fireProbeListManagerChanged( ProbeListManagerEvent.CONTENT_CHANGE );		
	}
	
	public void addObject( ProbeList object ){
		addObjectAtTop( object );
	}

	public void addObjectAtBottom( ProbeList object ) {
		addObject( object, false );
	}

	public void addObjectAtTop( ProbeList object )	{
		addObject( object, true );
	}


	public List<ProbeList> getProbeLists() {
		Object[] ta = treeModel.toArray();
		LinkedList<ProbeList> ret = new LinkedList<ProbeList>();
		for (Object o : ta)
			ret.add((ProbeList)o);
		return ret;
	}

	protected DelayedUpdateTask probeRemoved = new DelayedUpdateTask("MasterTable cleanup") {

		protected boolean needsUpdating() {
			return true;
		}

		
		protected Runnable latestInvocation;
		
		protected void performUpdate() {
			
			final Collection<Probe> everything = new TreeSet<Probe>(dataSet.getMasterTable().getProbes().values());
			
			for (ProbeList pl : getProbeLists()) {
				if (pl instanceof MasterTableProbeList)
					continue;
				Collection<Probe> accounted_for = pl.getAllProbes();
				everything.removeAll(accounted_for);
			}
			
			if (everything.size()>0 && latestInvocation==null) {
				
				latestInvocation = new Runnable() {
					
					public void run() {						
						String message = "The MasterTable contains "+everything.size()+" probes that are no longer contained in any ProbeList.\n" +
								"Would you like to remove the meta-info for these probes?";
						if (JOptionPane.showConfirmDialog((Component)null, message, dataSet.getName(), 
								JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE)==JOptionPane.YES_OPTION) {
						
							for (Probe pb : everything) {
								MIGroupSelection<MIType> mgs = dataSet.getMIManager().getGroupsForObject(pb);
								for (MIGroup mg : mgs)
									mg.remove(pb);
								dataSet.getMasterTable().removeProbe(pb,true);
							}
							System.out.println("Removing "+everything.size()+" probes after ProbeList closing.");
							getProbeListManagerView().getComponent().repaint();
						}
						latestInvocation=null;
					}
				};
				
				SwingUtilities.invokeLater(latestInvocation);
			}
				
		}
		
	};
	
	protected void remove_internal(ProbeList object) {
		object.removeProbeListListener( this );
		if (!(object instanceof UnionProbeList)) {
			object.fireProbeListChanged( ProbeListEvent.PROBELIST_CLOSED );
			object.propagateClosing();
		}
		fireObjectRemoved(object);
	}
	
	public void removeObject( ProbeList object ) {
		if (object instanceof MasterTableProbeList)
			return;
		
		if (!contains(object))
			return;
		
		// one deletion can trigger a lot of removals
		for (ProbeList victim : treeModel.removeProbeList(object)) {
			remove_internal(victim);
		}
	
		fireProbeListManagerChanged( ProbeListManagerEvent.CONTENT_CHANGE );

		// now, if a probe list is closed, all it's probes that are not contained in any other list
		// (even non-sticky lists) are removed
		probeRemoved.trigger();
	}


	public void clear()	{		
		for ( ProbeList pl : getProbeLists() )
			pl.removeProbeListListener( this );
		treeModel.clear();		
		fireProbeListManagerChanged( ProbeListManagerEvent.OVERALL_CHANGE );
	}	


	public void probeListChanged( ProbeListEvent event ) {
		// currently the events are simply propagated 
		if ( ( event.getChange() & ProbeListEvent.CONTENT_CHANGE ) != 0 )
			fireProbeListManagerChanged( ProbeListManagerEvent.CONTENT_CHANGE );      

		if ( ( event.getChange() & ProbeListEvent.ANNOTATION_CHANGE ) != 0 )
			fireProbeListManagerChanged( ProbeListManagerEvent.ANNOTATION_CHANGE );      

		if ( ( event.getChange() & ProbeListEvent.LAYOUT_CHANGE ) != 0 ) 
			fireProbeListManagerChanged( ProbeListManagerEvent.LAYOUT_CHANGE );
	}   


	public boolean isSilent() {
		return ( isSilent );
	}


	public void setSilent( boolean isSilent ) {
		this.isSilent = isSilent;
	}


	public void setDataSet(DataSet dataSet) {
		this.dataSet=dataSet;
	}

	public DataSet getDataSet() {
		return dataSet;
	}

	public int getNumberOfObjects() {
		return treeModel.getSize();
	}

	public void addObject(Object object) {
		addObject((ProbeList)object);
	}

	public void addObjectAtBottom(Object object) {
		addObjectAtBottom((ProbeList)object);
		
	}

	public void addObjectAtTop(Object object) {
		addObjectAtTop((ProbeList)object);	
	}

	public boolean contains(String name) {		
		return contains(name,null);
	}

	public boolean contains(Object obj) {
		return Arrays.asList(treeModel.toArray()).contains(obj);
	}

	public ListModel getModel() {
		return treeModel;
	}

	@SuppressWarnings("unchecked")
	public List getObjects() {
		return Arrays.asList(treeModel.toArray());
	}

	public void removeObject(Object object) {
		removeObject((ProbeList)object);
	}



	public void replaceObject(Object object, Object replacement) {
		replaceObject((ProbeList)object, (ProbeList)replacement);
	}
	
	public DefaultTreeModel getTreeModel() {
		return treeModel;
	}

	
    public void addStoreListener(StoreListener listener) {
        storeListenerList.add(StoreListener.class, listener);
    }
    
    public void removeStoreListener(StoreListener listener) {
        storeListenerList.remove(StoreListener.class, listener);
    }

    public void fireObjectAdded(Object obj) {
    	StoreEvent se = new StoreEvent(fakeStore, obj);
    	for(StoreListener l:storeListenerList.getListeners(StoreListener.class))
    		l.objectAdded(se);
    }
    
    public void fireObjectRemoved(Object obj) {
    	StoreEvent se = new StoreEvent(fakeStore, obj);
    	for(StoreListener l:storeListenerList.getListeners(StoreListener.class))
    		l.objectRemoved(se);
    }

	public ProbeList getProbeList(String name) {
		for (ProbeList pl : getProbeLists())
			if (pl.getName().equals(name))
				return pl;
		return null;
	}

	public ProbeList getSharedAncestor(Collection<ProbeList> probeLists) {
		ProbeListNode parentNode=null;
		for (ProbeList pl : probeLists)  {
			if (pl.getParent()==null)
				continue;
			ProbeListNode plnode = treeModel.nodeOf(pl.getParent(), treeModel.getRoot());
			if (parentNode==null)
				parentNode = plnode;
			else
				parentNode = (ProbeListNode)plnode.getSharedAncestor(parentNode);
		}
		return (parentNode==null?null:parentNode.getProbeList());
	}
	
	public List<ProbeList> getProbeListsBelow(ProbeList probelistparent) {
		ProbeListNode plnode = treeModel.nodeOf(probelistparent, treeModel.getRoot());
		LinkedList<ProbeList> res = new LinkedList<ProbeList>();
		for (int i=0; i!=plnode.getChildCount(); ++i) {
			res.add(((ProbeListNode)plnode.getChildAt(i)).getProbeList());
		}
		return res;
	}
	
	public void orderChangedExternally() {
		fireProbeListManagerChanged(ProbeListManagerEvent.ORDER_CHANGE);
	}

}