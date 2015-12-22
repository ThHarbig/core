/**
 * This is Mayday's Meta Information Manager. Meta Information Objects (MIOs) are arranged in Groups (MIGroups) 
 * constituting logical groupings (e.g. one group contains p-values for a given statistical test). Each MI Object
 * represents one value and can be associated with several objects (Probes, ProbeLists, DataSets, MIOs).
 * Each MIGroup in the MIManager has an ID that is stable over the lifetime of the MIManager object.
 * @author Florian Battke
 */
package mayday.core.meta;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Set;
import java.util.TreeMap;

import javax.swing.event.EventListenerList;

import mayday.core.DataSet;
import mayday.core.EventFirer;
import mayday.core.meta.miotree.Directory;
import mayday.core.meta.miotree.FileIterator;
import mayday.core.pluma.AbstractPlugin;
import mayday.core.pluma.Constants;
import mayday.core.pluma.PluginInfo;
import mayday.core.pluma.PluginManager;
import mayday.core.pluma.PluginManagerException;
import mayday.core.pluma.prototypes.CorePlugin;
import mayday.core.structures.maps.MultiTreeMap;


public class MIManager extends AbstractPlugin implements CorePlugin, MIGroupListener {

	private static TreeMap<String, PluginInfo> _mioTypes;

	public synchronized static TreeMap<String, PluginInfo> getMioTypes() {
		if (_mioTypes==null) {
			_mioTypes = new TreeMap<String, PluginInfo>();
			Set<PluginInfo> plis = PluginManager.getInstance().getPluginsFor(Constants.MC_METAINFO);
			for (PluginInfo pli : plis)
				if (pli.getInstance() instanceof MIType)
					_mioTypes.put(pli.getIdentifier(), pli);
		}
		return _mioTypes;
	}


	private DataSet dataSet;

	// even if MIOs are deleted, the ids are stable over the lifetime of the MIManager
	private ArrayList<MIGroup> mioGroups = new ArrayList<MIGroup>();
	private MultiTreeMap<String, Integer> mioName2id = new MultiTreeMap<String, Integer>();
	private MultiTreeMap<String, Integer> mioType2id = new MultiTreeMap<String, Integer>();
	private Directory treeRoot = new Directory("", this);


	protected EventFirer<MIManagerEvent, MIManagerListener> eventfirer
		= new EventFirer<MIManagerEvent, MIManagerListener>() {
		protected void dispatchEvent(MIManagerEvent event, MIManagerListener listener) {
			listener.miManagerChanged(event);
		}		
	};
	
	private EventListenerList objectListenerList = new EventListenerList(); // these are already delayed by the respective migroups

	/** Default constructor, only needed for Pluma. Do not use this for any other purpose!
	 */
	public MIManager() {
		// default constructor needed for pluginmanager  
	}

	/** Create a new MIManager for a given dataset
	 * 	@param dataSet the dataset this mimanager belongs to
	 */
	public MIManager( DataSet dataSet ) {  
		this.dataSet = dataSet;

		// add annotation mio group to make sure it's always the first mio group
		this.newGroup("PAS.MIO.Annotation", "Annotation");
	}


	/** get the dataset that this mimanager belongs to
	 * @return the dataset
	 */
	public DataSet getDataSet() {
		return ( this.dataSet );
	}


	private int addGroup(String groupName, String groupType, MIGroup group, String treePath) {
		if (groupType.equals("PAS.MIO.Annotation") && mioType2id.containsKey("PAS.MIO.Annotation"))
			throw new RuntimeException("There can only ever be ONE miogroup for Annotations per DataSet");
		int id = mioGroups.size();
		mioName2id.put(groupName, id);
		mioType2id.put(groupType, id);
		mioGroups.add(group);
		treeRoot.getDirectory(treePath, true).putDirectory(new Directory(group));
		group.addMIGroupListener(this);
		fireMIManagerChanged( MIManagerEvent.GROUP_ADDED );
		return id;
	}


