package mayday.core.structures.maps;

import java.util.Collection;
import java.util.TreeMap;

public class AllWayMap<K> extends TreeMap<K, Collection<K>> {
	
	private static final long serialVersionUID = 1L;

	public void put(Collection<K> things) {
		// add all entries pointing to this collection
		for (K thing : things) { 
			put(thing, things);
		}			
	}
	
	public Collection<K> remove(Object key) {
		// remove this key
		Collection<K> things = super.remove(key);
		// remove all pointers to this key in values
		things.remove(key);
		return null;
	}
	
	public void removeComplete(K key) {
		Collection<K> things = get(key);
		if (things!=null)
			for (K thing : things)
				super.remove(thing);		
	}
	
}
