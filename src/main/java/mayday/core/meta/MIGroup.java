package mayday.core.meta;

import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.WeakHashMap;
import java.util.Map.Entry;

import mayday.core.EventFirer;
import mayday.core.pluma.PluginInfo;

public class MIGroup {

	private String name;
	private PluginInfo pli;
	private MIManager manager;

	private Map<Object, MIType> contents= Collections.synchronizedMap(new WeakHashMap<Object, MIType>());

	protected EventFirer<MIGroupEvent, MIGroupListener> eventfirer
	= new EventFirer<MIGroupEvent, MIGroupListener>() {
		protected void dispatchEvent(MIGroupEvent event, MIGroupListener listener) {
			listener.miGroupChanged(event);
		}		
	};

	// constructor is only called from MIManager
	/** Create a new MIGroup. This method should only be called by MIManager. It is left public in case
	 * some plugin needs to do really strange things. Use MIManager.newGroup() instead.
	 */
	public MIGroup(PluginInfo Pli, String groupName, MIManager Manager) {
		if (Pli==null || groupName ==null)
			throw new RuntimeException("Cannot create MIO Group without PluginInfo or Group Name");
		pli=Pli;
		if (groupName.contains("/"))
			System.err.println("MIGroup names may not contain forward slashes (\"/\") - replacing.");
		name=groupName.replace("/", "|"); 
		manager=Manager;
	}

	/** Rename this MIGroup. The change will trigger either a MIGroupChanged event from this object (if it is not
	 * contained in any MIManager, should rarely happen), or a MIManagerEvent informing listeners of this change.
	 * @param groupName The new name for this group
	 */
	public void setName(String groupName) {
		String oldName = name;
		if (groupName.contains("/"))
			System.err.println("MIGroup names may not contain forward slashes (\"/\") - replacing.");
		name=groupName.replace("/", "|"); 
		if (manager!=null) {
			manager.renameGroup(this, oldName, groupName);			
		} else {
			this.fireMIGroupChanged(MIGroupEvent.OVERALL_CHANGE, this);
		}		
	}

	// only called from MIManager.addGroup()
	void setManager(MIManager Manager) {
		manager = Manager;
	}

	/** get the name of this MIGroup
	 * @return the name of this group
	 */
	public String getName() {
		return name;
	}

	/** get the type of the MIOs in this group as represented by a Pluma ID 
	 * @return the Pluma ID of the MIOs in this group
	 */
	public String getMIOType() {
		return pli.getIdentifier();
	}

	/** get the PluginInfo object for the MIO type of the MIOs in this group.
	 * @return the Pluma PluginInfo object for the contained MIO type.
	 */
	public PluginInfo getMIOPluginInfo() {
		return pli;
	}

	/** get the Java Class for the MIO type of the MIOs in this group.
	 * @return the Java Class for the contained MIO type.
	 */
	@SuppressWarnings("unchecked")
	public Class<? extends MIType> getMIOClass() {
		return (Class<? extends MIType>)pli.getPluginClass();
	}


	/** Get the containing MIManager.
	 * @return The MIManager instance that this group belongs to, or null if this group is not contained in any 
	 * MIManager.
	 */
	public MIManager getMIManager() {
		return manager;
	}


	/** Add a new object (Probe, ProbeList, DataSet,...) to this group and link it to a MIO object.
	 * MIGroupListener will be notified if they are listening to this group or if they have been added
	 * to the MIManager using MIManager.addListenerForObject().  
	 * @param mioExtendable The object to add
	 * @param mio The MIO to link to the object.
	 * @throws RuntimeException if the MIO object is of the wrong type. 
	 */
	public void add(Object mioExtendable, MIType mio) {
		if (mio==null)
			this.remove(mioExtendable);
		else {
			if (mio.getClass() != pli.getPluginClass())
				throw new RuntimeException("Can not add MIO of class \""+mio.getClass()
						+"\" to a MIGroup of type \""+this.getMIOType()+"\", class \""+pli.getPluginClass()+"\"");
			int event = (contents.containsKey(mioExtendable)) ? MIGroupEvent.MIO_REPLACED : MIGroupEvent.MIO_ADDED;
			contents.put(mioExtendable, mio);		
			fireMIGroupChanged(event, mioExtendable);
		}
	}

	/** Add a new object (Probe, ProbeList, DataSet,...) to this group, create a new MIO object and link it to
	 * the object
	 * @param mioExtendable The object to add
	 * @return the newly created MIO object. Use setValue() to initialize the MIO Object.
	 */
	public MIType add(Object mioExtendable) {
		MIType mio = MIManager.newMIO(getMIOType());
		add(mioExtendable, mio);
		return mio;
	}

