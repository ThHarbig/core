package mayday.core.structures.maps;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

@SuppressWarnings("unchecked")

public class MultiHashMap<K, V> extends HashMap<K, Object> {

	private static final long serialVersionUID = 1L;
	
	protected int contentCount=0;
	
	protected LinkedList<V> cast(Object o) {
		return (LinkedList<V>)o;
	}

	public List<V> get(Object key) {
		return get((K)key, true);
	}
		
	/** if the Key is of class object, the normal get(K) method is ambiguous because the return type
	 * is different in MultiHashmap than that inherited from Hashmap. Thus you can use this method
	 * to force the compiler to use the right method.
	 * @param key the key to fetch values for
	 * @param forceAmbiguity doesn't matter what you put in, true or false
	 * @return the values for the given key or an empty list if there aren't any.
	 */
	public List<V> get(K key, boolean forceAmbiguity) {
		Object o = super.get(key);
		List<V> ret=Collections.EMPTY_LIST;
		if (o!=null) {
			if (o instanceof LinkedList) {
				ret=Collections.unmodifiableList(cast(o));
			} else {
				ArrayList<V> llv = new ArrayList<V>(1); // less overhead than linkedlist
				llv.add((V)o);
				ret = Collections.unmodifiableList(llv);
			}
		}			
		return ret;
	}
	
	public void put_unambigous(K key, V value) {
		Object o = super.get(key);
		if (o==null) {
			++contentCount;
			super.put(key, value);
		} else {
			if (o instanceof LinkedList) {
				if (!cast(o).contains(value)) {
					++contentCount;
					cast(o).add(value);
				}
			} else {
				if(!value.equals(o)) {
					LinkedList<V> ov = new LinkedList<V>();
					ov.add((V)o);
					ov.add(value);					
					super.put(key, ov);
					++contentCount;
				}
			}
		}		
	}
	
	public Object put(Object key, Object value) {
		put_unambigous((K)key, (V)value);
		return null;
	}
	
	public void putReplace(K key, Collection<V> values) {
		
		List<V> previous = get(key);
		contentCount-=previous.size();
		
		LinkedList<V> copy = new LinkedList<V>(values);
		super.put(key, copy);
		contentCount+=values.size();
	}
	
	public boolean remove(Object key, Object value) {
		Object o = super.get(key);
		if (o!=null) {
			if (o instanceof LinkedList) {
				if (cast(o).contains(value)) {
					--contentCount;
					cast(o).remove(value);
					if (cast(o).isEmpty()) {
						super.remove(key);
					}
					return true;
				}
			} else {
				super.remove(key);
				--contentCount;
				return true;
			}
		}
		return false;
	}
	
	public int size_everything() {
		if (contentCount<0) {
			contentCount=0;			
			for (Object o : values()) {
				if (o instanceof LinkedList) {
					contentCount += cast(o).size();
				}
				else 
					++contentCount;
			}
		}
		return contentCount;
	}
	
	public Collection<V> everything() {
		LinkedList<V> ret = new LinkedList<V>();
		for (Object o : values()) {
			if (o instanceof LinkedList)
				ret.addAll(cast(o));
			else 
				ret.add((V)o);
		}
		return ret;
	}

	protected void internalput(K key, Object o) {
		super.put(key, o);
	}
	
	public Object clone() {
		MultiHashMap<K, V> clone = new MultiHashMap<K, V>();
		for (Map.Entry<K,Object> e : entrySet()) {
			Object v = e.getValue();
			if (v instanceof LinkedList) {
				LinkedList<V> lv = new LinkedList<V>();
				lv.addAll((LinkedList<V>)v);
				clone.internalput(e.getKey(), lv);
			}
			else clone.internalput(e.getKey(), v);
		}
		return clone;
	}
	
	@SuppressWarnings("rawtypes")
	public boolean equals(Object o) {
	    	if (o == this)
	    	    return true;

	    	if (!(o instanceof MultiHashMap))
	    	    return false;
	    	
	    	MultiHashMap<K,V> m = (MultiHashMap<K,V>) o;
	    	if (m.size() != size())
	    	    return false;

	    	try {
	    		Iterator<Map.Entry<K,Object>> i = entrySet().iterator();
	    		while (i.hasNext()) {
	    			Map.Entry<K,Object> e = i.next();
	    			K key = e.getKey();
	    			Object value = e.getValue();
	    			if (value == null) {
	    				if (!(m.get(key)==null && m.containsKey(key)))
	    					return false;
	    			} else {
	    				if (value instanceof LinkedList) {
	    					Object othervalue = m.get(key);
	    					if (othervalue!=value) {
	    						if (othervalue==null) // we know value!=null
	    							return false;
	    						// quickly check for equality
	    						if (!(othervalue instanceof List))
	    							return false;
	    						if (((List)othervalue).size()!=((List)value).size())
	    							return false;
	    						// check elements independent of sort order
	    						LinkedList tmp = new LinkedList(((List)value));
	    						tmp.removeAll(((List)othervalue));
	    						if (!tmp.isEmpty())
	    							return false;
	    					}
	    				} else {
	    					if (value.equals(m.get(key)))
	    						return false;
	    				}
	    			}    			
	    		}
	    	} catch (ClassCastException unused) {
	    		return false;
	    	} catch (NullPointerException unused) {
	    		return false;
	    	}

	    	return true;
	    }

	
}
