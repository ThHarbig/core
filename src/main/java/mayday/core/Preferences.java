package mayday.core;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.util.prefs.BackingStoreException;

import mayday.core.io.StorageNode;

public class Preferences extends StorageNode {

// ===== STATIC PART OF THE CLASS ===========================================================================
	
	protected final static String BackingFileName = System.getProperty("user.home","")+File.separatorChar+".mayday.prefs";

	protected static Preferences userRoot;
	protected static boolean unsaved = false;
	protected static DelayedUpdateTask prefSaver = new PrefSaverTask();
	
	public static Preferences userRoot() {
		if (userRoot == null) {
			userRoot = new Preferences("User ROOT","");			
			try {
				userRoot.loadFrom(new BufferedReader(new FileReader(BackingFileName)));
			} catch (FileNotFoundException e) {
				// assume new installation -> empty prefs
			} catch (Exception e) {				
				System.err.println("Could not read preferences: "+e+"\n"+e.getMessage());
			}
		}
		return userRoot;
	}
	
	protected static void flush0() throws BackingStoreException  {
		if (!unsaved)
			return;
		try {
			Preferences ur = userRoot();
			ur.saveTo(new BufferedWriter(new FileWriter(BackingFileName)));
		} catch (Exception e) {
			throw new BackingStoreException("Could not store preferences: "+e+"\n"+e.getMessage());
		}
		unsaved = false;
	}
	
	protected static class PrefSaverTask extends DelayedUpdateTask {

		public PrefSaverTask() {
			super("Saving preferences", 10000);
		}

		protected boolean needsUpdating() {
			return unsaved;
		}

		protected void performUpdate() {
			try {
				flush0();
			} catch (BackingStoreException e) {
				System.err.println("Preferences could not be saved!");
				e.printStackTrace();
			}
		}
		
	};
	
	public static Preferences createUnconnectedPrefTree(String k, String v) {
		Preferences ret = new Preferences(k,v);
		ret.isConnectedToUserRoot = false;
		return ret;
	}

	
// ===== INSTANCE SPECIFIC PART OF THE CLASS =================================================================	
	
	protected boolean isConnectedToUserRoot = true; // true if this preference node starts a subtree connected to userRoot;
	
	protected Preferences(String k, String v) {
		super(k,v);
	}	
	
	protected void setUnsaved() {
		if (isConnectedToUserRoot) {
			unsaved = true;
			prefSaver.trigger();
		}
	}
	
	public void connectSubtree(Preferences subTree) {
		subTree.isConnectedToUserRoot = this.isConnectedToUserRoot;
		children.put(subTree.Name, subTree);
		setUnsaved();
	}
	
	public Preferences node(String name) {
		Preferences n = (Preferences)children.get(name);
		if (n==null) {
			n = new Preferences(name, "");
			children.put(name, n);
			setUnsaved();
		}
		return n;			
	}
		
	public void put(String key, String value) {
		Preferences vNode = node(key);
		boolean unsaved = !vNode.Value.equals(value);
		vNode.Value = value;
		if (unsaved) 
			setUnsaved();
	}
	
	public void putInt(String key, Integer value) {
		put(key, ""+value);
	}
	
	public void putBoolean(String key, Boolean value) {
		put(key, ""+value);
	}
	
	public String get(String key, String defaultValue) {
		Preferences n = (Preferences)children.get(key);
		if (n==null)
			return defaultValue;
		return n.Value;
	}
	
	public Integer getInt(String key, Integer defaultValue) {
		try {
			return Integer.parseInt(get(key, ""+defaultValue));
		} catch (Exception e) {
			return defaultValue;
		}
	}
	
	public Boolean getBoolean(String key, Boolean defaultValue) {
		try {
			return Boolean.parseBoolean(get(key, ""+defaultValue));
		} catch (Exception e) {
			return defaultValue;
		}
	}
	
	public void clear() {
		if (children.size()>0) {
			children.clear();
			setUnsaved();
		}
	}
	
	public String[] keys() {
		return children.keySet().toArray(new String[0]);
	}
	
	public void remove(String key) {
		if (children.containsKey(key)) {
			children.remove(key);
			setUnsaved();
		}
	}
		
	public void flush() throws  BackingStoreException {
		if (isConnectedToUserRoot)
			flush0();
	}
	
	public String toString() {
		return Name;
	}
	
	protected StorageNode createEmptyChild() {
		return new Preferences(null,null);
	}

	
}
