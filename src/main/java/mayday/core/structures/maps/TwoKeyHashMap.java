package mayday.core.structures.maps;

import java.util.Collection;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.TreeMap;
import java.util.Map.Entry;

@SuppressWarnings({ "serial", "unchecked" })
public class TwoKeyHashMap<K1 extends Comparable, K2 extends Comparable, V> extends TreeMap<TwoKey<K1,K2>,V> {

	public boolean containsKey(K1 k1, K2 k2) {
		return containsKey(makeKey(k1,k2,false));
	}

	public V get(K1 k1, K2 k2) {
		return get(makeKey(k1,k2,true));
	}
	
	public Collection<V> getAll(K1 k1, K2 k2) {
		LinkedList<V> result = new LinkedList<V>();
		TwoKey<K1,K2> key = makeKey(k1,k2,true);
		for (Entry<TwoKey<K1, K2>, V> e : entrySet()) {
			if (key.equals(e.getKey()))
				result.add(e.getValue());			
		}
		return result;
	}

	public void remove(K1 k1, K2 k2) {
		if (k1==null || k2==null)
			throw new IllegalArgumentException("No Key element may be null in remove or put");
		remove(makeKey(k1,k2,false));
	}

	public void put(K1 k1, K2 k2, V v) {
		if (k1==null || k2==null)
			throw new IllegalArgumentException("No Key element may be null in remove or put");
		put(makeKey(k1,k2,false),v);
	}


	public void removeAll(K1 k1, K2 k2) {
//		System.out.print("RemoveAll: Before "+size());
		TwoKey<K1,K2> key;
		key = makeKey(k1, k2, true);
		Iterator<Entry<TwoKey<K1, K2>, V>> it = entrySet().iterator();
		while (it.hasNext())
			if (key.equals(it.next().getKey()))
				it.remove();
//		System.out.println(" after "+size());
	}
	
	public TwoKey<K1, K2> makeKey(K1 k1, K2 k2, boolean allowNull) {
		if (allowNull)
			return new NonNullOnly_TwoKey<K1, K2>(k1, k2);
		else
			return new TwoKey<K1, K2>(k1, k2);
	}
	
	@SuppressWarnings("hiding")
	protected class NonNullOnly_TwoKey<K1 extends Comparable, K2 extends Comparable> extends TwoKey<K1,K2> {

		public NonNullOnly_TwoKey(K1 k1, K2 k2) {
			super(k1, k2);
		}
		
		public int compareTo(TwoKey<K1,K2> o) {
			if (k1==null)
				return k2.compareTo(o.k2);
			if (k2==null)
				return k1.compareTo(o.k1);
			return super.compareTo(o);
		}

	}
		
}