	/** Create a new MIGroup in this MIManager. 
	 * @param mioType The Pluma ID of the MIO type for the new group, e.g. PAS.MIO.String. Note that only ONE group of
	 * the annotation type (PAS.MIO.Annotation) can be present in a MIManager. Thus if mioType is set to "PAS.MIO.Annotation",
	 * the already existing annotation group is returned.
	 * @param groupName The name of the new group
	 * @param treePath The path in the mio tree hierarchy where this group should be found
	 * @return The new MIGroup that you can now add MIOs to.
	 * @throws RuntimeException if the mioType can not be found by Pluma.
	 */
	public MIGroup newGroup(String mioType, String groupName, String treePath) {
		if (mioType.equals("PAS.MIO.Annotation") && mioType2id.containsKey("PAS.MIO.Annotation"))
			return getGroupsForType("PAS.MIO.Annotation").get(0);
		PluginInfo pli;
		if ( (pli=getMioTypes().get(mioType))==null )
			throw new RuntimeException("Can not create MIGroup \""+groupName+
					"\", because the MIType \""+mioType+"\" was not found.");
		MIGroup ng = new MIGroup(pli, groupName, this);	  
		addGroup(groupName, mioType, ng, treePath);
		return ng;
	}

	/** Create a new MIGroup in this MIManager. 
	 * @param mioType The Pluma ID of the MIO type for the new group, e.g. PAS.MIO.String. Note that only ONE group of
	 * the annotation type (PAS.MIO.Annotation) can be present in a MIManager. Thus if mioType is set to "PAS.MIO.Annotation",
	 * the already existing annotation group is returned. The new group is added below an already existing group in the
	 * tree hierarchy.
	 * @param groupName The name of the new group
	 * @param parent The existing group to add below.
	 * @return The new MIGroup that you can now add MIOs to.
	 * @throws RuntimeException if the mioType can not be found by Pluma.
	 */
	public MIGroup newGroup(String mioType, String groupName, MIGroup parent) {
		String treepath = getTreeRoot().getPathFor(parent);
		return newGroup(mioType, groupName, treepath);
	}
	
	/** Create a new MIGroup in this MIManager. 
	 * @param mioType The Pluma ID of the MIO type for the new group, e.g. PAS.MIO.String. Note that only ONE group of
	 * the annotation type (PAS.MIO.Annotation) can be present in a MIManager. Thus if mioType is set to "PAS.MIO.Annotation",
	 * the already existing annotation group is returned.
	 * This is equivalent to calling newGroup(mioType, groupName, ""), i.e. the new group is added in the root of the
	 * mio tree hierarchy.
	 * @param groupName The name of the new group
	 * @return The new MIGroup that you can now add MIOs to.
	 * @throws RuntimeException if the mioType can not be found by Pluma.
	 */
	public MIGroup newGroup(String mioType, String groupName) {
		return newGroup(mioType, groupName, "");
	}
	

	/** Add an already existing MIGroup to this MIManager. Usually, newGroup() should be used to create new groups.
	 * This function is only added for plugins that need to do strange things with MIO Groups.
	 * @param group The MIGroup to add
	 * @param treePath The path in the mio tree hierarchy where this group should be found
	 * @return the index of the group, to be used via getGroup(int)
	 */
	public int addGroup(MIGroup group, String treePath) {
		String mioType = group.getMIOType();
		if (getMioTypes().get(mioType)==null)
			throw new RuntimeException("Can not add MIGroup \""+group.getName()+
					"\", because the MIType \""+mioType+"\" was not found.");
		if (group.getMIManager()!=null) {
			if (group.getMIManager()!=this) {
				throw new RuntimeException("MIGroup \""+group.getName()+
				"\" already belongs to another MIManager.");
			} else {
				if (!this.mioGroups.contains(group)) {
					addGroup(group.getName(), mioType, group, treePath); 
				}
				return mioGroups.indexOf(group);
			} 
		}
		group.setManager(this);
		return addGroup(group.getName(), mioType, group, treePath);  
	}

	/** Adds a new mio group below an already existing one in the mio tree
	 * @param group the group to add
	 * @param parent the existing group to add below
	 * @return the index of the added group.
	 */
	public int addGroupBelow(MIGroup group, MIGroup parent) {
		String parentpath = getTreeRoot().getPathFor(parent);
		return addGroup(group, parentpath);
	}
	
	/** Add an already existing MIGroup to this MIManager. Usually, newGroup() should be used to create new groups.
	 * This function is only added for plugins that need to do strange things with MIO Groups.
	 * This is equivalent to calling addGroup(group, ""), i.e. the group is added in the root of the
	 * mio tree hierarchy.
	 * @param group The MIGroup to add
	 * @return the index of the group, to be used via getGroup(int)
	 */
	public int addGroup(MIGroup group) {
		return addGroup(group, "");
	}


