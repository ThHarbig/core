package mayday.core.probelistmanager;

import java.util.Collection;
import java.util.List;
import java.util.TreeSet;

import javax.swing.ListModel;

import mayday.core.DataSet;
import mayday.core.DelayedUpdateTask;
import mayday.core.EventFirer;
import mayday.core.Probe;
import mayday.core.ProbeList;
import mayday.core.ProbeListEvent;
import mayday.core.ProbeListStore;
import mayday.core.meta.MIGroup;
import mayday.core.meta.MIGroupSelection;
import mayday.core.meta.MIType;
import mayday.core.probelistmanager.gui.ProbeListManagerView;


/*
 * Created on Mar 29, 2003
 *
 */

/**
 * @author Nils Gehlenborg
 * @version 0.1 
 */
@SuppressWarnings("serial")
public class ProbeListManagerList
extends ProbeListStore
implements ProbeListManager
{
	protected EventFirer<ProbeListManagerEvent, ProbeListManagerListener> eventfirer
	= new EventFirer<ProbeListManagerEvent, ProbeListManagerListener>() {
		protected void dispatchEvent(ProbeListManagerEvent event, ProbeListManagerListener listener) {
			listener.probeListManagerChanged(event);
		}		
	};

	private ProbeListManagerView probeListManagerView; // fb 080319 added to get at the seleected probelists from outside 

	private boolean isSilent; // indicates whether listeners are notified or not

	public ProbeListManagerList( DataSet dataSet ){
		super();
		setDataSet( dataSet );  	
	}

	public void setProbeListManagerView(ProbeListManagerView plmv) {
		probeListManagerView = plmv;
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


	public boolean moveUpProbeList( int index )	{
		boolean l_result = super.moveUpProbeList( index ); 
		fireProbeListManagerChanged( ProbeListManagerEvent.ORDER_CHANGE );
		return ( l_result );
	}


	public boolean moveUpProbeList( ProbeList probeList ) {
		boolean l_result =  super.moveUpProbeList( probeList );
		fireProbeListManagerChanged( ProbeListManagerEvent.ORDER_CHANGE );
		return ( l_result );
	}


	public boolean moveDownProbeList( int index ) {
		boolean l_result =  super.moveDownProbeList( index );
		fireProbeListManagerChanged( ProbeListManagerEvent.ORDER_CHANGE );
		return ( l_result );
	}


	public boolean moveDownProbeList( ProbeList probeList )	{		
		boolean l_result = super.moveDownProbeList( probeList ); 
		fireProbeListManagerChanged( ProbeListManagerEvent.ORDER_CHANGE );
		return ( l_result );
	}


	@SuppressWarnings("unchecked")
	public void setObjects( java.util.List objects ) {
		super.setObjects( objects );
		for ( int i = 0; i < objects.size(); ++i ) {
			((ProbeList)objects.get( i ) ).addProbeListListener( this );
		}
		fireProbeListManagerChanged( ProbeListManagerEvent.CONTENT_CHANGE );		    
	}
	
	protected void verifyNameUniqueness( ProbeList newPL ) {     
		
		ProbeListNamer.ensureNameUniqueness(this, newPL);
		
//        while ( newPL.getName().equals("") || this.contains( newPL.getName() , newPL ) ) {
//            String message = MaydayDefaults.Messages.PROBE_LIST_NOT_UNIQUE;
//            message = message.replaceAll( MaydayDefaults.Messages.REPLACEMENT, newPL.getName() );
//            message += "\n" + MaydayDefaults.Messages.ENTER_NEW_NAME;
//            
//            String name = (String)JOptionPane.showInputDialog( null,
//                message,
//                MaydayDefaults.Messages.WARNING_TITLE,
//                JOptionPane.WARNING_MESSAGE,
//                null,
//                null,
//                newPL.getName() );
//            
//            name=(name==null?"":name);
//                     
//            newPL.setName( name );                                                   
//        }
	}

	public void replaceObject(Object object, Object replacement) {
		int index = indexOf(object);
		super.removeObject(object);
		verifyNameUniqueness((ProbeList)object);
		add(index,replacement);
		((ProbeList)replacement).addProbeListListener( this );
		fireProbeListManagerChanged( ProbeListManagerEvent.CONTENT_CHANGE );
	}


	public void addObject( Object object ){
		if (contains(object)) {
			System.err.println("ProbeListManager: Cannot add probelist, because it is already contained: "+object.toString());
			return;
		}
		verifyNameUniqueness((ProbeList)object);
		super.addObject( object );
		((ProbeList)object).addProbeListListener( this );
		fireProbeListManagerChanged( ProbeListManagerEvent.CONTENT_CHANGE );
	}


	public void addObjectAtBottom( Object object ) {
		addObject(object); // the same
	}


	public void addObjectAtTop( Object object )	{
		verifyNameUniqueness((ProbeList)object);
		super.addObjectAtTop( object );
		((ProbeList)object).addProbeListListener( this );
		fireProbeListManagerChanged( ProbeListManagerEvent.CONTENT_CHANGE );
	}


	@SuppressWarnings("unchecked")
	public List<ProbeList> getProbeLists() {
		return (List<ProbeList>)getObjects();
//		LinkedList<ProbeList> ret = new LinkedList<ProbeList>();
//		for (Object o : getObjects())
//			ret.add((ProbeList)o);
//		return ret;
	}

	protected DelayedUpdateTask probeRemoved = new DelayedUpdateTask("MasterTable cleanup") {

		protected boolean needsUpdating() {
			return true;
		}

		protected void performUpdate() {
			Collection<Probe> everything = new TreeSet<Probe>(getDataSet().getMasterTable().getProbes().values());
			for (ProbeList pl : getProbeLists()) {
				Collection<Probe> accounted_for = pl.getAllProbes();
				everything.removeAll(accounted_for);
			}
			if (everything.size()>0) {
				System.out.println("Removing "+everything.size()+" probes after ProbeList closing.");
				for (Probe pb : everything) {
					MIGroupSelection<MIType> mgs = getDataSet().getMIManager().getGroupsForObject(pb);
					for (MIGroup mg : mgs)
						mg.remove(pb);
					getDataSet().getMasterTable().removeProbe(pb,true);
				}			
			}
		}
		
	};
	
	public void removeObject( Object object )
	{
		super.removeObject( object );

		((ProbeList)object).removeProbeListListener( this );
		((ProbeList)object).fireProbeListChanged( ProbeListEvent.PROBELIST_CLOSED );

		((ProbeList)object).propagateClosing();

		fireProbeListManagerChanged( ProbeListManagerEvent.CONTENT_CHANGE );

		// now, if a probe list is closed, all it's probes that are not contained in any other list
		// (even non-sticky lists) are removed
		probeRemoved.trigger();
	}


	public void clear()	{
		for ( int i = 0; i < size(); ++i )
			((ProbeList)getElementAt( i ) ).removeProbeListListener( this );
		super.clear();
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

	public ListModel getModel() {
		return this;
	}

	public ProbeList getProbeList(String name) {
		for (ProbeList pl : getProbeLists())
			if (pl.getName().equals(name))
				return pl;
		return null;
	}

	public ProbeList getSharedAncestor(Collection<ProbeList> probeLists) {
		return null; // always root
	}

	public void orderChangedExternally() {
		fireProbeListManagerChanged(ProbeListManagerEvent.ORDER_CHANGE);
	}

	public List<ProbeList> getProbeListsBelow(ProbeList probelistparent) {
		return null; // no children here
	}

}