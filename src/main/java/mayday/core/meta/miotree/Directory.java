package mayday.core.meta.miotree;

import java.util.Iterator;
import java.util.LinkedList;
import java.util.TreeSet;

import mayday.core.meta.MIGroup;
import mayday.core.meta.MIManager;

@SuppressWarnings("unchecked")
public class Directory implements Comparable {
	public String Name;
	private TreeSet< Directory > subDirs = new TreeSet< Directory >();
	MIGroup thisGroup;
	MIManager Manager;
	Directory Parent;
	
	public Directory(String name, Directory parent, MIManager manager) {
		Name = name;
		Parent = parent;
		Manager=manager;
	}
	
	public Directory(MIGroup theGroup, Directory parent) {
		this(theGroup.getName(), parent, theGroup.getMIManager());
		thisGroup = theGroup;
	}
	
	public Directory(String name, MIManager manager) {
		this(name, null, manager);
	}
	
	public Directory(MIGroup theGroup) {
		this(theGroup, null);
	}
	
	public TreeSet<Directory> getSubDirs() {
		return subDirs;
	}
	
	private Directory getSubDirByName(String Name) {
		for (Directory d : subDirs) 
			if (d.getName().equals(Name))
				return d;
		return null;
	}
	
	public Directory getDirectory(String subPath, boolean createPath) {
		if (subPath.startsWith("/"))
			subPath = subPath.substring(1);
		if (subPath.equals("")) return this;
		String nextSubPath;
		String nextPath;
		if (subPath.contains("/")) {
			nextPath = subPath.substring(subPath.indexOf("/")+1);
			nextSubPath = subPath.substring(0,subPath.indexOf("/"));
		} else {
			nextSubPath = subPath;
			nextPath = "";
		}
		Directory nextRoot = getSubDirByName(nextSubPath);
		if (nextRoot==null && createPath) {
			nextRoot = new Directory(nextSubPath, this, Manager);
			putDirectory(nextRoot);
		}
		if (nextRoot!=null) {
			return nextRoot.getDirectory(nextPath, createPath);
		}
		return null;			
	}
	
	public MIGroup getGroup() {
		return thisGroup;
	}	 
	
	public void setGroup(MIGroup theGroup) {
		//changeCount++;
		thisGroup = theGroup;
	}
	
	public void putDirectory(Directory dir) {
		//changeCount++;
		dir.Parent=this;
		DirectoryNamer.ensureNameUniqueness(dir);
		subDirs.add(dir);
	}
	
	public MIManager getManager() {		
		return Manager;
	}
	
	public Directory getParent() {
		return Parent;
	}
	
	private String toString_internal(String indent) {
		StringBuilder res = new StringBuilder(indent+this.getName()+" ("+(thisGroup==null?"no group":thisGroup.hashCode())+")\n");
		for (Directory d : subDirs) 
			res.append(d.toString_internal(indent+" "));
		return res.toString();
	}
	
	public String toString() {
		return toString_internal("");
	}
	
	
	public FileIterator getFiles(boolean recursive) {
		return new FileIterator(recursive, this);
	}
	
	public FileRegexIterator getFiles(String regExp, boolean recursive) {
		return new FileRegexIterator(regExp, recursive, this);
	}
	
	// remove group wherever it appears
	public void removeGroup(MIGroup mg, boolean recursive) {
		if (thisGroup==mg)
			thisGroup=null;
		if (recursive) {
			LinkedList<Directory> removable = new LinkedList<Directory>();
			for (Directory d : subDirs) {
				d.removeGroup(mg, recursive);
				if (d.subDirs.size()==0 && d.getGroup()==null)
					removable.add(d);
			}
			for (Directory d : removable) 
				this.subDirs.remove(d);
		}
	}
	
	public void removeDirectory(Directory d) {
		Iterator<Directory> id = subDirs.iterator(); // need to compare objects, not names
		while (id.hasNext())
			if (id.next()==d)
				id.remove();
	}
	
	public String getName() {
		if (thisGroup!=null)
			Name = thisGroup.getName(); //update Name if changed
		return Name;
	}
	
	void setName(String name) {
		if (getName().equals(name))
			return;
		if (thisGroup!=null)
			thisGroup.setName(name);
		Name = name;
	}
	
	// returns ONLY the first path found
	public String getPathFor(MIGroup mg) {
		if (thisGroup==mg)
			return getName();
		for (Directory d : subDirs) { 
			String p = d.getPathFor(mg);
			if (p!=null) {
				return getName()+"/"+p;
			}
		}
		return null;
	}

	public int compareTo(Object o) {
		if (!(o instanceof Directory))
			return (-1);
		
		// Compare names for sorting, BUT when names are equal, compare hashes
		// to distinguish identical objects
		int ret = ((Directory)o).getName().compareTo(getName());
		if (ret==0)
			ret = new Integer(hashCode()).compareTo(o.hashCode());
		return ret; 
	}
	

}