	private void removeGroup(String mioType, String groupName, int id) {
		if (mioType.equals("PAS.MIO.Annotation"))
			throw new RuntimeException("Annotation MIO Group can never be removed.");
		mioName2id.remove(groupName, id);
		mioType2id.remove(mioType, id);
		treeRoot.removeGroup(mioGroups.get(id),true);
		mioGroups.set(id,null);
		fireMIManagerChanged( MIManagerEvent.GROUP_DELETED );
	}


	/** Remove a MIO group by its group id
	 * @param id the group to remove
	 */
	public void removeGroup(int id) {
		MIGroup grp = mioGroups.get(id);
		removeGroup(grp.getMIOType(), grp.getName(), id);
	}

	/** Remove a MIO group from the MIManager
	 * @param group the group to remove
	 */
	public void removeGroup(MIGroup group) {
		if (mioGroups.contains(group))
			removeGroup(group.getMIOType(), group.getName(), mioGroups.indexOf(group));
	}
	
	public int getGroupID(MIGroup mg) {
		return mioGroups.indexOf(mg);
	}


	/** Get a MIGroup from this manager by its group id
	 * @param identifier the group id (index) to retrieve
	 * @return the selected group or null, if it was removed previously 
	 * @throws ArrayIndexOutOfBoundsException if the identifer is not valid.
	 */
	public MIGroup getGroup(int identifier) {
		return mioGroups.get(identifier);
	}


	/** Get a list of MIGroups for a given list of Group IDs. 
	 * @param ids the groups to retrieve
	 * @return a MIGroupSelection of the selected groups. Items in this selection can be null if the respective
	 * groups have been deleted previously.
	 */
	public MIGroupSelection<MIType> getGroupsForIDs(List<Integer> ids) {
		MIGroupSelection<MIType> ret = new MIGroupSelection<MIType>();
		for (int i : ids) 
			ret.add(mioGroups.get(i));
		return ret;	  
	}

	/** Get a list of MIGroups for a given MIO Type as described by a Pluma ID.
	 * @param mioType The Pluma ID of the MIO Type that the groups have to contain
	 * @return The MIGroupSelection of the selected groups.
	 */
	public MIGroupSelection<MIType> getGroupsForType(String mioType) {
		List<Integer> ids = mioType2id.get(mioType);
		return getGroupsForIDs(ids);
	}

	/** Get a list of MIGroups with a given name
	 * @param groupName The name of the groups to retrieve
	 * @return The MIGroupSelection of the selected groups.
	 */
	public MIGroupSelection<MIType> getGroupsForName(String groupName) {
		List<Integer> ids = mioName2id.get(groupName);
		return getGroupsForIDs(ids);
	}  

	/** Get a list of MIGroups containing a given object
	 * @param mioExtendable The object that needs to be contained in the groups
	 * @return The MIGroupSelection of the selected groups.
	 */
	public MIGroupSelection<MIType> getGroupsForObject(Object mioExtendable) {
		MIGroupSelection<MIType> ret = new MIGroupSelection<MIType>();
		for (MIGroup mg : mioGroups)
			if (mg!=null && mg.contains(mioExtendable))
				ret.add(mg);
		return ret;	  
	}  

	/** Get a list of MIGroups containing MIOs that extend a given superclass or implement an interface
	 * @param theInterface The superclass resp interface that the MIO objects need to extend resp implement.
	 * @return The MIGroupSelection of the selected groups.
	 */
	public <T extends MIType> MIGroupSelection<T> getGroupsForInterface(Class<T> theInterface) {
		MIGroupSelection<T> ret = new MIGroupSelection<T>();
		for (MIGroup grp : mioGroups)
			if (grp!=null && theInterface.isAssignableFrom(grp.getMIOClass()))
				ret.add(grp);
		return ret;
	}

	public <T extends MIType> MIGroupSelection<T> getGroupsForInterfaces(Class<T>[] theInterfaces) {
		return getGroupsForInterfaces(theInterfaces, false);
	}
	
	/** Get a list of MIGroups containing MIOs that extend any of a given set of superclasses or implement an interface
	 * @param theInterfaces The superclasses resp interfaces that the MIO objects need to extend resp implement.
	 * @param strict don't allow inheritance
	 * @return The MIGroupSelection of the selected groups.
	 */
	public <T extends MIType> MIGroupSelection<T> getGroupsForInterfaces(Class<T>[] theInterfaces, boolean strict) {
		MIGroupSelection<T> ret = new MIGroupSelection<T>();
		for (MIGroup grp : mioGroups) {
			if (grp!=null) {
				for (Class<T> ct : theInterfaces) {
					Class<?> cc = grp.getMIOClass();
					if (!strict) {
						if (ct.isAssignableFrom(cc)) {
							ret.add(grp);
							break;
						}
					} else {						
						if (ct.equals(cc)) {
							ret.add(grp);
							break;
						}
					}
				}
			}
		}
		return ret;
	}
	
