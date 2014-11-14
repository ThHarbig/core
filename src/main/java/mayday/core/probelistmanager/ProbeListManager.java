package mayday.core.probelistmanager;

import java.util.Collection;
import java.util.List;

import javax.swing.ListModel;

import mayday.core.DataSet;
import mayday.core.ProbeList;
import mayday.core.ProbeListListener;
import mayday.core.StoreListener;
import mayday.core.probelistmanager.gui.ProbeListManagerView;


public interface ProbeListManager extends ProbeListListener
{
	public void setProbeListManagerView(ProbeListManagerView plmv);
	public ProbeListManagerView getProbeListManagerView();

	public DataSet getDataSet();
	public void setDataSet(DataSet ds);
	
	@SuppressWarnings("unchecked")
	public List getObjects();
	public List<ProbeList> getProbeLists();
	@SuppressWarnings("unchecked")
	public void setObjects( java.util.List objects ); 

	public int getNumberOfObjects();
	
	public boolean contains(String name);
	public boolean contains(Object obj);
	
	public ProbeList getProbeList(String name);
	
	public void addProbeListManagerListener( ProbeListManagerListener listener );
	public void removeProbeListManagerListener( ProbeListManagerListener listener );

	public boolean moveUpProbeList( int index );
	public boolean moveUpProbeList( ProbeList probeList );
	public boolean moveDownProbeList( int index );
	public boolean moveDownProbeList( ProbeList probeList );

	public void addObject( Object object );	
	public void addObjectAtBottom( Object object );
	public void addObjectAtTop( Object object );

	public void removeObject( Object object );
	
	public void orderChangedExternally();

	public void replaceObject(Object object, Object replacement);

	public void clear();
	
	public boolean isSilent();	
	public void setSilent( boolean isSilent );

	public void addStoreListener(StoreListener list);
	public void removeStoreListener(StoreListener list);
	
	public ProbeList getSharedAncestor(Collection<ProbeList> probeLists);
	public List<ProbeList> getProbeListsBelow(ProbeList probelistparent);
	
	public ListModel getModel();	
}