package mayday.core.structures.maps;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import java.util.Map.Entry;

@SuppressWarnings("unchecked")

public class MultiTreeMap<K extends Comparable, V> extends TreeMap<K, Object> {

	private static final long serialVersionUID = 1L;
	
	protected int contentCount=0;
	
	protected LinkedList<V> cast(Object o) {
		return (LinkedList<V>)o;
	}

	public List<V> get(K key) {
		Object o = super.get(key);
		List<V> ret=Collections.EMPTY_LIST;
		if (o!=null) {
			if (o instanceof LinkedList) {
				ret=Collections.unmodifiableList(cast(o));
			} else {
				ArrayList llv = new ArrayList<V>(1); // less overhead than linkedlist
				llv.add((V)o);
				ret = Collections.unmodifiableList(llv);
			}
		}			
		return ret;
	}
	
	@Override
	public Object put(K key, Object valueobj) {
		V value = (V)valueobj;
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
		return null;
	}
	
	public void putReplace(K key, Collection<V> values) {
		
		List<V> previous = get(key);
		contentCount-=previous.size();
		
		LinkedList<V> copy = new LinkedList<V>(values);
		super.put(key, copy);
		contentCount+=values.size();
	}
	
		
	public void remove(K key, V value) {
		Object o = super.get(key);
		if (o!=null) {
			if (o instanceof LinkedList) {
				if (cast(o).contains(value)) {
					--contentCount;
					cast(o).remove(value);
					if (cast(o).isEmpty())
						super.remove(key);
				}
			} else {
				super.remove(key);
				--contentCount;
			}
		}					
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
		MultiTreeMap<K, V> clone = new MultiTreeMap<K, V>();
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
	
    public boolean equals(Object o) {
    	if (o == this)
    	    return true;

    	if (!(o instanceof MultiTreeMap))
    	    return false;
    	
    	MultiTreeMap<K,V> m = (MultiTreeMap<K,V>) o;
    	if (m.size() != size())
    	    return false;

    	try {
    		Iterator<Entry<K,Object>> i = entrySet().iterator();
    		while (i.hasNext()) {
    			Entry<K,Object> e = i.next();
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