	/** Get a list of MIGroups below a given path in the mio tree hierarchy.
	 * Note that most MIGroups will be contained in the root note ("/") because most plugins don't use
	 * the mio hierarchy mechanism.
	 * @param path The root node that the desired MIO groups reside under
	 * @return recursive If set to true, all MIO Groups below the path are returned, if set to false only those 
	 * groups directly below the given path are returned.
	 */
	public MIGroupSelection<MIType> getGroupsForPath(String path, boolean recursive) {
		Directory d = treeRoot.getDirectory(path, false);
		MIGroupSelection<MIType> ret = new MIGroupSelection<MIType>();
		if (d==null)
			return ret;
		
		FileIterator fmfi = d.getFiles(recursive);
		while (fmfi.hasNext()) {
			MIGroup agroup = fmfi.next();
			if (!ret.contains(agroup)) 
				ret.add(agroup);
		}

		return ret;
	}
	
	/** Get exactly one MIGroup as identified by its full path in the MIO tree hierarchy.
	 * @param path The full path to the MIGroup including its name
	 * @return the desired MIGroup or null if none exists for that path
	 */
	public MIGroup getGroupByPath(String path) {
		if (path.length()==0)
			return null;
		int pos = path.lastIndexOf("/");
		String p,n;
		if (pos>-1) {
			p = path.substring(0,pos);
			n = path.substring(pos+1);
		} else {
			p="";
			n=path;
		}
		MIGroupSelection<MIType> mgs = getGroupsForPath(p, true).filterByName(n);
		
		
		if (mgs.size()>0)
			return mgs.get(0);
		return null;
	}

	/** Get a list of all MIGroups contained in the MIManager
	 * @return The MIGroupSelection of all groups. This object does NOT contain deleted groups, i.e. it is guaranteed
	 * that all items in the list are not null.
	 */
	public MIGroupSelection<MIType> getGroups() {
		MIGroupSelection<MIType> ret = new MIGroupSelection<MIType>();
		for (MIGroup grp : mioGroups)
			if (grp!=null)
				ret.add(grp);
		return ret;
	}

	/** Get the root node of the MIO tree hierarchy.
	 * @return The root node
	 */
	public Directory getTreeRoot() {
		return treeRoot;
	}

	/** Get a list of all available MIO types, represented by Pluma IDs that can be used in newGroup(). 
	 * @return The set of all available types.
	 */
	public static Set<String> getAvailableTypes() {
		return getMioTypes().keySet();
	}

	/** Get a list of all MIO types that are represented by at least one MIGroup in this MIManager instance.
	 * For a list of all _available_ types, use getAvailableTypes(). 
	 * @return The set of all present types.
	 */
	public Set<String> getTypes() {
		return mioType2id.keySet();
	}

	//only called from MIGroup
	void renameGroup(MIGroup group, String oldname, String newname) {
		int id = mioGroups.indexOf(group);
		if (id==-1)
			return;
		String tp = getTreeRoot().getPathFor(group);
		if (tp!=null) {// not already in a renaming process
			mioName2id.remove(oldname, id);
			Directory d = getTreeRoot().getDirectory(tp, false);
			Directory dp = d.getParent();
			dp.removeDirectory(d);
			dp.putDirectory(d);
			if (!newname.equals(d.getName())) {
				System.err.println("MIGroup name change from \""+oldname+"\" to \""+newname+"\" had to be changed to \""+d.getName()+"\"");
				newname = d.getName();			
			}
			mioName2id.put(newname,id);
			fireMIManagerChanged(MIManagerEvent.GROUP_RENAMED);
		}
	}

	/** Convert a Pluma ID to a Java Class. If you need to use this, make sure you really do. In general, using
	 * Pluma IDs is a lot safer than using class names.
	 * @param mioType The Pluma ID of the desired MIO class.
	 * @return A Class object for the desired mio type or null, if the type was not found.
	 */
	@SuppressWarnings("unchecked")
	public static Class<? extends MIType> getMIOClass(String mioType) {
		PluginInfo pli = getMIOPluginInfo(mioType);
		if (pli==null)
			return null;
		return (Class<? extends MIType>)pli.getPluginClass();
	}

