package mayday.core.structures.maps;


@SuppressWarnings({"serial","unchecked"})
public class PairHashMap<K extends Comparable, V> extends TwoKeyHashMap<K,K,V> {

	@Override
	public TwoKey<K, K> makeKey(K k1, K k2, boolean allowNull) {
		if (k2==null) // special case for comparisons
			return new WildcardTwoKey<K>(k1);
		if (k1==null) 
			return new WildcardTwoKey<K>(k2);
		if (k1.compareTo(k2)<0)
			return super.makeKey(k1, k2, allowNull);
		else
			return super.makeKey(k2, k1, allowNull);
	}
	
	public void remove(K key) {
		// make sure NULL is always on the right
		removeAll(key, null);
	}
	
	
	@SuppressWarnings("hiding")
	protected class WildcardTwoKey<K extends Comparable> extends TwoKey<K,K> {

		public WildcardTwoKey(K k1) {
			super(k1, null);
		}
		
		public int compareTo(TwoKey<K,K> o) {
			if (k1.compareTo(o.k1)==0 || k1.compareTo(o.k2)==0)
				return 0;
			return super.compareTo(o);
		}

	}

}