	/** Remove an object from this group
	 * MIGroupListener will be notified if they are listening to this group or if they have been added
	 * to the MIManager using MIManager.addListenerForObject().  
	 * @param mioExtendable the object to remove
	 */
	public void remove(Object mioExtendable) {
		if (contents.containsKey(mioExtendable)) { 
			contents.remove(mioExtendable);
			fireMIGroupChanged(MIGroupEvent.MIO_REMOVED, mioExtendable);
			if (contents.size()==0 && getMIManager()!=null)
				getMIManager().removeGroup(this);
		}
	}

	/** Get all objects that have MIOs in this group
	 * @return A collection of all mio-annotated objects
	 */
	public Collection<Object> getObjects() {
		return contents.keySet();
	}

	/** Get all objects that have MIOs in this group AND are of a given type (e.g. ProbeList.class)
	 * @param T the class that all objects have to be of
	 * @return   A parametrized collection of all mio-annotated objects
	 */
	@SuppressWarnings("unchecked")
	public <T> Collection<T> getObjectsForClass(Class T) {
		LinkedList<T> ret = new LinkedList<T>();
		for (Object o : getObjects()) 
			if (o.getClass() == T)
				ret.add((T)o);
		return ret;
	}

	/** Check if an object is annotated with a MIO object in this group
	 * @param o The object to check
	 * @return true if a MIO is present in this MIGroup for the given object
	 */
	public boolean contains(Object o) {
		return contents.containsKey(o);
	}

	/** Get the MIO annotation for a given object
	 * @param o The object that the MIO should be linked to
	 * @return The desired MIO or null if the object is not in this group.
	 */
	public MIType getMIO(Object o) {
		return contents.get(o);
	}

	/** Get a Map linking objects to MItypes. Use this function for efficient iteration over this group.
	 * @return The Map.
	 */
	public Set<Entry<Object, MIType>> getMIOs() {
		return contents.entrySet();
	}

	/** Get a Map linking objects to MItypes where the objects are of a given class (e.g. ProbeList). 
	 * @param T the class that all objects have to be of
	 * @return The parametrized Map.
	 */
	@SuppressWarnings("unchecked")
	public <T> Set<Entry<T, MIType>> getMIOs(Class T) {
		Set<Entry<T, MIType>> ret = new HashSet<Entry<T,MIType>>();
		for (Entry<Object,MIType> o : getMIOs()) 
			if (o.getKey().getClass() == T)
				ret.add((Entry<T,MIType>)o);
		return ret;
	}

	/** Get a list of all objects that are annotated by one specific MIO object instance. Note that this reverse
	 * lookup is a very slow operation (O(n) over all annotated objects in this MIGroup).
	 * @param mio The MIO instance to scan for
	 * @return the list of objects annotated by the mio instance
	 */
	@Deprecated //slow 
	public List<Object> getObjectsForMIO(MIType mio) {
		LinkedList<Object> ret = new LinkedList<Object>();
		for(Entry<Object, MIType> e : getMIOs()) {
			if (e.getValue() == mio)
				ret.add(e.getKey());
		}
		return ret;
	}

	public String getPath() {
		if (getMIManager()==null)
			return null;
		String s = getMIManager().getTreeRoot().getPathFor(this);
		if (!s.endsWith(this.getName()))
			throw new RuntimeException("Path returned by the MIManager is incorrect!");
		return s.substring(0, s.lastIndexOf("/"));
	}
	
	/** Clone this MIGroup. This is not a deep clone, i.e. the MIO objects and the annotated objects are NOT cloned.
	 * @see java.lang.Object#clone()
	 */
	public MIGroup clone() {
		MIGroup ret = new MIGroup(pli, getName()+" - Clone", null);
		for(Entry<Object, MIType> e : getMIOs()) {
			ret.add(e.getKey(), e.getValue());
		}
		return ret;
	}


	/** Get the number of objects annotated in this MIGroup
	 * @return The number of annotated objects
	 */
	public int size() {
		return this.contents.size();
	}

	public String toString() {
		return this.getName()+" ["+this.size()+"]";
	}

	/** Add a MIGroupListener that will be notified when objects are added or removed from this group
	 * @param listener the listener to add
	 */
	public void addMIGroupListener( MIGroupListener listener ) {
		eventfirer.addListener( listener );
	}


	/** Remove a MIGroupListener
	 * @param listener the listener to remove
	 */
	public void removeMIGroupListener( MIGroupListener listener ) {
		eventfirer.removeListener( listener );
	}


	// public because fake events are fired from AbstractMIRenderer
	public void fireMIGroupChanged( int change, Object object )	{
		eventfirer.fireEvent(new MIGroupEvent(this, change, object));
	}


}
