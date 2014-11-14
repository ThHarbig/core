package mayday.core.structures.maps;

import java.util.Collection;
import java.util.Map;
import java.util.Set;

public class DefaultValueMap<K, V> implements Map<K,V> {

	Map<K,V> internalmap;
	V Default;
	
	public DefaultValueMap(Map<K,V> wrappedMap, V DefaultValue) {
		internalmap=wrappedMap;
		Default = DefaultValue;
	}


	public void clear() {
		internalmap.clear();		
	}

	public boolean containsKey(Object key) {
		return internalmap.containsKey(key);
	}

	public boolean containsValue(Object value) {
		return internalmap.containsValue(value);
	}

	public Set<java.util.Map.Entry<K, V>> entrySet() {
		return internalmap.entrySet();
	}

	public V get(Object key) {
		V tmp = internalmap.get(key);
		if (tmp==null)
			return Default;
		return tmp;
	}

	public boolean isEmpty() {
		return internalmap.isEmpty();
	}

	public Set<K> keySet() {
		return internalmap.keySet();
	}

	public void putAll(Map<? extends K, ? extends V> t) {
		internalmap.putAll(t);	
	}

	public V remove(Object key) {
		return internalmap.remove(key);
	}

	public int size() {
		return internalmap.size();
	}

	public Collection<V> values() {
		return internalmap.values();
	}


	public V put(K key, V value) {
		return internalmap.put(key, value);
	}
	
	public String toString() {
		return internalmap.toString();
	}

	
}