	/** Convert a Pluma ID to a Pluma PluginInfo object
	 * @param mioType The Pluma ID of the desired MIO PluginInfo.
	 * @return The PluginInfo object, or null if the type was not found
	 */
	public static PluginInfo getMIOPluginInfo(String mioType) {
		return getMioTypes().get(mioType);
	}

	/** Create a new MIO object for a given type as specified by a Pluma ID. Usually, MIGroup.add(Object) should be
	 * used instead of this method, because it does not throw exceptions.
	 * @param mioType The Pluma ID of the desired MIO type
	 * @return a new object of the given MIO type.
	 * @throws RuntimeException if the MIO type was not found.
	 */
	public static MIType newMIO(String mioType) {
		PluginInfo pli;
		if ((pli=getMioTypes().get(mioType))==null)
			throw new RuntimeException("Can not create new MIO because the MIType \""+mioType+"\" was not found.");
		return (MIType)pli.newInstance();
	}

	
	/** Move a MIGroup to a new location in the tree.
	 * @param mg The group to move
	 * @param newPath The path of the parent directory to move to (will be created, if necessary)
	 */
	public void moveGroupInTree(MIGroup mg, String newPath) {
		String oldPath = treeRoot.getPathFor(mg);
		Directory mgnode = treeRoot.getDirectory(oldPath, false);
		String parentPath = oldPath.substring(0, oldPath.lastIndexOf("/"));
		Directory parentnode = treeRoot.getDirectory(parentPath, false);
		parentnode.getSubDirs().remove(mgnode);
		Directory newParent = treeRoot.getDirectory(newPath, true);
		newParent.putDirectory(mgnode);
		fireMIManagerChanged(MIManagerEvent.OVERALL_CHANGE);
	}


	/** Add a MIGroupListener that will be notified whenever a given object is added to or removed from a 
	 * MIGroup in this MIManager. The MIGroupListener must implement the getWatchedObject() method.
	 * @param listener The listener to add.
	 */
	public void addListenerForObject( MIGroupListener listener ) {
		objectListenerList.add(MIGroupListener.class, listener);	  
	}

	/** Remove a MIGroupListener for a given object.
	 * @param listener The listener to remove.
	 */
	public void removeListenerForObject( MIGroupListener listener ) {
		objectListenerList.remove(MIGroupListener.class, listener);
	}

	/** Add a MIManagerListener that will be notified whenever a group is added, removed or renamed.
	 * @param listener The listener to add
	 */
	public void addMIManagerListener( MIManagerListener listener ) {
		eventfirer.addListener(listener);
	}


	/** Remove a MIManagerListener
	 * @param listener the listener to remove
	 */
	public void removeMIManagerListener( MIManagerListener listener ) {
		eventfirer.removeListener(listener);
	}


	protected void fireMIManagerChanged( int change )
	{
		eventfirer.fireEvent(new MIManagerEvent(this, change));
	}

	public Object getWatchedObject() {
		return null;
	}


	protected void fireMIGroupChanged( MIGroupEvent miGroupEvent )
	{
		// guaranteed to return a non-null array
		Object[] l_listeners = this.objectListenerList.getListenerList();

		if (l_listeners.length==0)
			return;

		// process the listeners last to first, notifying
		// those that are interested in this event
		for ( int i = l_listeners.length-2; i >= 0; i-=2 )  {
			if ( l_listeners[i] == MIGroupListener.class )  {
				MIGroupListener list = ((MIGroupListener)l_listeners[i+1]);
				if (list.getWatchedObject()==null || list.getWatchedObject()==miGroupEvent.getMioExtendable())
					list.miGroupChanged( miGroupEvent );
			}
		}
	}

	public void miGroupChanged(MIGroupEvent event) {
		fireMIGroupChanged(event);
	}


	/** Called by Pluma
	 * @see mayday.core.pluma.AbstractPlugin#init()
	 */
	@Override
	public void init() {
	}


	@SuppressWarnings("unchecked")
	@Override
	public PluginInfo register() throws PluginManagerException {
		return new PluginInfo(
				(Class)this.getClass(),
				"PAS.core.MIManager",
				new String[]{},
				Constants.MC_CORE,
				new HashMap<String, Object>(),
				"Florian Battke",
				"battke@informatik.uni-tuebingen.de",
				"Meta Information Object Manager",
				"MIO Manager"
		);
	}


	public void run() {
		//empty
	}



}
